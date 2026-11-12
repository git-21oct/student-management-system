import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class StudentManager {
    private ArrayList<Student> students = new ArrayList<>();
    private final String fileName = "students.txt";

    public StudentManager() {
        loadFromFile();
    }

    public void addStudent(Student s) {
        if (findById(s.getId()) != null) {
            System.out.println("A student with this ID already exists.");
            return;
        }
        students.add(s);
        if (!saveToFile()) {
            students.remove(s);                      // undo: keep list and file consistent
            System.out.println("Student was NOT added because the file could not be saved.");
            return;
        }
        System.out.println("Student added successfully.");
    }

    public void viewAll() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public void updateStudent(int id, String name, int age, String course) {
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        // remember old values so we can undo
        String oldName = s.getName();
        int oldAge = s.getAge();
        String oldCourse = s.getCourse();

        try {
            s.setName(name);
            s.setAge(age);
            s.setCourse(course);
        } catch (IllegalArgumentException e) {
            restore(s, oldName, oldAge, oldCourse);  // undo any partial change
            System.out.println("Update failed: " + e.getMessage());
            return;
        }

        if (!saveToFile()) {
            restore(s, oldName, oldAge, oldCourse);
            System.out.println("Student was NOT updated because the file could not be saved.");
            return;
        }
        System.out.println("Student updated successfully.");
    }

    public void deleteStudent(int id) {
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        int index = students.indexOf(s);
        students.remove(index);
        if (!saveToFile()) {
            students.add(index, s);                  // undo: put it back in the same place
            System.out.println("Student was NOT deleted because the file could not be saved.");
            return;
        }
        System.out.println("Student deleted successfully.");
    }

    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    private void restore(Student s, String name, int age, String course) {
        s.setName(name);
        s.setAge(age);
        s.setCourse(course);
    }

    // ---------- File handling ----------

    // Returns true only if the data was really written.
    // Writes to a temporary file first, then replaces the real file,
    // so a failed save never corrupts the existing data file.
    private boolean saveToFile() {
        File temp = new File(fileName + ".tmp");
        try (PrintWriter writer = new PrintWriter(new FileWriter(temp))) {
            for (Student s : students) {
                writer.println(s.toFileString());
            }
            if (writer.checkError()) {               // PrintWriter hides IOExceptions
                throw new IOException("Write operation failed.");
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            temp.delete();
            return false;
        }

        try {
            Files.move(temp.toPath(), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            temp.delete();
            return false;
        }
    }

    // Each line is handled on its own: a bad record is skipped and reported,
    // and loading continues with the next line.
    private void loadFromFile() {
        File file = new File(fileName);
        if (!file.exists()) return;

        int loaded = 0;
        int skipped = 0;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                try {
                    Student s = Student.fromFileString(line);
                    if (findById(s.getId()) != null) {
                        throw new IllegalArgumentException("Duplicate ID " + s.getId() + ".");
                    }
                    students.add(s);
                    loaded++;
                } catch (IllegalArgumentException e) {
                    skipped++;
                    System.out.println("Skipped line " + lineNumber + " (" + e.getMessage() + "): " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        System.out.println("Loaded " + loaded + " record(s)"
                + (skipped > 0 ? ", skipped " + skipped + " invalid record(s)." : "."));
    }
}

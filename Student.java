public class Student {
    private int id;
    private String name;
    private int age;
    private String course;

    // The constructor uses the validating setters, so an invalid
    // Student object can never be created.
    public Student(int id, String name, int age, String course) {
        setId(id);
        setName(name);
        setAge(age);
        setCourse(course);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }

    // ---------- model-level validation ----------
    private void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        // commas would break the comma-separated file format
        this.name = name.trim().replace(",", " ");
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be greater than 0.");
        }
        this.age = age;
    }

    public void setCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course cannot be empty.");
        }
        this.course = course.trim().replace(",", " ");
    }

    // One line in the file: id,name,age,course
    public String toFileString() {
        return id + "," + name + "," + age + "," + course;
    }

    // Throws IllegalArgumentException (NumberFormatException is a subclass)
    // if the line is malformed or holds invalid values.
    public static Student fromFileString(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields but found " + p.length + ".");
        }
        return new Student(Integer.parseInt(p[0].trim()), p[1], Integer.parseInt(p[2].trim()), p[3]);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Age: " + age + " | Course: " + course;
    }
}

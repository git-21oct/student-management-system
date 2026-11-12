import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        int choice;

        do {
            System.out.println("\n--- Student Record Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }

            switch (choice) {
                case 1:
                    int id = readPositiveInt("Enter ID: ");
                    while (manager.findById(id) != null) {
                        System.out.println("ID already exists. Please enter a different ID.");
                        id = readPositiveInt("Enter ID: ");
                    }
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine().trim().replace(",", " ");
                    int age = readPositiveInt("Enter Age: ");
                    System.out.print("Enter Course: ");
                    String course = sc.nextLine().trim().replace(",", " ");
                    try {
                        manager.addStudent(new Student(id, name, age, course));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Student not added: " + e.getMessage());
                    }
                    break;
                case 2:
                    manager.viewAll();
                    break;
                case 3:
                    int uid = readPositiveInt("Enter ID of student to update: ");
                    System.out.print("Enter new Name: ");
                    String newName = sc.nextLine().trim().replace(",", " ");
                    int newAge = readPositiveInt("Enter new Age: ");
                    System.out.print("Enter new Course: ");
                    String newCourse = sc.nextLine().trim().replace(",", " ");
                    manager.updateStudent(uid, newName, newAge, newCourse);
                    break;
                case 4:
                    manager.deleteStudent(readPositiveInt("Enter ID of student to delete: "));
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);

        sc.close();
    }

    // Keeps asking until the user enters a number greater than 0
    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Value must be greater than 0. Negative numbers are not allowed.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    private static final int PAGE_SIZE = 20;
    private static final Scanner in = new Scanner(System.in);
    private static final StudentRepository repo = new StudentRepository("data/students.csv");

    public static void main(String[] args) {
        try {
            repo.load();
        } catch (IOException e) {
            System.out.println("Could not read data/students.csv: " + e.getMessage());
            return;
        }
        System.out.println("Student Management System - " + repo.all().size() + " students loaded");

        while (true) {
            System.out.println();
            System.out.println("1. View all students");
            System.out.println("2. Search by name, roll number or email");
            System.out.println("3. Filter by department");
            System.out.println("4. Add a student");
            System.out.println("5. Update a student");
            System.out.println("6. Delete a student");
            System.out.println("7. Statistics");
            System.out.println("0. Save and exit");
            String choice = ask("Choose an option: ");
            switch (choice) {
                case "1": showPaged(repo.all()); break;
                case "2": showPaged(repo.search(ask("Search for: "))); break;
                case "3": showPaged(repo.byDepartment(ask("Department: "))); break;
                case "4": addStudent(); break;
                case "5": updateStudent(); break;
                case "6": deleteStudent(); break;
                case "7": showStatistics(); break;
                case "0":
                    save();
                    System.out.println("Saved. Goodbye!");
                    return;
                default: System.out.println("Please enter a number from 0 to 7.");
            }
        }
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private static int askInt(String prompt, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(ask(prompt));
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }

    private static double askCgpa(String prompt) {
        while (true) {
            try {
                double v = Double.parseDouble(ask(prompt));
                if (v >= 0 && v <= 10) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Enter a number from 0 to 10.");
        }
    }

    private static String askRequired(String prompt) {
        String v = ask(prompt);
        while (v.isEmpty() || v.contains(",")) {
            System.out.println("This field is required and cannot contain a comma.");
            v = ask(prompt);
        }
        return v;
    }

    private static void showPaged(List<Student> list) {
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        int pages = (list.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 0; page < pages; page++) {
            System.out.printf("%n%-9s %-22s %-24s %-4s %-5s %s%n",
                    "Roll no", "Name", "Department", "Year", "CGPA", "City");
            int from = page * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, list.size());
            for (Student s : list.subList(from, to)) {
                System.out.println(s);
            }
            System.out.printf("Showing %d-%d of %d%n", from + 1, to, list.size());
            if (page < pages - 1 && !ask("Press Enter for the next page, or type q to stop: ").isEmpty()) {
                return;
            }
        }
    }

    private static void addStudent() {
        String roll = askRequired("Roll number: ");
        if (repo.findByRoll(roll) != null) {
            System.out.println("Roll number " + roll + " already exists.");
            return;
        }
        String name = askRequired("Full name: ");
        String email = askRequired("Email: ");
        String phone = ask("Phone: ").replace(",", "");
        String gender = ask("Gender (Male/Female/Other): ").replace(",", "");
        String dob = ask("Date of birth (YYYY-MM-DD): ").replace(",", "");
        String dept = askRequired("Department: ");
        int year = askInt("Year (1-4): ", 1, 4);
        double cgpa = askCgpa("CGPA (0-10): ");
        String city = ask("City: ").replace(",", "");
        int admission = askInt("Admission year: ", 1990, 2100);
        repo.add(new Student(roll, name, email, phone, gender, dob, dept, year, cgpa, city, admission));
        save();
        System.out.println("Added " + name + ".");
    }

    private static void updateStudent() {
        Student s = repo.findByRoll(ask("Roll number to update: "));
        if (s == null) {
            System.out.println("No student with that roll number.");
            return;
        }
        System.out.println("Current: " + s);
        System.out.println("Press Enter to keep the current value.");
        String v = ask("Name [" + s.getName() + "]: ");
        if (!v.isEmpty() && !v.contains(",")) s.setName(v);
        v = ask("Email [" + s.getEmail() + "]: ");
        if (!v.isEmpty() && !v.contains(",")) s.setEmail(v);
        v = ask("Phone [" + s.getPhone() + "]: ");
        if (!v.isEmpty() && !v.contains(",")) s.setPhone(v);
        v = ask("Department [" + s.getDepartment() + "]: ");
        if (!v.isEmpty() && !v.contains(",")) s.setDepartment(v);
        v = ask("Year [" + s.getYear() + "]: ");
        if (!v.isEmpty()) {
            try {
                int y = Integer.parseInt(v);
                if (y >= 1 && y <= 4) s.setYear(y); else System.out.println("Year unchanged (must be 1-4).");
            } catch (NumberFormatException e) {
                System.out.println("Year unchanged (not a number).");
            }
        }
        v = ask("CGPA [" + s.getCgpa() + "]: ");
        if (!v.isEmpty()) {
            try {
                double c = Double.parseDouble(v);
                if (c >= 0 && c <= 10) s.setCgpa(c); else System.out.println("CGPA unchanged (must be 0-10).");
            } catch (NumberFormatException e) {
                System.out.println("CGPA unchanged (not a number).");
            }
        }
        v = ask("City [" + s.getCity() + "]: ");
        if (!v.isEmpty() && !v.contains(",")) s.setCity(v);
        save();
        System.out.println("Saved changes to " + s.getName() + ".");
    }

    private static void deleteStudent() {
        String roll = ask("Roll number to delete: ");
        Student s = repo.findByRoll(roll);
        if (s == null) {
            System.out.println("No student with that roll number.");
            return;
        }
        if (ask("Delete " + s.getName() + " (" + s.getRollNo() + ")? Type yes to confirm: ").equalsIgnoreCase("yes")) {
            repo.delete(roll);
            save();
            System.out.println("Deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private static void showStatistics() {
        List<Student> all = repo.all();
        if (all.isEmpty()) {
            System.out.println("No students yet.");
            return;
        }
        System.out.println("Total students: " + all.size());
        System.out.printf("Average CGPA: %.2f%n", all.stream().mapToDouble(Student::getCgpa).average().orElse(0));

        System.out.printf("%n%-24s %-9s %s%n", "Department", "Students", "Avg CGPA");
        Map<String, List<Student>> byDept =
                all.stream().collect(Collectors.groupingBy(Student::getDepartment, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<Student>> e : byDept.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Student::getCgpa).average().orElse(0);
            System.out.printf("%-24s %-9d %.2f%n", e.getKey(), e.getValue().size(), avg);
        }

        System.out.println("\nTop 5 by CGPA:");
        all.stream()
                .sorted((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()))
                .limit(5)
                .forEach(System.out::println);
    }

    private static void save() {
        try {
            repo.save();
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }
}

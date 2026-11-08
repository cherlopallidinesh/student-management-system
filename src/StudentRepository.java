import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Keeps all students in memory and saves them to a CSV file. */
public class StudentRepository {
    private static final String HEADER =
            "roll_no,name,email,phone,gender,dob,department,year,cgpa,city,admission_year";

    private final Path file;
    private final List<Student> students = new ArrayList<>();

    public StudentRepository(String path) {
        this.file = Paths.get(path);
    }

    public void load() throws IOException {
        students.clear();
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {      // line 0 is the header
            if (!lines.get(i).isBlank()) {
                students.add(Student.fromCsv(lines.get(i)));
            }
        }
    }

    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Student s : students) {
            lines.add(s.toCsv());
        }
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public List<Student> all() {
        return students;
    }

    public Student findByRoll(String rollNo) {
        for (Student s : students) {
            if (s.getRollNo().equalsIgnoreCase(rollNo)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> search(String text) {
        String t = text.toLowerCase();
        return students.stream()
                .filter(s -> s.getName().toLowerCase().contains(t)
                        || s.getRollNo().toLowerCase().contains(t)
                        || s.getEmail().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Student> byDepartment(String department) {
        return students.stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    /** Returns false if the roll number is already used. */
    public boolean add(Student s) {
        if (findByRoll(s.getRollNo()) != null) {
            return false;
        }
        students.add(s);
        return true;
    }

    public boolean delete(String rollNo) {
        Student s = findByRoll(rollNo);
        return s != null && students.remove(s);
    }
}

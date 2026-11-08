public class Student {
    private String rollNo, name, email, phone, gender, dob, department, city;
    private int year, admissionYear;
    private double cgpa;

    public Student(String rollNo, String name, String email, String phone, String gender,
                   String dob, String department, int year, double cgpa, String city,
                   int admissionYear) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dob = dob;
        this.department = department;
        this.year = year;
        this.cgpa = cgpa;
        this.city = city;
        this.admissionYear = admissionYear;
    }

    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public double getCgpa() { return cgpa; }
    public String getCity() { return city; }
    public int getAdmissionYear() { return admissionYear; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year) { this.year = year; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public void setCity(String city) { this.city = city; }

    /** One line of students.csv. */
    public String toCsv() {
        return String.join(",", rollNo, name, email, phone, gender, dob, department,
                String.valueOf(year), String.valueOf(cgpa), city, String.valueOf(admissionYear));
    }

    public static Student fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Student(p[0], p[1], p[2], p[3], p[4], p[5], p[6],
                Integer.parseInt(p[7]), Double.parseDouble(p[8]), p[9], Integer.parseInt(p[10]));
    }

    @Override
    public String toString() {
        return String.format("%-9s %-22s %-24s %-4d %-5.2f %s",
                rollNo, name, department, year, cgpa, city);
    }
}

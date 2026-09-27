import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class CGPACalculator {

    static Scanner sc = new Scanner(System.in);

    // Map Grade Letter to its corresponding Grade Point
    public static double getGradePoint(String grade) {
        switch (grade.toUpperCase()) {
            case "O": return 10.0;
            case "A+": return 9.0;
            case "A": return 8.0;
            case "B+": return 7.0;
            case "B": return 6.0;
            case "C": return 5.0;
            case "RA": return 0.0;
            default: return -1.0; // Invalid grade
        }
    }

    // 1. Add Student Profile
    public static void addStudent() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Register Number (Student ID): ");
            String studentId = sc.nextLine();
            System.out.print("Enter Student Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Department: ");
            String dept = sc.nextLine();

            String sql = "INSERT INTO student (student_id, name, department) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, dept);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student profile added successfully!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Enter Grade for Course
    // 2. Enter Grade for Course
    public static void addGrade() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Register Number (Student ID): ");
            String studentId = sc.nextLine().trim();
            System.out.print("Enter Semester Number (1 or 2): ");
            int semNo = sc.nextInt();
            sc.nextLine(); // consume newline

            // Show available subjects for that semester from curriculum
            PreparedStatement subPs = con.prepareStatement("SELECT * FROM curriculum_courses WHERE semester_no = ?");
            subPs.setInt(1, semNo);
            ResultSet subRs = subPs.executeQuery();

            System.out.println("\n--- AVAILABLE SUBJECTS FOR SEMESTER " + semNo + " ---");
            boolean hasSubjects = false;
            while (subRs.next()) {
                hasSubjects = true;
                System.out.println("Code: " + subRs.getString("course_code") + 
                                   " | Title: " + subRs.getString("course_title") + 
                                   " | Credits: " + subRs.getInt("credits"));
            }

            if (!hasSubjects) {
                System.out.println("No subjects found for this semester in the database!");
                con.close();
                return;
            }

            System.out.print("\nEnter Course Code to record grade (e.g., CS3251): ");
            String courseCode = sc.nextLine().trim();
            System.out.print("Enter Grade Letter Received (O, A+, A, B+, B, C, RA): ");
            String gradeLetter = sc.nextLine().trim();

            double gradePoint = getGradePoint(gradeLetter);
            if (gradePoint == -1.0) {
                System.out.println("Invalid Grade Letter entered! Please use O, A+, A, B+, B, C, or RA.");
                con.close();
                return;
            }

            String sql = "INSERT INTO student_grades (student_id, course_code, grade_letter, grade_point) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            ps.setString(3, gradeLetter.toUpperCase());
            ps.setDouble(4, gradePoint);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Grade recorded successfully! Assigned Grade Point: " + gradePoint);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 3. Calculate Semester SGPA
    public static void calculateSGPA() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Register Number (Student ID): ");
            String studentId = sc.nextLine();
            System.out.print("Enter Semester Number for SGPA: ");
            int semNo = sc.nextInt();
            sc.nextLine();

            String sql = "SELECT SUM(c.credits * g.grade_point) AS total_points, SUM(c.credits) AS total_credits " +
                         "FROM student_grades g " +
                         "JOIN curriculum_courses c ON g.course_code = c.course_code " +
                         "WHERE g.student_id = ? AND c.semester_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setInt(2, semNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double totalPoints = rs.getDouble("total_points");
                int totalCredits = rs.getInt("total_credits");

                if (totalCredits > 0) {
                    double sgpa = totalPoints / totalCredits;
                    System.out.printf("\n--> Semester %d SGPA: %.2f\n", semNo, sgpa);
                } else {
                    System.out.println("No recorded grades found for this semester.");
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Calculate Overall CGPA
    public static void calculateCGPA() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Register Number (Student ID): ");
            String studentId = sc.nextLine();

            String sql = "SELECT SUM(c.credits * g.grade_point) AS total_points, SUM(c.credits) AS total_credits " +
                         "FROM student_grades g " +
                         "JOIN curriculum_courses c ON g.course_code = c.course_code " +
                         "WHERE g.student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double totalPoints = rs.getDouble("total_points");
                int totalCredits = rs.getInt("total_credits");

                if (totalCredits > 0) {
                    double cgpa = totalPoints / totalCredits;
                    System.out.printf("\n--> Overall Cumulative CGPA: %.2f\n", cgpa);
                } else {
                    System.out.println("No grade records found for this student.");
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main Menu Loop
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== PERI CSE CGPA & GPA CALCULATOR =====");
            System.out.println("1. Add Student Profile");
            System.out.println("2. Enter Grade for Course");
            System.out.println("3. Calculate Semester SGPA");
            System.out.println("4. Calculate Overall CGPA");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: addStudent(); break;
                case 2: addGrade(); break;
                case 3: calculateSGPA(); break;
                case 4: calculateCGPA(); break;
                case 5: 
                    System.out.println("Thank You!");
                    System.exit(0);
                default: 
                    System.out.println("Invalid Choice!");
            }
        }
    }
}
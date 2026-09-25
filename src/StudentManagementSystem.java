import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class StudentManagementSystem {

    static Scanner sc = new Scanner(System.in);

    // 1. Add Student
    public static void addStudent() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Registration Number: ");
            int regnum = sc.nextInt();
            sc.nextLine(); // Consume newline

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Department: ");
            String dept = sc.nextLine();

            System.out.print("Enter Mark: ");
            double mark = sc.nextDouble();

            String sql = "INSERT INTO student (regnum, name, dept, mark) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, regnum);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setDouble(4, mark);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student added successfully!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View Students
    public static void viewStudents() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM student";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- STUDENT DETAILS ---");
            System.out.println("RegNum | Name  | Dept | Mark");
            System.out.println("----------------------------");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("regnum") + "  | " +
                    rs.getString("name") + " | " +
                    rs.getString("dept") + " | " +
                    rs.getDouble("mark")
                );
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Search Student
    public static void searchStudent() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Registration Number to Search: ");
            int regnum = sc.nextInt();

            String sql = "SELECT * FROM student WHERE regnum = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, regnum);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nStudent Found!");
                System.out.println("RegNum : " + rs.getInt("regnum"));
                System.out.println("Name   : " + rs.getString("name"));
                System.out.println("Dept   : " + rs.getString("dept"));
                System.out.println("Mark   : " + rs.getDouble("mark"));
            } else {
                System.out.println("Student not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Update Student Mark
    public static void updateStudent() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Registration Number to Update: ");
            int regnum = sc.nextInt();

            System.out.print("Enter New Mark: ");
            double mark = sc.nextDouble();

            String sql = "UPDATE student SET mark = ? WHERE regnum = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, mark);
            ps.setInt(2, regnum);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student mark updated successfully!");
            } else {
                System.out.println("Student not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Delete Student
    public static void deleteStudent() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Registration Number to Delete: ");
            int regnum = sc.nextInt();

            String sql = "DELETE FROM student WHERE regnum = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, regnum);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main Menu Loop
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n----- STUDENT MANAGEMENT SYSTEM -----");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student Mark");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewStudents(); break;
                case 3: searchStudent(); break;
                case 4: updateStudent(); break;
                case 5: deleteStudent(); break;
                case 6: 
                    System.out.println("Thank You!");
                    System.exit(0);
                default: 
                    System.out.println("Invalid Choice!");
            }
        }
    }
}
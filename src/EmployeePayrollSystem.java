import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class EmployeePayrollSystem {

    static Scanner sc = new Scanner(System.in);

    // 1. Add Employee
    public static void addEmployee() {
        try {
            Connection con = DBConnection.getConnection();

            System.out.print("Enter ID: ");
            int id = sc.nextInt();
            sc.nextLine(); // Consume newline

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Department: ");
            String dept = sc.nextLine();

            System.out.print("Enter Basic Salary: ");
            double salary = sc.nextDouble();

            String sql = "INSERT INTO employee (id, name, dept, basic_salary) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setDouble(4, salary);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Employee added successfully!");
            }

            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View Employees
    public static void viewEmployees() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM employee";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n--- EMPLOYEE DETAILS ---");
            System.out.println("ID  | Name  | Dept | Basic Salary");
            System.out.println("---------------------------------");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("dept") + " | " +
                    rs.getDouble("basic_salary")
                );
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Search Employee
    public static void searchEmployee() {
        try {
            Connection con = DBConnection.getConnection();

            System.out.print("Enter Employee ID to search: ");
            int id = sc.nextInt();

            String sql = "SELECT * FROM employee WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nEmployee Found!");
                System.out.println("ID           : " + rs.getInt("id"));
                System.out.println("Name         : " + rs.getString("name"));
                System.out.println("Department   : " + rs.getString("dept"));
                System.out.println("Basic Salary : " + rs.getDouble("basic_salary"));
            } else {
                System.out.println("Employee not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Update Salary
    public static void updateSalary() {
        try {
            Connection con = DBConnection.getConnection();

            System.out.print("Enter Employee ID to update: ");
            int id = sc.nextInt();

            System.out.print("Enter New Basic Salary: ");
            double salary = sc.nextDouble();

            String sql = "UPDATE employee SET basic_salary = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, salary);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Salary updated successfully!");
            } else {
                System.out.println("Employee not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Calculate Salary (HRA, DA, Deduction, Net Salary)
    public static void calculateSalary() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Employee ID for Salary Calculation: ");
            int id = sc.nextInt();

            String sql = "SELECT * FROM employee WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double basic = rs.getDouble("basic_salary");

                // Calculations
                double hra = basic * 0.20;       // 20% HRA
                double da = basic * 0.10;        // 10% DA
                double deduction = basic * 0.05; // 5% Deduction
                double netSalary = (basic + hra + da) - deduction;

                System.out.println("\n===== SALARY SLIP =====");
                System.out.println("Employee ID   : " + id);
                System.out.println("Name          : " + name);
                System.out.println("Basic Salary  : " + basic);
                System.out.println("HRA (20%)     : " + hra);
                System.out.println("DA (10%)      : " + da);
                System.out.println("Deduction (5%): " + deduction);
                System.out.println("-------------------------");
                System.out.println("Net Salary    : " + netSalary);
                System.out.println("=========================");
            } else {
                System.out.println("Employee not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. View Department Employees
    public static void viewDepartmentEmployees() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Department Name (e.g., CSE, HR): ");
            String dept = sc.next();

            String sql = "SELECT * FROM employee WHERE dept = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dept);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- EMPLOYEES IN DEPARTMENT: " + dept.toUpperCase() + " ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | Basic Salary: " + rs.getDouble("basic_salary"));
            }
            if (!found) {
                System.out.println("No employees found in this department.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 7. Generate Salary Report (Practice: SUM, AVG, MAX, MIN, GROUP BY, HAVING)
    public static void generateSalaryReport() {
        try {
            Connection con = DBConnection.getConnection();
            // Using aggregate functions and GROUP BY / HAVING clauses
            String sql = "SELECT dept, COUNT(id) AS total_emp, SUM(basic_salary) AS total_payroll, " +
                         "AVG(basic_salary) AS avg_salary, MAX(basic_salary) AS max_salary, " +
                         "MIN(basic_salary) AS min_salary FROM employee GROUP BY dept HAVING AVG(basic_salary) >= 30000";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n====================== DEPARTMENT SALARY REPORT ======================");
            System.out.println("Dept | Total Employees | Total Payroll | Avg Salary | Max Salary | Min Salary");
            System.out.println("----------------------------------------------------------------------");
            
            while (rs.next()) {
                System.out.println(
                    rs.getString("dept") + "  | " +
                    rs.getInt("total_emp") + "               | " +
                    rs.getDouble("total_payroll") + "  | " +
                    rs.getDouble("avg_salary") + "  | " +
                    rs.getDouble("max_salary") + "  | " +
                    rs.getDouble("min_salary")
                );
            }
            System.out.println("======================================================================");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main Menu Loop
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n----- EMPLOYEE PAYROLL MANAGEMENT SYSTEM -----");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Search Employee");
            System.out.println("4. Update Salary");
            System.out.println("5. Calculate Salary");
            System.out.println("6. View Department Employees");
            System.out.println("7. Generate Salary Report");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewEmployees();
                    break;
                case 3:
                    searchEmployee();
                    break;
                case 4:
                    updateSalary();
                    break;
                case 5:
                    calculateSalary();
                    break;
                case 6:
                    viewDepartmentEmployees();
                    break;
                case 7:
                    generateSalaryReport();
                    break;
                case 8:
                    System.out.println("Thank you for using the system!");
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice! Please enter a number between 1 and 8.");
            }
        }
    }
}
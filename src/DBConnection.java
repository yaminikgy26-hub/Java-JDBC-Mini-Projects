// import java.sql.Connection;
// import java.sql.DriverManager;

// public class DBConnection {
//     static String URL = "jdbc:mysql://localhost:3306/PayrollSystem";
//     static String username = "root"; // Update with your MySQL username if different
//     static String password = "Yamini@2627"; // Update with your MySQL password if any

//     public static Connection getConnection() {
//         Connection con = null;
//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             con = DriverManager.getConnection(URL, username, password);
//             System.out.println("Database connected successfully");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return con;
//     }
// }
// import java.sql.Connection;
// import java.sql.DriverManager;

// public class DBConnection {
//     static String URL = "jdbc:mysql://localhost:3306/BusBookingSystem";
//     static String username = "root"; 
//     static String password = "Yamini@2627"; // Update with your password

//     public static Connection getConnection() {
//         Connection con = null;
//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             con = DriverManager.getConnection(URL, username, password);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return con;
//     }
// }

// import java.sql.Connection;
// import java.sql.DriverManager;

// public class DBConnection {
//     static String URL = "jdbc:mysql://localhost:3306/StudentManagementDB";
//     static String username = "root"; 
//     static String password = "Yamini@2627"; // Update with your password

//     public static Connection getConnection() {
//         Connection con = null;
//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             con = DriverManager.getConnection(URL, username, password);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return con;
//     }
// }

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    static String URL = "jdbc:mysql://localhost:3306/CGPACalculatorDB";
    static String username = "root"; 
    static String password = "Yamini@2627"; // Update with your password

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
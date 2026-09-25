import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class BusTicketSystem {

    static Scanner sc = new Scanner(System.in);

    // 1. Add Passenger
    public static void addPassenger() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Age: ");
            int age = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            String sql = "INSERT INTO passenger (name, age, gender, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Passenger added successfully! Passenger ID: " + rs.getInt(1));
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View Buses
    public static void viewBuses() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM bus";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- AVAILABLE BUSES ---");
            while (rs.next()) {
                System.out.println(rs.getInt("bus_id") + " | Bus No: " + rs.getString("bus_number") + 
                                   " | " + rs.getString("source") + " → " + rs.getString("destination"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Search Bus
    public static void searchBus() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Source: ");
            String source = sc.nextLine();
            System.out.print("Enter Destination: ");
            String destination = sc.nextLine();

            String sql = "SELECT * FROM bus WHERE source = ? AND destination = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, source);
            ps.setString(2, destination);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("\nBus Found: ID: " + rs.getInt("bus_id") + 
                                   " | Bus No: " + rs.getString("bus_number") + 
                                   " | " + rs.getString("source") + " → " + rs.getString("destination"));
            }
            if (!found) System.out.println("No buses found for this route.");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Check Available Seats
    public static void checkSeats() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Bus ID to check seats: ");
            int busId = sc.nextInt();

            String sql = "SELECT seat_number, status FROM seat WHERE bus_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- SEAT STATUS (✓: Available, X: Booked) ---");
            while (rs.next()) {
                int seatNo = rs.getInt("seat_number");
                String status = rs.getString("status");
                String symbol = status.equals("Available") ? "✓" : "X";
                System.out.print("Seat " + seatNo + " [" + symbol + "]  ");
            }
            System.out.println("\n--------------------------------------------");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Book Ticket
    public static void bookTicket() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Passenger ID: ");
            int passengerId = sc.nextInt();
            System.out.print("Enter Bus ID: ");
            int busId = sc.nextInt();
            System.out.print("Enter Seat Number to Book: ");
            int seatNumber = sc.nextInt();

            // Check seat availability
            String checkSql = "SELECT seat_id, status FROM seat WHERE bus_id = ? AND seat_number = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setInt(1, busId);
            checkPs.setInt(2, seatNumber);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                int seatId = rs.getInt("seat_id");

                if (status.equals("Booked")) {
                    System.out.println("Seat Not Available!");
                } else {
                    // Update seat to Booked
                    PreparedStatement updateSeat = con.prepareStatement("UPDATE seat SET status = 'Booked' WHERE seat_id = ?");
                    updateSeat.setInt(1, seatId);
                    updateSeat.executeUpdate();

                    // Insert into booking
                    PreparedStatement bookPs = con.prepareStatement("INSERT INTO booking (passenger_id, bus_id, seat_id) VALUES (?, ?, ?)");
                    bookPs.setInt(1, passengerId);
                    bookPs.setInt(2, busId);
                    bookPs.setInt(3, seatId);
                    bookPs.executeUpdate();

                    System.out.println("Ticket Booked Successfully!");
                }
            } else {
                System.out.println("Invalid Seat Number!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. Cancel Ticket
    public static void cancelTicket() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Booking ID to cancel: ");
            int bookingId = sc.nextInt();

            // Get seat_id associated with the booking
            PreparedStatement getSeat = con.prepareStatement("SELECT seat_id FROM booking WHERE booking_id = ? AND status = 'Confirmed'");
            getSeat.setInt(1, bookingId);
            ResultSet rs = getSeat.executeQuery();

            if (rs.next()) {
                int seatId = rs.getInt("seat_id");

                // Free the seat
                PreparedStatement freeSeat = con.prepareStatement("UPDATE seat SET status = 'Available' WHERE seat_id = ?");
                freeSeat.setInt(1, seatId);
                freeSeat.executeUpdate();

                // Cancel booking
                PreparedStatement cancelBooking = con.prepareStatement("UPDATE booking SET status = 'Cancelled' WHERE booking_id = ?");
                cancelBooking.setInt(1, bookingId);
                cancelBooking.executeUpdate();

                System.out.println("Ticket Cancelled Successfully!");
            } else {
                System.out.println("Active booking not found with this ID.");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 7. View Booking
    public static void viewBooking() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Passenger ID: ");
            int passengerId = sc.nextInt();

            String sql = "SELECT * FROM booking WHERE passenger_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, passengerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- BOOKINGS FOR PASSENGER " + passengerId + " ---");
            while (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("booking_id") + 
                                   " | Bus ID: " + rs.getInt("bus_id") + 
                                   " | Seat ID: " + rs.getInt("seat_id") + 
                                   " | Status: " + rs.getString("status"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 8. Generate Ticket (Invoice)
    public static void generateTicket() {
        try {
            Connection con = DBConnection.getConnection();
            System.out.print("Enter Booking ID to generate ticket: ");
            int bookingId = sc.nextInt();

            String sql = "SELECT b.booking_id, p.name, p.phone, bu.bus_number, bu.source, bu.destination, s.seat_number, b.booking_date, b.status " +
                         "FROM booking b " +
                         "JOIN passenger p ON b.passenger_id = p.passenger_id " +
                         "JOIN bus bu ON b.bus_id = bu.bus_id " +
                         "JOIN seat s ON b.seat_id = s.seat_id " +
                         "WHERE b.booking_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n==================================");
                System.out.println("          BUS E-TICKET            ");
                System.out.println("==================================");
                System.out.println("Booking ID  : " + rs.getInt("booking_id"));
                System.out.println("Passenger   : " + rs.getString("name"));
                System.out.println("Phone       : " + rs.getString("phone"));
                System.out.println("Bus Number  : " + rs.getString("bus_number"));
                System.out.println("Route       : " + rs.getString("source") + " → " + rs.getString("destination"));
                System.out.println("Seat Number : " + rs.getInt("seat_number"));
                System.out.println("Booked On   : " + rs.getTimestamp("booking_date"));
                System.out.println("Status      : " + rs.getString("status"));
                System.out.println("==================================");
            } else {
                System.out.println("Booking not found!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main Loop
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n----- BUS TICKET BOOKING SYSTEM -----");
            System.out.println("1. Add Passenger");
            System.out.println("2. View Buses");
            System.out.println("3. Search Bus");
            System.out.println("4. Book Ticket");
            System.out.println("5. Check Available Seats");
            System.out.println("6. Cancel Ticket");
            System.out.println("7. View Booking");
            System.out.println("8. Generate Ticket");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: addPassenger(); break;
                case 2: viewBuses(); break;
                case 3: searchBus(); break;
                case 4: bookTicket(); break;
                case 5: checkSeats(); break;
                case 6: cancelTicket(); break;
                case 7: viewBooking(); break;
                case 8: generateTicket(); break;
                case 9: 
                    System.out.println("Thank You!");
                    System.exit(0);
                default: 
                    System.out.println("Invalid Choice!");
            }
        }
    }
}
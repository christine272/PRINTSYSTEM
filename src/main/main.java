package main;

import config.config;
import java.util.*;

public class main {

    private static config con = new config();

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            con.connectDB();

            while (true) {
                System.out.println("\n===== Welcome to Printing Shop System =====");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int initialChoice = scan.nextInt();
                scan.nextLine();

                if (initialChoice == 1) {
                    if (login(scan)) {
                        customerMenu(scan);
                    }
                } else if (initialChoice == 2) {
                    register(scan);
                } else if (initialChoice == 3) {
                    System.out.println("Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    
    private static boolean login(Scanner scan) {
        System.out.print("Enter email: ");
        String email = scan.nextLine();

        System.out.print("Enter password: ");
        String password = scan.nextLine();

        String sql = "SELECT * FROM tbl_user WHERE u_email = ? AND u_pass = ?";
        List<Map<String, Object>> result = con.fetchRecords(sql, email, password);

        if (result.isEmpty()) {
            System.out.println("Invalid credentials!");
            return false;
        }

        Map<String, Object> user = result.get(0);
        String status = user.get("u_status").toString();
        String type = user.get("u_type").toString();

        if (status.equalsIgnoreCase("Pending")) {
            System.out.println("Your account is pending approval. Please contact the admin.");
            return false;
        }

        System.out.println("Login successful! Welcome " + user.get("u_name"));

        if (type.equalsIgnoreCase("Admin")) {
            adminDashboard(scan);
        } else if (type.equalsIgnoreCase("Staff")) {
            staffDashboard(scan);
        }

        return true;
    }

    
    private static void register(Scanner scan) {
        System.out.print("Enter your name: ");
        String name = scan.nextLine();

        System.out.print("Enter your email: ");
        String email = scan.nextLine();

        // Check if email already exists
        while (true) {
            String checkSql = "SELECT * FROM tbl_user WHERE u_email = ?";
            List<Map<String, Object>> existing = con.fetchRecords(checkSql, email);
            if (existing.isEmpty()) {
                break;
            } else {
                System.out.print("Email already exists. Enter a different email: ");
                email = scan.nextLine();
            }
        }

        System.out.print("Choose user type (1 - Admin, 2 - Staff): ");
        int typeChoice = scan.nextInt();
        scan.nextLine();
        while (typeChoice < 1 || typeChoice > 2) {
            System.out.print("Invalid choice. Please enter 1 or 2: ");
            typeChoice = scan.nextInt();
            scan.nextLine();
        }
        String userType = (typeChoice == 1) ? "Admin" : "Staff";

        System.out.print("Enter your password: ");
        String password = scan.nextLine();

        String insertSql = "INSERT INTO tbl_user(u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
        con.addRecord(insertSql, name, email, userType, "Pending", password);

        System.out.println("Registration successful! Please wait for admin approval.");
    }

    
    private static void adminDashboard(Scanner scan) {
        while (true) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Approve User Accounts");
            System.out.println("2. Logout");
            System.out.print("Enter choice: ");
            int choice = scan.nextInt();
            scan.nextLine();

            if (choice == 1) {
                viewUsers();
                System.out.print("Enter User ID to approve: ");
                int userId = scan.nextInt();
                scan.nextLine();
                String updateSql = "UPDATE tbl_user SET u_status = 'Approved' WHERE u_id = ?";
                con.updateRecord(updateSql, userId);
                System.out.println("User approved.");
            } else if (choice == 2) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    
    private static void staffDashboard(Scanner scan) {
        System.out.println("\n--- Staff Dashboard ---");
        System.out.println("Currently no special functions for staff.");
    }

    
    private static void viewUsers() {
        String query = "SELECT * FROM tbl_user";
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        con.viewRecords(query, headers, columns);
    }

   
    private static void customerMenu(Scanner scan) {
        int choice;
        do {
            System.out.println("\n=== Customer Management Menu ===");
            System.out.println("1. Enter Customer Details");
            System.out.println("2. View Customer Details");
            System.out.println("3. Update Customer Details");
            System.out.println("4. Delete Customer Details");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = scan.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scan.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = scan.nextLine();
                    System.out.print("Enter Address: ");
                    String address = scan.nextLine();

                    String insertSql = "INSERT INTO user (name, email, phone, address) VALUES (?, ?, ?, ?)";
                    con.addRecord(insertSql, name, email, phone, address);
                    System.out.println("Customer details saved successfully!");
                    break;

                case 2:
                    System.out.println("\n--- Customer Details ---");
                    String query = "SELECT * FROM user";
                    String[] headers = {"ID", "Name", "Email", "Phone", "Address"};
                    String[] columns = {"u_id", "name", "email", "phone", "address"};
                    con.viewRecords(query, headers, columns);
                    break;

                case 3:
                    System.out.print("Enter Customer ID to update: ");
                    int id = scan.nextInt();
                    scan.nextLine();

                    System.out.print("Enter New Name: ");
                    String newName = scan.nextLine();
                    System.out.print("Enter New Email: ");
                    String newEmail = scan.nextLine();
                    System.out.print("Enter New Phone: ");
                    String newPhone = scan.nextLine();
                    System.out.print("Enter New Address: ");
                    String newAddress = scan.nextLine();

                    String updateSql = "UPDATE user SET name = ?, email = ?, phone = ?, address = ? WHERE u_id = ?";
                    con.updateRecord(updateSql, newName, newEmail, newPhone, newAddress, id);
                    System.out.println("Customer details updated successfully!");
                    break;

                case 4:
                    System.out.print("Enter Customer ID to delete: ");
                    int deleteId = scan.nextInt();
                    scan.nextLine();

                    String deleteSql = "DELETE FROM user WHERE u_id = ?";
                    con.deleteRecord(deleteSql, deleteId);
                    System.out.println("Customer details deleted successfully!");
                    break;

                case 5:
                    System.out.println("Logging out...");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1–5.");
            }
        } while (choice != 5);
    }
}

package main;

import config.config;
import java.util.Scanner;
import java.sql.SQLException;

public class Main {

    // Hardcoded credentials for demo (replace with DB-based auth in production)
    private static final String STAFF_USERNAME = "staff";
    private static final String STAFF_PASSWORD = "pass";
    private static final String OWNER_USERNAME = "owner";
    private static final String OWNER_PASSWORD = "admin";

    public static void main(String[] args) {
        config con = new config();
        try {
            con.connectDB();
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            return;
        }

        try (Scanner scan = new Scanner(System.in)) {
            // Login Process
            boolean loggedIn = false;
            int role = 0;  // 1 = Staff, 2 = Owner

            while (!loggedIn) {
                System.out.println("\n=== Welcome to Printing Shop System ===");
                System.out.println("Select your role:");
                System.out.println("1. Staff");
                System.out.println("2. Owner");
                System.out.print("Enter role (1 or 2): ");
                
                if (!scan.hasNextInt()) {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    scan.next();
                    continue;
                }
                role = scan.nextInt();
                scan.nextLine();
                
                if (role != 1 && role != 2) {
                    System.out.println("Invalid role. Please enter 1 for Staff or 2 for Owner.");
                    continue;
                }
                
                System.out.print("Enter Username: ");
                String username = scan.nextLine();
                
                System.out.print("Enter Password: ");
                String password = scan.nextLine();
                
                // Verify credentials
                if ((role == 1 && username.equals(STAFF_USERNAME) && password.equals(STAFF_PASSWORD)) ||
                    (role == 2 && username.equals(OWNER_USERNAME) && password.equals(OWNER_PASSWORD))) {
                    loggedIn = true;
                    System.out.println("Login successful! Welcome to the Printing Shop System.");
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            }

            // Role-based menus
            if (role == 1) {
                runStaffMenu(scan, con);
            } else if (role == 2) {
                runOwnerMenu(scan, con);
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            try {
                con.closeConnection();
            } catch (SQLException e) {
                System.out.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Staff Menu: Limited to adding materials and viewing
    private static void runStaffMenu(Scanner scan, config con) {
        int choice;
        do {
            System.out.println("\n=== Staff Menu ===");
            System.out.println("1. Add Printing Material/Supply");
            System.out.println("2. View Inventory");
            System.out.println("3. View Orders");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");
            
            if (!scan.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scan.next();
                continue;
            }
            choice = scan.nextInt();
            scan.nextLine();
            
            if (choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    System.out.print("Enter Material Name: ");
                    String name = scan.nextLine();
                    
                    System.out.print("Enter Quantity: ");
                    int quantity = scan.nextInt();
                    
                    System.out.print("Enter Price: ");
                    double price = scan.nextDouble();
                    scan.nextLine();
                    
                    String sql = "INSERT INTO materials (name, quantity, price) VALUES (?, ?, ?)";
                    try {
                        con.addMaterial(sql, name, quantity, price);  // Assume this method exists in config
                        System.out.println("Material added successfully!");
                    } catch (SQLException e) {
                        System.out.println("Error adding material: " + e.getMessage());
                    }
                    break;
                    
                case 2:
                    System.out.println("\n--- Inventory ---");
                    String invQuery = "SELECT * FROM inventory";
                    String[] invHeaders = {"ID", "Name", "Quantity", "Price"};
                    String[] invColumns = {"i_id", "name", "quantity", "price"};
                    try {
                        con.viewInventory(invQuery, invHeaders, invColumns);  // Assume this method exists
                    } catch (SQLException e) {
                        System.out.println("Error viewing inventory: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                    System.out.println("\n--- Orders ---");
                    String ordQuery = "SELECT * FROM orders";
                    String[] ordHeaders = {"ID", "Customer ID", "Material ID", "Quantity", "Status"};
                    String[] ordColumns = {"o_id", "customer_id", "material_id", "quantity", "status"};
                    try {
                        con.viewOrders(ordQuery, ordHeaders, ordColumns);  // Assume this method exists
                    } catch (SQLException e) {
                        System.out.println("Error viewing orders: " + e.getMessage());
                    }
                    break;
                    
                case 4:
                    System.out.println("Logging out. Goodbye!");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    // Owner Menu: Full access, including adapted Customer Menu
    private static void runOwnerMenu(Scanner scan, config con) {
        int choice;
        do {
            System.out.println("\n=== Owner Menu ===");
            System.out.println("1. Customer Menu (Manage Customers)");
            System.out.println("2. View Inventory");
            System.out.println("3. View Orders");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");
            
            if (!scan.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scan.next();
                continue;
            }
            choice = scan.nextInt();
            scan.nextLine();
            
            if (choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    runCustomerMenu(scan, con);  // Sub-menu for customer CRUD
                    break;
                    
                case 2:
                    System.out.println("\n--- Inventory ---");
                    String invQuery = "SELECT * FROM inventory";
                    String[] invHeaders = {"ID", "Name", "Quantity", "Price"};
                    String[] invColumns = {"i_id", "name", "quantity", "price"};
                    try {
                        con.viewInventory(invQuery, invHeaders, invColumns);
                    } catch (SQLException e) {
                        System.out.println("Error viewing inventory: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                    System.out.println("\n--- Orders ---");
                    String ordQuery = "SELECT * FROM orders";
                    String[] ordHeaders = {"ID", "Customer ID", "Material ID", "Quantity", "Status"};
                    String[] ordColumns = {"o_id", "customer_id", "material_id", "quantity", "status"};
                    try {
                        con.viewOrders(ordQuery, ordHeaders, ordColumns);
                    } catch (SQLException e) {
                        System.out.println("Error viewing orders: " + e.getMessage());
                    }
                    break;
                    
                case 4:
                    System.out.println("Logging out. Goodbye!");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    // Customer Menu: Adapted from original CRUD
    private static void runCustomerMenu(Scanner scan, config con) {
        int choice;
        do {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Enter Customer Details");
            System.out.println("2. View Customer Details");
            System.out.println("3. Update Customer Details");
            System.out.println("4. Delete Customer Details");
            System.out.println("5. Back to Owner Menu");
            System.out.print("Enter your choice (1-5): ");
            
            if (!scan.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                scan.next();
                continue;
            }
            choice = scan.nextInt();
            scan.nextLine();
            
            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, 4, or 5.");
                continue;
            }
            
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
                    
                    String sql = "INSERT INTO user (name, email, phone, address) VALUES (?, ?, ?, ?)";
                    try {
                        con.addRecord(sql, name, email, phone, address);
                        System.out.println("Customer details saved successfully!");
                    } catch (SQLException e) {
                        System.out.println("Error saving details: " + e.getMessage());
                    }
                    break;
                    
                case 2:
                    System.out.println("\n--- Customer Details ---");
                    String query = "SELECT * FROM user";
                    String[] headers = {"ID", "Name", "Email", "Phone", "Address"};
                    String[] columns = {"u_id", "name", "email", "phone", "address"};
                    try {
                        con.viewRecords(query, headers, columns);
                    } catch (SQLException e) {
                        System.out.println("Error retrieving details: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                    System.out.print("Enter Customer ID to update: ");
                    if (!scan.hasNextInt()) {
                        System.out.println("Invalid ID.");
                        scan.next();
                        break;
                    }
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
                    try {
                        con.updateRecord(updateSql, newName, newEmail, newPhone, newAddress, id);
                        System.out.println("Customer details updated successfully!");
                    } catch (SQLException e) {
                        System.out.println("Error updating details: " + e.getMessage());
                    }
                    break;
                    
                case 4:
                    System.out.print("Enter Customer ID to delete: ");
                    if (!scan.hasNextInt()) {
                        System.out.println("Invalid ID.");
                        scan.next();
                        break;
                    }
                    int deleteID = scan.nextInt();
                    scan.nextLine();
                    
                    String deleteSql = "DELETE FROM user WHERE u_id = ?";
                    try {
                        con.deleteRecord(deleteSql, deleteID);
                        System.out.println("Customer details deleted successfully!");
                    } catch (SQLException e) {
                        System.out.println("Error deleting details: " + e.getMessage());
                    }
                    break;
                    
                case 5:
                    System.out.println("Returning to Owner Menu.");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }
}

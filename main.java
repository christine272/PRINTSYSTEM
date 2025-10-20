package main;

import config.config;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            config con = new config();
            config.connectDB();  
            
            String name = "";
            String email = "";
            String phone = "";
            String address = "";
            
            int choice;
            
            do {
                System.out.println("\n=== Printing Shop Menu ===");
                System.out.println("1. Enter Customer Details");
                System.out.println("2. View Customer Details");
                System.out.println("3. Update Customer Details");
                System.out.println("4. Delete Customer Details");
                System.out.print("Enter your choice: ");
                choice = scan.nextInt();
                scan.nextLine();
                
                switch (choice) {
                    case 1:
                        System.out.print("Enter Name: ");
                        name = scan.nextLine();
                        
                        System.out.print("Enter Email: ");
                        email = scan.nextLine();
                        
                        System.out.print("Enter Phone: ");
                        phone = scan.nextLine();
                        
                        System.out.print("Enter Address: ");
                        address = scan.nextLine();
                        
                        String sql = "INSERT INTO user (name, email, phone, address) VALUES (?, ?, ?, ?)";
                        con.addRecord(sql, name, email, phone, address);
                        
                        System.out.println("Customer details saved successfully!");
                        break;
                        
                    case 2:
                        System.out.println("\n--- Customer Details ---");
                        
                        String query = "SELECT * FROM user";
                        String[] headers = {"ID", "Name", "Email", "Phone", "Address" };
                        String[] columns = {"u_id", "name", "email", "phone", "address" };
                        
                        con.viewRecords(query, headers, columns);
                        
                        break;
                        
                    case 3:
                        System.out.println("Enter Customer ID to update: ");
                        int id = scan.nextInt();
                        scan.nextLine();
                        
                        System.out.println("Enter New Name: ");
                        String newName = scan.nextLine();
                        
                        System.out.println("Enter New Email: ");
                        String newEmail = scan.nextLine();
                        
                        System.out.println("Enter New Phone: ");
                        String newPhone = scan.nextLine();
                        
                        System.out.println("Enter New Address: ");
                        String newAddress = scan.nextLine();
                        
                        String updateSql = "UPDATE user SET name = ?, email = ?, phone = ?, address = ? WHERE u_id = ?";
                        con.updateRecord(updateSql, newName, newEmail, newPhone, newAddress, id);
                        
                        break;
                        
                        
                    case 4:
                        System.out.println("Enter Customer ID to delete: ");
                        int deleteID = scan.nextInt();
                        scan.nextLine();
                        
                        String deleteSql = "DELETE FROM user WHERE u_id = ?";
                        con.deleteRecord(deleteSql, deleteID);
                        
                        break;
                        
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, 3 or 4.");
                }
                
            } while (choice != 4);
        }
    }  
} 
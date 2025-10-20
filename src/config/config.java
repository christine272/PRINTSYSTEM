package config;

import java.sql.*;
import java.util.*;

public class config {

    // -----------------------------------------------
    // CONNECT TO DATABASE
    // -----------------------------------------------
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            // Ensure your database path is correct
            con = DriverManager.getConnection("jdbc:sqlite:printing.db");         
        } catch (Exception e) {
        }
        return con;
    }

    // -----------------------------------------------
    // ADD RECORD
    // -----------------------------------------------
    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // FETCH RECORDS (used for login and view)
    // -----------------------------------------------
    public List<Map<String, Object>> fetchRecords(String sql, Object... values) {
        List<Map<String, Object>> records = new ArrayList<>();

        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    records.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching records: " + e.getMessage());
        }

        return records;
    }

    // -----------------------------------------------
    // VIEW RECORDS
    // -----------------------------------------------
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("--------------------------------------------------------------------------------");
            for (String header : columnHeaders) {
                System.out.printf("%-20s", header);
            }
            System.out.println("\n--------------------------------------------------------------------------------");

            while (rs.next()) {
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    System.out.printf("%-20s", value != null ? value : "");
                }
                System.out.println();
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // UPDATE RECORD
    // -----------------------------------------------
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // DELETE RECORD
    // -----------------------------------------------
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
            System.out.println("Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }
}

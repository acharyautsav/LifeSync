package databasePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing finance data in the database.
 * This class handles inserting, retrieving, and analyzing finance records.
 */
public class FinanceDatabase {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Ensures that the finance tables exist in the database.
     * This method is called when the FinanceTrackerView is initialized.
     */
    public static void ensureTableExists() {
        // The tables are already created by DbConnection, so we just verify they exist
        try (Connection conn = dbConnection.getConnection()) {
            // Check finance_summary table
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "finance_summary", null)) {
                if (!rs.next()) {
                    System.err.println("❌ finance_summary table does not exist");
                } else {
                    System.out.println("✓ finance_summary table exists");
                }
            }
            
            // Check expenses table
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "expenses", null)) {
                if (!rs.next()) {
                    System.err.println("❌ expenses table does not exist");
                } else {
                    System.out.println("✓ expenses table exists");
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error checking finance tables: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Saves finance summary to database.
     * 
     * @param totalIncome The total income
     * @param totalExpenses The total expenses
     * @param savingGoal The saving goal
     * @return true if the data was saved successfully, false otherwise
     */
    public static boolean saveFinanceSummary(double totalIncome, double totalExpenses, double savingGoal) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "INSERT INTO finance_summary (user_id, total_income, total_expenses, saving_goal) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Get current user ID from session
                int userId = UserSession.getInstance().getUserId();
                
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, totalIncome);
                pstmt.setDouble(3, totalExpenses);
                pstmt.setDouble(4, savingGoal);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Finance summary saved successfully");
                    return true;
                } else {
                    System.out.println("❌ Failed to save finance summary");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error saving finance summary: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Saves an expense to the database.
     * 
     * @param amount The expense amount
     * @param description The expense description
     * @param date The expense date
     * @return true if the expense was saved successfully, false otherwise
     */
    
    /**
     * Saves an expense to the database.
     * 
     * @param amount The expense amount
     * @param description The expense description
     * @param date The expense date
     * @return true if the expense was saved successfully, false otherwise
     */
    public static boolean saveExpense(double amount, String description, LocalDate date) {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot save expense.");
                return false;
            }
            
            // Debug the database structure
            try (ResultSet columns = conn.getMetaData().getColumns(null, null, "expenses", null)) {
                System.out.println("Columns in expenses table:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    System.out.println("- " + columnName);
                }
            }
            
            // Check if the category column exists
            boolean hasCategoryColumn = false;
            try (ResultSet columns = conn.getMetaData().getColumns(null, null, "expenses", "category")) {
                hasCategoryColumn = columns.next();
            }
            
            String query;
            if (hasCategoryColumn) {
                query = "INSERT INTO expenses (user_id, amount, category, description, expense_date) VALUES (?, ?, ?, ?, ?)";
            } else {
                query = "INSERT INTO expenses (user_id, amount, description, expense_date) VALUES (?, ?, ?, ?)";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, amount);
                
                if (hasCategoryColumn) {
                    pstmt.setString(3, "Uncategorized");
                    pstmt.setString(4, description);
                    pstmt.setDate(5, java.sql.Date.valueOf(date));
                } else {
                    pstmt.setString(3, description);
                    pstmt.setDate(4, java.sql.Date.valueOf(date));
                }
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Expense saved successfully for user ID: " + userId);
                    return true;
                } else {
                    System.out.println("❌ Failed to save expense");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error saving expense: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    
    
    public static boolean saveExpense(double amount, String category, String description, LocalDate expenseDate) {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot save expense.");
                return false;
            }
            
            String query = "INSERT INTO expenses (user_id, amount, category, description, expense_date) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, category);
                pstmt.setString(4, description);
                pstmt.setDate(5, java.sql.Date.valueOf(expenseDate));
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Expense saved successfully for user ID: " + userId);
                    return true;
                } else {
                    System.out.println("❌ Failed to save expense");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error saving expense: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves the latest finance summary from the database.
     * 
     * @return A map containing the finance summary details
     */
    public static Map<String, Object> getLatestFinanceSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", 0.0);
        summary.put("totalExpenses", 0.0);
        summary.put("savingGoal", 0.0);
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            String query = "SELECT total_income, total_expenses, saving_goal FROM finance_summary " +
                          "WHERE user_id = ? " +
                          "ORDER BY update_date DESC LIMIT 1";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        summary.put("totalIncome", rs.getDouble("total_income"));
                        summary.put("totalExpenses", rs.getDouble("total_expenses"));
                        summary.put("savingGoal", rs.getDouble("saving_goal"));
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving finance summary: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return summary;
    }
    
    /**
     * Retrieves all expenses from the database.
     * 
     * @return A list of maps containing expense details
     */
    public static List<Map<String, Object>> getAllExpenses() {
        List<Map<String, Object>> expenses = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot retrieve expenses.");
                return expenses;
            }
            
            // Check if the category column exists
            boolean hasCategoryColumn = false;
            try (ResultSet columns = conn.getMetaData().getColumns(null, null, "expenses", "category")) {
                hasCategoryColumn = columns.next();
            }
            
            String query;
            if (hasCategoryColumn) {
                query = "SELECT amount, category, description, expense_date FROM expenses " +
                       "WHERE user_id = ? " +
                       "ORDER BY expense_date DESC";
            } else {
                query = "SELECT amount, description, expense_date FROM expenses " +
                       "WHERE user_id = ? " +
                       "ORDER BY expense_date DESC";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> expense = new HashMap<>();
                        expense.put("amount", rs.getDouble("amount"));
                        expense.put("description", rs.getString("description"));
                        if (hasCategoryColumn) {
                            expense.put("category", rs.getString("category"));
                        } else {
                            expense.put("category", "Uncategorized");
                        }
                        expense.put("date", rs.getDate("expense_date").toLocalDate());
                        expenses.add(expense);
                    }
                    System.out.println("✓ Retrieved " + expenses.size() + " expenses for user ID: " + userId);
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving expenses: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return expenses;
    }
    
}
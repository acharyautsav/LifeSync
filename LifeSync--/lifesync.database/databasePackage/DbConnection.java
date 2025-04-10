package databasePackage;

import java.sql.*;
import javafx.scene.control.Alert;

/**
 * DbConnection - Central class for managing all database operations in LifeSync
 * Handles database creation, table creation with proper foreign keys, and connection management
 * This class can be executed independently to initialize the database structure
 */
public class DbConnection {
    // Database connection constants
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "lifesync";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "45683968";
    
    // Connection URLs
    private static final String MYSQL_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT;
    private static final String DB_URL = MYSQL_URL + "/" + DB_NAME;
    
    // Singleton instance
    private static DbConnection instance;
    
    /**
     * Main method to make this class executable on its own
     * Running this method will initialize the database and all tables
     */
    public static void main(String[] args) {
        System.out.println("=== LifeSync Database Initialization ===");
        System.out.println("Starting database setup process...");
        
        // Get the instance to trigger initialization
        DbConnection db = getInstance();
        
        System.out.println("\nVerifying database connection...");
        try (Connection conn = db.getConnection()) {
            System.out.println("✓ Successfully connected to database: " + DB_NAME);
            
            // Print database information
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            
            // List all tables
            System.out.println("\nVerifying tables in database:");
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("✓ Table exists: " + tableName);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Database initialization complete ===");
    }
    
    
    
    
    /**
     * Authenticate a user login
     * 
     * @param username The username
     * @param password The password
     * @return A map containing login result information
     */
    public java.util.Map<String, Object> authenticateUser(String username, String password) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", false);
        result.put("userId", -1);
        result.put("isAdmin", false);
        
        try {
            // Check for admin credentials (hardcoded)
            // Admin username: "admin", password: "admin" (hashed)
            // The hash below is for "admin" using SHA-256
            String adminUsername = "admin";
            String adminPasswordHash = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
            
            if (adminUsername.equals(username)) {
                // This is an admin login attempt, verify password
                String hashedInputPassword = hashPassword(password);
                
                if (adminPasswordHash.equals(hashedInputPassword)) {
                    // Admin login successful
                    result.put("success", true);
                    result.put("userId", 0); // Special admin ID
                    result.put("isAdmin", true);
                    System.out.println("✓ Admin login successful");
                    return result;
                } else {
                    // Admin password incorrect
                    System.out.println("❌ Admin password incorrect");
                    return result;
                }
            }
            
            // If not admin, proceed with regular user authentication
            Connection conn = getConnection();
            String query = "SELECT user_id, password FROM users WHERE username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        int userId = rs.getInt("user_id");
                        
                        if (password.equals(storedPassword)) {
                            result.put("success", true);
                            result.put("userId", userId);
                            result.put("isAdmin", false);
                        }
                    }
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            showError("Error during login authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * Hash a password using SHA-256
     * 
     * @param password The password to hash
     * @return The hashed password as a hex string
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    /**
     * Get singleton instance of DbConnection
     */
    public static DbConnection getInstance() {
        if (instance == null) {
            instance = new DbConnection();
        }
        return instance;
    }
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private DbConnection() {
        initializeDatabase();
    }
    
    /**
     * Get a connection to the database
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initialize the database and create all tables
     */
    private void initializeDatabase() {
        createDatabase();
        createUsersTable();
        createCompletedTaskTable();
        createFinanceTables();
        createMoodTable();
        createSleepDataTable();
        createPendingTasksTable();
        System.out.println("All tables have been successfully initialized!");
    }
    
    /**
     * Create the database if it doesn't exist
     */
    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDatabaseSQL);
            System.out.println("✓ Database '" + DB_NAME + "' created successfully or already exists");
            
        } catch (SQLException e) {
            showError("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create the users table if it doesn't exist
     */
    private void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        executeUpdate(createTableSQL, "✓ Users table created successfully or already exists");
    }
    
    /**
     * Create the completed_task table if it doesn't exist
     */
    private void createCompletedTaskTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS completed_tasks (" +
                "task_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "task_description TEXT NOT NULL, " +
                "completion_date DATE NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        executeUpdate(createTableSQL, "✓ Completed tasks table created successfully or already exists");
    }
    
    /**
     * Create the finance tables if they don't exist
     */
    private void createFinanceTables() {
        String createSummaryTableSQL = "CREATE TABLE IF NOT EXISTS finance_summary (" +
                "summary_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "total_income DOUBLE NOT NULL, " +
                "total_expenses DOUBLE NOT NULL, " +
                "saving_goal DOUBLE NOT NULL, " +
                "update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        String createExpensesTableSQL = "CREATE TABLE IF NOT EXISTS expenses (" +
                "expense_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "amount DOUBLE NOT NULL, " +
                "description VARCHAR(255) NOT NULL, " +
                "expense_date DATE NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        executeUpdate(createSummaryTableSQL, "✓ Finance summary table created successfully or already exists");
        executeUpdate(createExpensesTableSQL, "✓ Expenses table created successfully or already exists");
    }
    
    /**
     * Create the mood table if it doesn't exist
     */
    private void createMoodTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS mood_tracker (" +
                "mood_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "mood_description VARCHAR(50) NOT NULL, " +
                "mood_level INT NOT NULL, " +
                "record_date DATE NOT NULL, " +
                "notes TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        executeUpdate(createTableSQL, "✓ Mood tracker table created successfully or already exists");
    }
    
    /**
     * Create the sleep data table if it doesn't exist
     */
    private void createSleepDataTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS sleep_records (" +
                "sleep_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "sleeptime DATETIME NOT NULL, " +
                "waketime DATETIME NOT NULL, " +
                "sleep_hours INT NOT NULL, " +
                "sleep_minutes INT NOT NULL, " +
                "warningsleephour BOOLEAN DEFAULT FALSE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        executeUpdate(createTableSQL, "✓ Sleep records table created successfully or already exists");
    }
    
    /**
     * Create the pending tasks table if it doesn't exist
     */
    private void createPendingTasksTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS pending_tasks (" +
                "task_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "task_description TEXT NOT NULL, " +
                "due_date DATE, " +
                "priority INT DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        
        executeUpdate(createTableSQL, "✓ Pending tasks table created successfully or already exists");
    }
    
    /**
     * Helper method to execute SQL update statements
     */
    private void executeUpdate(String sql, String successMessage) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            System.out.println(successMessage);
            
        } catch (SQLException e) {
            showError("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to show error messages
     */
    private void showError(String message) {
        System.err.println(message);
        
        // If running in JavaFX application thread, show alert
        if (javafx.application.Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
    
    /**
     * Check if a user exists in the database
     */
    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            showError("Error checking if user exists: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user ID by username
     */
    public int getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
            
        } catch (SQLException e) {
            showError("Error getting user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1; // User not found
    }
    
    /**
     * Close resources safely
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test if the database connection is working
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            showError("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
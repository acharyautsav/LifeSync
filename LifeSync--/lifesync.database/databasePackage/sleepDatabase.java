package databasePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing sleep data in the database.
 * This class handles inserting, retrieving, and analyzing sleep records.
 */
public class sleepDatabase {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Ensures that the sleep_records table exists in the database.
     * This method is called when the SleepTrackerView is initialized.
     */
    public static void ensureTableExists() {
        // The table is already created by DbConnection, so we just verify it exists
        try (Connection conn = dbConnection.getConnection()) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "sleep_records", null)) {
                if (!rs.next()) {
                    System.err.println("❌ sleep_records table does not exist");
                } else {
                    System.out.println("✓ sleep_records table exists");
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error checking sleep_records table: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Inserts sleep data into the database.
     * 
     * @param sleepTime The time the user went to sleep
     * @param wakeupTime The time the user woke up
     * @param totalMinutes Total sleep duration in minutes
     * @param isWarning Whether the sleep duration is less than recommended
     * @return true if the data was inserted successfully, false otherwise
     */
    public boolean insertSleepData(LocalTime sleepTime, LocalTime wakeupTime, long totalMinutes, boolean isWarning) {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot save sleep record.");
                return false;
            }
            
            // First, let's check the structure of the sleep_records table
            try (ResultSet columns = conn.getMetaData().getColumns(null, null, "sleep_records", null)) {
                System.out.println("Columns in sleep_records table:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    System.out.println("- " + columnName);
                }
            }
            
            // Convert LocalTime to LocalDateTime for database storage
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sleepDateTime = LocalDateTime.of(now.toLocalDate(), sleepTime);
            LocalDateTime wakeupDateTime = LocalDateTime.of(now.toLocalDate(), wakeupTime);
            
            // If wakeup time is before sleep time, assume it's the next day
            if (wakeupTime.isBefore(sleepTime)) {
                wakeupDateTime = wakeupDateTime.plusDays(1);
            }
            
            // Calculate hours and minutes
            int hours = (int)(totalMinutes / 60);
            int minutes = (int)(totalMinutes % 60);
            
            // Using the correct column names based on the table structure
            String insertSql = "INSERT INTO sleep_records (user_id, sleeptime, waketime, sleep_hours, sleep_minutes, warningsleephour) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, userId); // Use the current user's ID from the session
                pstmt.setObject(2, sleepDateTime);
                pstmt.setObject(3, wakeupDateTime);
                pstmt.setInt(4, hours);
                pstmt.setInt(5, minutes);
                pstmt.setBoolean(6, isWarning);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Sleep data saved successfully for user ID " + userId + ": " + hours + "h " + minutes + "m");
                    return true;
                } else {
                    System.out.println("❌ Failed to save sleep data");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error saving sleep data: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves sleep records from the database for the current user.
     * 
     * @param limit Maximum number of records to retrieve
     * @return A list of maps containing sleep record details
     */
    public static List<Map<String, Object>> getSleepHistory(int limit) {
        List<Map<String, Object>> sleepHistory = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot retrieve sleep history.");
                return sleepHistory;
            }
            
            String query = "SELECT sleeptime, waketime, sleep_hours, sleep_minutes, warningsleephour, created_at " +
                           "FROM sleep_records " +
                           "WHERE user_id = ? " +
                           "ORDER BY created_at DESC LIMIT ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, limit);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> sleepRecord = new HashMap<>();
                        sleepRecord.put("sleepTime", rs.getObject("sleeptime", LocalDateTime.class));
                        sleepRecord.put("wakeTime", rs.getObject("waketime", LocalDateTime.class));
                        sleepRecord.put("sleepHours", rs.getInt("sleep_hours"));
                        sleepRecord.put("sleepMinutes", rs.getInt("sleep_minutes"));
                        sleepRecord.put("warningFlag", rs.getBoolean("warningsleephour"));
                        sleepRecord.put("createdAt", rs.getObject("created_at", LocalDateTime.class));
                        sleepHistory.add(sleepRecord);
                    }
                    System.out.println("✓ Retrieved " + sleepHistory.size() + " sleep records for user ID: " + userId);
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving sleep history: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return sleepHistory;
    }
}
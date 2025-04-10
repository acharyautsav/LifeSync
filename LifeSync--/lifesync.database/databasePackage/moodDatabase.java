package databasePackage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing mood data in the database.
 * This class handles inserting, retrieving, and analyzing mood records.
 */
public class moodDatabase {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Ensures that the mood_tracker table exists in the database.
     * This method is called when the MoodTrackerView is initialized.
     */
    public static void ensureTableExists() {
        // The table is already created by DbConnection, so we just verify it exists
        try (Connection conn = dbConnection.getConnection()) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "mood_tracker", null)) {
                if (!rs.next()) {
                    System.err.println("❌ mood_tracker table does not exist");
                } else {
                    System.out.println("✓ mood_tracker table exists");
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error checking mood_tracker table: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Saves a mood record to the database.
     * 
     * @param moodDetails The mood to save (e.g., "Happy", "Sad", etc.)
     * @return true if the mood was saved successfully, false otherwise
     */
    public static boolean saveMood(String moodDetails) {
        if (moodDetails == null || moodDetails.trim().isEmpty()) {
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot save mood record.");
                return false;
            }
            
            // Using the correct column names based on the table structure
            String insertSql = "INSERT INTO mood_tracker (user_id, mood_description, mood_level, record_date, notes, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, userId); // Use the current user's ID from the session
                pstmt.setString(2, moodDetails); // mood_description
                pstmt.setInt(3, getMoodLevel(moodDetails)); // mood_level (1-5 based on mood)
                pstmt.setDate(4, Date.valueOf(LocalDate.now())); // record_date
                pstmt.setString(5, "Recorded via MoodTracker"); // notes
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Mood saved successfully for user ID " + userId + ": " + moodDetails);
                    return true;
                } else {
                    System.out.println("❌ Failed to save mood: " + moodDetails);
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error saving mood: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Converts a mood description to a numeric level (1-5)
     * 
     * @param moodDescription The mood description
     * @return A numeric level from 1-5
     */
    private static int getMoodLevel(String moodDescription) {
        // Map common moods to levels (1-5, where 5 is the most positive)
        if (moodDescription == null) {
            return 3; // Neutral
        }
        
        String mood = moodDescription.toLowerCase();
        
        if (mood.contains("happy") || mood.contains("excited") || mood.contains("great")) {
            return 5;
        } else if (mood.contains("good") || mood.contains("content") || mood.contains("relaxed")) {
            return 4;
        } else if (mood.contains("neutral") || mood.contains("okay") || mood.contains("fine")) {
            return 3;
        } else if (mood.contains("sad") || mood.contains("tired") || mood.contains("down")) {
            return 2;
        } else if (mood.contains("angry") || mood.contains("stressed") || mood.contains("depressed")) {
            return 1;
        }
        
        // Default to neutral
        return 3;
    }
    
    /**
     * Retrieves all mood records from the database for the current user.
     * 
     * @return A list of maps containing mood details and recorded dates
     */
    public static List<Map<String, Object>> getMoodHistory() {
        List<Map<String, Object>> moodHistory = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot retrieve mood history.");
                return moodHistory;
            }
            
            String query = "SELECT mood_description, record_date, mood_level, notes FROM mood_tracker " +
                          "WHERE user_id = ? " +
                          "ORDER BY record_date DESC, created_at DESC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> moodRecord = new HashMap<>();
                        moodRecord.put("moodDetails", rs.getString("mood_description"));
                        moodRecord.put("recordedDate", rs.getDate("record_date").toLocalDate());
                        moodRecord.put("moodLevel", rs.getInt("mood_level"));
                        moodRecord.put("notes", rs.getString("notes"));
                        moodHistory.add(moodRecord);
                    }
                    System.out.println("✓ Retrieved " + moodHistory.size() + " mood records for user ID: " + userId);
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving mood history: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return moodHistory;
    }
}
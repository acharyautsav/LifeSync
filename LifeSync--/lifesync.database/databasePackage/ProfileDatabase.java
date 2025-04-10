package databasePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing user profile data in the database.
 * This class handles retrieving and updating user profile information.
 */
public class ProfileDatabase {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Ensures that the profiles table exists in the database.
     * This method is called when the ProfileView is initialized.
     */
    public static void ensureTableExists() {
        // The table is already created by DbConnection, so we just verify it exists
        try (Connection conn = dbConnection.getConnection()) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "profiles", null)) {
                if (!rs.next()) {
                    System.err.println("❌ profiles table does not exist");
                    
                    // Create the profiles table if it doesn't exist
                    String createTableSql = 
                        "CREATE TABLE profiles (" +
                        "profile_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "full_name VARCHAR(100), " +
                        "bio TEXT, " +
                        "profile_picture MEDIUMBLOB, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                        ")";
                    
                    try (PreparedStatement pstmt = conn.prepareStatement(createTableSql)) {
                        pstmt.executeUpdate();
                        System.out.println("✓ profiles table created successfully");
                    }
                } else {
                    System.out.println("✓ profiles table exists");
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error checking/creating profiles table: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Retrieves the profile information for the current user.
     * 
     * @return A map containing the user's profile information, or null if not found
     */
    public static Map<String, Object> getUserProfile() {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot retrieve profile.");
                return null;
            }
            
            // First, get basic user information from the users table
            String userQuery = "SELECT username, email, is_admin FROM users WHERE user_id = ?";
            Map<String, Object> profile = new HashMap<>();
            
            try (PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        profile.put("userId", userId);
                        profile.put("username", rs.getString("username"));
                        profile.put("email", rs.getString("email"));
                        profile.put("isAdmin", rs.getBoolean("is_admin"));
                    } else {
                        System.out.println("❌ User not found with ID: " + userId);
                        return null;
                    }
                }
            }
            
            // Next, get additional profile information from the profiles table
            String profileQuery = "SELECT full_name, bio, profile_picture FROM profiles WHERE user_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(profileQuery)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        profile.put("fullName", rs.getString("full_name"));
                        profile.put("bio", rs.getString("bio"));
                        profile.put("profilePicture", rs.getBytes("profile_picture"));
                    } else {
                        // No profile record yet, set defaults
                        profile.put("fullName", "");
                        profile.put("bio", "");
                        profile.put("profilePicture", null);
                    }
                }
            }
            
            System.out.println("✓ Retrieved profile for user ID: " + userId);
            return profile;
            
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving user profile: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Updates the profile information for the current user.
     * 
     * @param fullName The user's full name
     * @param bio The user's bio
     * @param profilePicture The user's profile picture (can be null)
     * @return true if the profile was updated successfully, false otherwise
     */
    public static boolean updateUserProfile(String fullName, String bio, byte[] profilePicture) {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot update profile.");
                return false;
            }
            
            // First check if profile exists
            String checkQuery = "SELECT COUNT(*) FROM profiles WHERE user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Profile exists, update it
                        String updateQuery = "UPDATE profiles SET full_name = ?, bio = ?";
                        
                        // Add profile picture to update if provided
                        if (profilePicture != null) {
                            updateQuery += ", profile_picture = ?";
                        }
                        
                        updateQuery += " WHERE user_id = ?";
                        
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, fullName);
                            updateStmt.setString(2, bio);
                            
                            int paramIndex = 3;
                            if (profilePicture != null) {
                                updateStmt.setBytes(paramIndex++, profilePicture);
                            }
                            
                            updateStmt.setInt(paramIndex, userId);
                            
                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("✓ Profile updated successfully for user ID: " + userId);
                                return true;
                            } else {
                                System.out.println("❌ Failed to update profile");
                                return false;
                            }
                        }
                    } else {
                        // Profile doesn't exist, create it
                        String insertQuery = "INSERT INTO profiles (user_id, full_name, bio";
                        
                        // Add profile picture to insert if provided
                        if (profilePicture != null) {
                            insertQuery += ", profile_picture";
                        }
                        
                        insertQuery += ") VALUES (?, ?, ?";
                        
                        // Add placeholder for profile picture if provided
                        if (profilePicture != null) {
                            insertQuery += ", ?";
                        }
                        
                        insertQuery += ")";
                        
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setString(2, fullName);
                            insertStmt.setString(3, bio);
                            
                            if (profilePicture != null) {
                                insertStmt.setBytes(4, profilePicture);
                            }
                            
                            int rowsAffected = insertStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("✓ Profile created successfully for user ID: " + userId);
                                return true;
                            } else {
                                System.out.println("❌ Failed to create profile");
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating user profile: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Updates the email address for the current user.
     * 
     * @param newEmail The new email address
     * @return true if the email was updated successfully, false otherwise
     */
    public static boolean updateEmail(String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty() || !newEmail.contains("@")) {
            System.err.println("❌ Invalid email address");
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot update email.");
                return false;
            }
            
            // Check if email is already in use by another user
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, newEmail);
                checkStmt.setInt(2, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("❌ Email address is already in use by another user");
                        return false;
                    }
                }
            }
            
            // Update the email
            String updateQuery = "UPDATE users SET email = ? WHERE user_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newEmail);
                updateStmt.setInt(2, userId);
                
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Email updated successfully for user ID: " + userId);
                    return true;
                } else {
                    System.out.println("❌ Failed to update email");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating email: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Updates the password for the current user.
     * 
     * @param currentPassword The user's current password
     * @param newPassword The user's new password
     * @return true if the password was updated successfully, false otherwise
     */
    public static boolean updatePassword(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6) {
            System.err.println("❌ New password must be at least 6 characters long");
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot update password.");
                return false;
            }
            
            // First verify the current password
            String verifyQuery = "SELECT password FROM users WHERE user_id = ?";
            try (PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
                verifyStmt.setInt(1, userId);
                try (ResultSet rs = verifyStmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        
                        // Verify the current password
                        // In a real application, you would use a proper password hashing library
                        if (!storedPassword.equals(currentPassword)) {
                            System.out.println("❌ Current password is incorrect");
                            return false;
                        }
                        
                        // Update the password
                        String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, newPassword);
                            updateStmt.setInt(2, userId);
                            
                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("✓ Password updated successfully for user ID: " + userId);
                                return true;
                            } else {
                                System.out.println("❌ Failed to update password");
                                return false;
                            }
                        }
                    } else {
                        System.out.println("❌ User not found");
                        return false;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating password: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes the profile picture for the current user.
     * 
     * @return true if the profile picture was deleted successfully, false otherwise
     */
    public static boolean deleteProfilePicture() {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot delete profile picture.");
                return false;
            }
            
            // Update the profile to set profile_picture to NULL
            String updateQuery = "UPDATE profiles SET profile_picture = NULL WHERE user_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, userId);
                
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Profile picture deleted successfully for user ID: " + userId);
                    return true;
                } else {
                    System.out.println("❌ No profile found or profile picture already null");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting profile picture: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets the total number of tasks (both pending and completed) for the current user.
     * 
     * @return The total number of tasks
     */
    public static int getTotalTaskCount() {
        int totalTasks = 0;
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot get task count.");
                return 0;
            }
            
            // Count pending tasks
            String pendingQuery = "SELECT COUNT(*) FROM pending_tasks WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(pendingQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalTasks += rs.getInt(1);
                    }
                }
            }
            
            // Count completed tasks
            String completedQuery = "SELECT COUNT(*) FROM completed_tasks WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(completedQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalTasks += rs.getInt(1);
                    }
                }
            }
            
            System.out.println("✓ Retrieved total task count for user ID: " + userId + ": " + totalTasks);
        } catch (SQLException ex) {
            System.err.println("❌ Error getting task count: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return totalTasks;
    }
    
    /**
     * Gets the total number of mood records for the current user.
     * 
     * @return The total number of mood records
     */
    public static int getMoodRecordCount() {
        int moodCount = 0;
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot get mood record count.");
                return 0;
            }
            
            // Count mood records
            String query = "SELECT COUNT(*) FROM mood_tracker WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        moodCount = rs.getInt(1);
                    }
                }
            }
            
            System.out.println("✓ Retrieved mood record count for user ID: " + userId + ": " + moodCount);
        } catch (SQLException ex) {
            System.err.println("❌ Error getting mood record count: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return moodCount;
    }
    
    /**
     * Gets the total number of sleep records for the current user.
     * 
     * @return The total number of sleep records
     */
    public static int getSleepRecordCount() {
        int sleepCount = 0;
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot get sleep record count.");
                return 0;
            }
            
            // Count sleep records
            String query = "SELECT COUNT(*) FROM sleep_records WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        sleepCount = rs.getInt(1);
                    }
                }
            }
            
            System.out.println("✓ Retrieved sleep record count for user ID: " + userId + ": " + sleepCount);
        } catch (SQLException ex) {
            System.err.println("❌ Error getting sleep record count: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return sleepCount;
    }
    
    /**
     * Gets the total number of expense records for the current user.
     * 
     * @return The total number of expense records
     */
    public static int getExpenseCount() {
        int expenseCount = 0;
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot get expense count.");
                return 0;
            }
            
            // Count expense records
            String query = "SELECT COUNT(*) FROM expenses WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        expenseCount = rs.getInt(1);
                    }
                }
            }
            
            System.out.println("✓ Retrieved expense count for user ID: " + userId + ": " + expenseCount);
        } catch (SQLException ex) {
            System.err.println("❌ Error getting expense count: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return expenseCount;
    }
    
    /**
     * Gets the account creation date for the current user.
     * 
     * @return The account creation date as a string, or null if not found
     */
    public static String getAccountCreationDate() {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot get account creation date.");
                return null;
            }
            
            // Get account creation date
            String query = "SELECT DATE_FORMAT(created_at, '%M %d, %Y') as formatted_date FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String creationDate = rs.getString("formatted_date");
                        System.out.println("✓ Retrieved account creation date for user ID: " + userId + ": " + creationDate);
                        return creationDate;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error getting account creation date: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
}
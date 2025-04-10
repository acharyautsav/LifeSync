package databasePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing pending tasks in the database.
 * This class handles adding, removing, and retrieving pending tasks.
 */
public class sendAndImportPendingList {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Retrieves all pending tasks from the database.
     * 
     * @return List of pending task descriptions
     */
    public static List<String> getPendingTasks() {
        List<String> pendingTasks = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot retrieve pending tasks.");
                return pendingTasks;
            }
            
            String query = "SELECT task_description FROM pending_tasks WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        pendingTasks.add(rs.getString("task_description"));
                    }
                    System.out.println("✓ Retrieved " + pendingTasks.size() + " pending tasks for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving pending tasks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pendingTasks;
    }
    
    /**
     * Adds a new task to the pending tasks list.
     * 
     * @param taskDescription The description of the task to add
     * @return true if the task was added successfully, false otherwise
     */
    public static boolean addTask(String taskDescription) {
        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot add pending task.");
                return false;
            }
            
            // First check if the task already exists for this user
            String checkQuery = "SELECT COUNT(*) FROM pending_tasks WHERE user_id = ? AND task_description = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                checkStmt.setString(2, taskDescription);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("⚠️ Task already exists for user ID " + userId + ": " + taskDescription);
                        return false;
                    }
                }
            }
            
            // Insert the new task - using the correct column names from your schema and the current user ID
            String insertQuery = "INSERT INTO pending_tasks (user_id, task_description) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setInt(1, userId);  // Use the current user's ID from the session
                pstmt.setString(2, taskDescription);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Task added successfully for user ID " + userId + ": " + taskDescription);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding task: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Removes a task from the pending tasks list.
     * 
     * @param taskDescription The description of the task to remove
     * @return true if the task was removed successfully, false otherwise
     */
    public static boolean removeTask(String taskDescription) {
        try (Connection conn = dbConnection.getConnection()) {
            // Get current user ID from session
            int userId = UserSession.getInstance().getUserId();
            
            if (userId == -1) {
                System.err.println("❌ No user logged in. Cannot remove pending task.");
                return false;
            }
            
            String query = "DELETE FROM pending_tasks WHERE user_id = ? AND task_description = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, taskDescription);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✓ Pending task removed successfully for user ID: " + userId);
                    return true;
                } else {
                    System.out.println("❌ Failed to remove pending task. Task not found.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error removing pending task: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
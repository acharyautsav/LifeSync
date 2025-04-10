package databasePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing user data in the database.
 * This class handles retrieving, updating, and deleting user records.
 */
public class databaseManager {
    
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * User class to represent user data
     */
    public static class User {
        private int id;
        private String username;
        private String email;
        private String password;
        private boolean isAdmin;
        
        public User(int id, String username, String email, String password, boolean isAdmin) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.isAdmin = isAdmin;
        }
        
        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public boolean isAdmin() { return isAdmin; }
    }
    
    /**
     * Retrieves all users from the database.
     * Only available to admin users.
     * 
     * @return A list of User objects
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        // Check if current user is admin
        if (!UserSession.getInstance().isAdmin()) {
            System.err.println("❌ Access denied: Only admin users can view all users");
            return users;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT user_id, username, email, password FROM users";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    boolean isAdmin = username.equals("admin"); // Consider "admin" username as admin
                    
                    users.add(new User(id, username, email, password, isAdmin));
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving users: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Retrieves a specific user from the database.
     * 
     * @param userId The ID of the user to retrieve
     * @return The User object, or null if not found
     */
    public static User getUser(int userId) {
        // Regular users can only access their own data
        if (!UserSession.getInstance().isAdmin() && userId != UserSession.getInstance().getUserId()) {
            System.err.println("❌ Access denied: Users can only access their own data");
            return null;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT user_id, username, email, password, username = 'admin' as is_admin FROM users WHERE user_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("user_id");
                        String username = rs.getString("username");
                        String email = rs.getString("email");
                        String password = rs.getString("password");
                        boolean isAdmin = rs.getBoolean("is_admin");
                        
                        return new User(id, username, email, password, isAdmin);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error retrieving user: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Updates a user's information in the database.
     * 
     * @param userId The ID of the user to update
     * @param username The new username (or null to keep current)
     * @param email The new email (or null to keep current)
     * @param password The new password (or null to keep current)
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateUser(int userId, String username, String email, String password) {
        // Regular users can only update their own data
        if (!UserSession.getInstance().isAdmin() && userId != UserSession.getInstance().getUserId()) {
            System.err.println("❌ Access denied: Users can only update their own data");
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
            List<String> updateFields = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();
            
            if (username != null && !username.trim().isEmpty()) {
                updateFields.add("username = ?");
                parameters.add(username);
            }
            
            if (email != null && !email.trim().isEmpty()) {
                updateFields.add("email = ?");
                parameters.add(email);
            }
            
            if (password != null && !password.trim().isEmpty()) {
                updateFields.add("password = ?");
                parameters.add(password);
            }
            
            // If no fields to update, return false
            if (updateFields.isEmpty()) {
                return false;
            }
            
            queryBuilder.append(String.join(", ", updateFields));
            queryBuilder.append(" WHERE user_id = ?");
            
            try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    pstmt.setObject(i + 1, parameters.get(i));
                }
                pstmt.setInt(parameters.size() + 1, userId);
                
                int rowsAffected = pstmt.executeUpdate();
                
                // If this is the current user and username was updated, update the session
                if (rowsAffected > 0 && userId == UserSession.getInstance().getUserId() && username != null && !username.isEmpty()) {
                    UserSession.getInstance().startSession(
                        userId, 
                        username, 
                        UserSession.getInstance().isAdmin()
                    );
                }
                
                return rowsAffected > 0;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating user: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a user from the database.
     * Only admin users can delete users, and they cannot delete themselves.
     * 
     * @param userId The ID of the user to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean deleteUser(int userId) {
        // Only admin can delete users
        if (!UserSession.getInstance().isAdmin()) {
            System.err.println("❌ Access denied: Only admin users can delete users");
            return false;
        }
        
        // Admin cannot delete themselves
        if (userId == UserSession.getInstance().getUserId()) {
            System.err.println("❌ Cannot delete the currently logged-in admin account");
            return false;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "DELETE FROM users WHERE user_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting user: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
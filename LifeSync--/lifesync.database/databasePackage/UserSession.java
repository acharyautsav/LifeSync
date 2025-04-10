package databasePackage;

/**
 * Singleton class to manage user session information across the application.
 */
public class UserSession {
    private static UserSession instance;
    
    private int userId = -1;
    private String username = "";
    private boolean isAdmin = false;
    
    // Private constructor to prevent instantiation
    private UserSession() {}
    
    /**
     * Get the singleton instance of UserSession
     * @return The UserSession instance
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * Start a new user session
     * @param userId The user ID
     * @param username The username
     * @param isAdmin Whether the user is an admin
     */
    public void startSession(int userId, String username, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.isAdmin = isAdmin;
        System.out.println("✓ Session started for user: " + username + " (ID: " + userId + ", Admin: " + isAdmin + ")");
    }
    
    /**
     * End the current user session
     */
    public void endSession() {
        this.userId = -1;
        this.username = "";
        this.isAdmin = false;
        System.out.println("✓ Session ended");
    }
    
    /**
     * Get the current user ID
     * @return The user ID, or -1 if no user is logged in
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Get the current username
     * @return The username, or an empty string if no user is logged in
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Check if the current user is an admin
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return isAdmin;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return userId != -1;
    }
}
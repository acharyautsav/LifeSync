package databasePackage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a completed task and providing utility methods
 * for managing completed tasks in the database.
 */
public class completedTask {
    private int id;
    private int userId;
    private String taskDescription;
    private LocalDate completionDate;
    
    // Database connection
    private static final DbConnection dbConnection = DbConnection.getInstance();
    
    /**
     * Constructor for creating a new completed task object
     * 
     * @param userId The ID of the user who completed the task
     * @param taskDescription The description of the completed task
     * @param completionDate The date when the task was completed
     */
    public completedTask(int userId, String taskDescription, LocalDate completionDate) {
        this.userId = userId;
        this.taskDescription = taskDescription;
        this.completionDate = completionDate;
    }
    
    /**
     * Constructor for creating a completed task object from database data
     * 
     * @param id The database ID of the task
     * @param userId The ID of the user who completed the task
     * @param taskDescription The description of the completed task
     * @param completionDate The date when the task was completed
     */
    public completedTask(int id, int userId, String taskDescription, LocalDate completionDate) {
        this.id = id;
        this.userId = userId;
        this.taskDescription = taskDescription;
        this.completionDate = completionDate;
    }
    
    /**
     * Saves this completed task to the database
     * 
     * @return true if the task was saved successfully, false otherwise
     */
    public boolean saveToDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            String insertQuery = "INSERT INTO completed_tasks (user_id, task_description, completion_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, taskDescription);
                pstmt.setDate(3, Date.valueOf(completionDate));
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saving completed task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all completed tasks for a specific user
     * 
     * @param userId The ID of the user whose tasks to retrieve
     * @return List of completed task objects
     */
    public static List<completedTask> getCompletedTasksByUser(int userId) {
        List<completedTask> tasks = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT id, user_id, task_description, completion_date FROM completed_tasks WHERE user_id = ? ORDER BY completion_date DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        tasks.add(new completedTask(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("task_description"),
                            rs.getDate("completion_date").toLocalDate()
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving completed tasks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Ensures that the completed_tasks table exists in the database.
     * This method should be called during application initialization.
     */
    public static void ensureCompletedTasksTableExists() {
        try (Connection conn = dbConnection.getConnection()) {
            String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS completed_tasks (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "task_description VARCHAR(255) NOT NULL, " +
                "completion_date DATE NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
            
            try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
                pstmt.executeUpdate();
                System.out.println("✓ Completed tasks table created successfully or already exists");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating completed tasks table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getTaskDescription() {
        return taskDescription;
    }
    
    public LocalDate getCompletionDate() {
        return completionDate;
    }
}
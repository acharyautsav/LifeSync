package lifesync.database;


import java.sql.*;

public class DatabaseHelper {
    private static Connection connection;

    // Connect to the database
    public static Connection connect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:tasks.db");
                Statement stmt = connection.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "task_name TEXT NOT NULL, " +
                        "status TEXT NOT NULL DEFAULT 'pending')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Add a task to the database
    public static void addTask(String taskName) {
        try {
            PreparedStatement pstmt = connect().prepareStatement("INSERT INTO tasks (task_name, status) VALUES (?, ?)");
            pstmt.setString(1, taskName);
            pstmt.setString(2, "pending");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all pending tasks from the database
    public static ResultSet loadPendingTasks() {
        try {
            Statement stmt = connect().createStatement();
            return stmt.executeQuery("SELECT id, task_name FROM tasks WHERE status = 'pending'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update the status of a task to complete
    public static void updateTaskStatus(int taskId, String status) {
        try {
            PreparedStatement pstmt = connect().prepareStatement("UPDATE tasks SET status = ? WHERE id = ?");
            pstmt.setString(1, status);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

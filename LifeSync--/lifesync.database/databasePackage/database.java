package databasePackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/lifesync"; // Replace with your database details
        String user = "root";  // Your MySQL username
        String password = "45683968";  // Your MySQL password

        try {
            // Ensure that the MySQL JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Return a connection to the database
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;  // Return null if connection fails
        }
    }
}

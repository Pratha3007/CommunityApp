package src.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/app_data";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Prevent instantiation
    private DatabaseConnection() {}

//    static {
//        try {
//            // Load MySQL JDBC Driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(URL, USER, PASSWORD);
//            System.out.println("Database connected successfully.");
//        } catch (ClassNotFoundException e) {
//            System.err.println("JDBC Driver not found.");
//            e.printStackTrace();
//        } catch (SQLException e) {
//            System.err.println("Failed to connect to the database.");
//            e.printStackTrace();
//        }
//    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException s) {
            System.out.println(s.getMessage());
        }catch (ClassNotFoundException c){
            System.out.println("Driver not loaded correctly");
            System.out.println(c.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing the database connection.");
            e.printStackTrace();
        }
    }
}

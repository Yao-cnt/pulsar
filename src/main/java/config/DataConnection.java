package config;

import java.sql.Connection;

public class DataConnection {
    private static String DB_URL = "jdbc:mysql://localhost:3306/hems";
    private static String DB_USER = "root";
    private static String DB_PASSWORD = "First#1234";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = java.sql.DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}

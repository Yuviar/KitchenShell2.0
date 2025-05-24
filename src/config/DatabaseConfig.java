package config;

import java.sql.*;

public class DatabaseConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/db_kitchenshellnew";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Koneksi ke database gagal!");
        }
        return connection;
    }

    public static Connection getconnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
            
}

package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

    // Nama database harus db_sikursus sesuai panduan Pertemuan 11
    private static final String URL
            = "jdbc:mysql://localhost:3306/db_sikursus2";
    private static final String USER = "root";
    private static final String PASS = "";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
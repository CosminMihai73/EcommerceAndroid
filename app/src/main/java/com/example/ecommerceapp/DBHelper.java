package com.example.ecommerceapp;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {
    private static final String DB = "OnlineStore";
    private static final String IP = "10.0.2.2";  // Adresa IP pentru emulatoare Android
    private static final String PORT = "3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Cosmin123!";

    public Connection CONN() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB;
            conn = DriverManager.getConnection(connectionString, USERNAME, PASSWORD);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return conn;
    }
}

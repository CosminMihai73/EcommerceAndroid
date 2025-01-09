package com.example.ecommerceapp.db;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    private static final String DB = "OnlineStore";
    private static final String IP = "10.0.2.2";  // Adresa IP pentru emulator Android
    private static final String PORT = "3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Cosmin123!";

    public static Connection CONN() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB + "?useSSL=false";
            conn = DriverManager.getConnection(connectionString, USERNAME, PASSWORD);
            Log.d("DB", "Conexiune stabilită cu succes la baza de date.");
        } catch (SQLException | ClassNotFoundException e) {
            Log.e("DB", "Eroare la conexiune la baza de date: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}


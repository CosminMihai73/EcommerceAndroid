package com.example.ecommerceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, createAccountButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper();

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    authenticateUser(username, password);
                } else {
                    Toast.makeText(MainActivity.this, "Introduceti username si parola", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void authenticateUser(String username, String password) {
        new Thread(() -> {
            String userType = checkCredentials(username, password);
            if (userType != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", getUserId(username, password)); // Salvează user_id
                editor.apply();

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Autentificare reușită!", Toast.LENGTH_SHORT).show();

                    Intent intent;
                    if (userType.equals("Client")) {
                        intent = new Intent(MainActivity.this, ClientMainActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, AdminMainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Username sau parola incorecte!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String checkCredentials(String username, String password) {
        String userType = null;
        try (Connection con = dbHelper.CONN()) {
            if (con != null) {
                String query = "SELECT type FROM Users WHERE username = ? AND password = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    userType = rs.getString("type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userType;
    }

    private int getUserId(String username, String password) {
        int userId = -1;
        try (Connection con = dbHelper.CONN()) {
            if (con != null) {
                String query = "SELECT id FROM Users WHERE username = ? AND password = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
}

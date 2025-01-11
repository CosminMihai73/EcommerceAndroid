package com.example.ecommerceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button createAccountButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dbHelper = new DBHelper();


        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        createAccountButton = findViewById(R.id.createAccountButton);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {

                    createAccount(username, password, email);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Completați toate câmpurile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAccount(String username, String password, String email) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean success = addUserToDatabase(username, password, email);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(CreateAccountActivity.this, "Cont creat cu succes!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Eroare la crearea contului", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean addUserToDatabase(String username, String password, String email) {
        boolean isSuccess = false;
        Connection con = dbHelper.CONN();
        if (con != null) {
            try {
                String query = "INSERT INTO Users (username, password, email, type) VALUES (?, ?, ?, 'Client')";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, email);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    isSuccess = true;
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }
}

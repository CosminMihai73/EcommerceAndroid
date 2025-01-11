package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;

public class AdminMainActivity extends AppCompatActivity {

    private Button manageProductsButton, viewReportsButton, updateUserInfoButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        manageProductsButton = findViewById(R.id.manageProductsButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);
        updateUserInfoButton = findViewById(R.id.updateUserInfoButton);
        logoutButton = findViewById(R.id.logoutButton);


        logoutButton.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();


            Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        manageProductsButton.setOnClickListener(v -> {

            Intent intent = new Intent(AdminMainActivity.this, ManageProductsActivity.class);
            startActivity(intent);
        });


        viewReportsButton.setOnClickListener(v -> {

            Intent intent = new Intent(AdminMainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });


        updateUserInfoButton.setOnClickListener(v -> {

            Intent intent = new Intent(AdminMainActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });
    }
}

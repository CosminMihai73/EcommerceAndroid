package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;

public class SettingsActivity extends AppCompatActivity {

    private Button purchaseHistoryButton, reservationHistoryButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        purchaseHistoryButton = findViewById(R.id.purchaseHistoryButton);
        reservationHistoryButton = findViewById(R.id.reservationHistoryButton);
        logoutButton = findViewById(R.id.logoutButton);

        purchaseHistoryButton.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, PurchaseHistoryActivity.class);

            startActivity(intent);
        });

        reservationHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ReservationHistoryActivity.class);

            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

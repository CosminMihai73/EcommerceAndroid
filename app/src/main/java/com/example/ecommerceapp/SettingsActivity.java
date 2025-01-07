package com.example.ecommerceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button purchaseHistoryButton, reservationHistoryButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inițializarea butoanelor
        purchaseHistoryButton = findViewById(R.id.purchaseHistoryButton);
        reservationHistoryButton = findViewById(R.id.reservationHistoryButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Logica pentru butonul de Istoric Achiziții
        purchaseHistoryButton.setOnClickListener(v -> {
            // Creează un Intent pentru a lansa activitatea de Istoric Achiziții
            Intent intent = new Intent(SettingsActivity.this, PurchaseHistoryActivity.class);

            // Lansează activitatea
            startActivity(intent);
        });

        // Logica pentru butonul de Istoric Rezervări
        reservationHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ReservationHistoryActivity.class);

            // Lansează activitatea
            startActivity(intent);
        });

        // Logica pentru butonul de Deconectare
        logoutButton.setOnClickListener(v -> {
            // Șterge datele din SharedPreferences pentru a deconecta utilizatorul
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Șterge toate datele stocate
            editor.apply();  // Aplică schimbările

            // Navighează către pagina de login (MainActivity)
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Închide activitatea curentă pentru a preveni revenirea la SettingsActivity
        });
    }
}

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

        // Logica pentru butonul de Deconectare
        logoutButton.setOnClickListener(v -> {
            // Șterge datele din SharedPreferences pentru a deconecta utilizatorul
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Șterge toate datele stocate
            editor.apply();  // Aplică schimbările

            // Navighează către pagina de login (MainActivity)
            Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Închide AdminMainActivity pentru a preveni revenirea la aceasta
        });

        // Gestionare Produse
        manageProductsButton.setOnClickListener(v -> {
            // Aici adaugi logica pentru a deschide activitatea de gestionare produse
            Intent intent = new Intent(AdminMainActivity.this, ManageProductsActivity.class);
            startActivity(intent);
        });

        // Vizualizare Rapoarte
        viewReportsButton.setOnClickListener(v -> {
            // Aici adaugi logica pentru a deschide activitatea de vizualizare rapoarte
            Intent intent = new Intent(AdminMainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        // Actualizare Informații user
        updateUserInfoButton.setOnClickListener(v -> {
            // Aici adaugi logica pentru a deschide activitatea de actualizare informații produse
            Intent intent = new Intent(AdminMainActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });
    }
}

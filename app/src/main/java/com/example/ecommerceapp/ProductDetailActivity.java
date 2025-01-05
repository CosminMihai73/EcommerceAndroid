package com.example.ecommerceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productBrand, productPrice, productStock, productDescription;
    private Button addToCartButton, reserveButton;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        cartManager = new CartManager(this);  // Inițializăm managerul de coș

        // Inițializăm view-urile
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        productDescription = findViewById(R.id.productDescription);
        addToCartButton = findViewById(R.id.addToCartButton);
        reserveButton = findViewById(R.id.reserveButton);

        // Obținem detaliile produsului din intent
        String name = getIntent().getStringExtra("product_name");
        String brand = getIntent().getStringExtra("product_brand");
        double price = getIntent().getDoubleExtra("product_price", 0);
        int stock = getIntent().getIntExtra("product_stock", 0);
        String description = getIntent().getStringExtra("product_description");
        String imagePath = getIntent().getStringExtra("product_image");

        // Setăm datele în view-uri
        productName.setText(name);
        productBrand.setText(brand);
        productPrice.setText("Price: " + price);
        productStock.setText("Stock: " + stock);
        productDescription.setText(description);

        // Setăm OnClickListener pentru butonul de adăugare în coș
        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(
                    0, name, brand, price, stock, description, imagePath);  // Creăm un obiect Product
            cartManager.addToCart(product);  // Adăugăm produsul în coș

            Toast.makeText(ProductDetailActivity.this, "Produs adăugat în coș!", Toast.LENGTH_SHORT).show();
        });

        // Setăm OnClickListener pentru butonul de rezervare
        reserveButton.setOnClickListener(v -> {
            // Obține ID-ul utilizatorului din SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1); // User ID din login

            if (userId != -1) {
                if (stock > 0) {
                    // Scade cantitatea din tabelul Products
                    new UpdateProductStockTask().execute(name, stock - 1);

                    // Salvează rezervarea în tabela 'reservations'
                    new SaveReservationTask().execute(name, 1, userId);  // Folosește ID-ul utilizatorului din SharedPreferences

                    // Afișează un mesaj de confirmare
                    Toast.makeText(ProductDetailActivity.this, "Produsul a fost rezervat pentru 24 de ore!", Toast.LENGTH_SHORT).show();

                    // Redirecționează către ClientMainActivity
                    Intent intent = new Intent(ProductDetailActivity.this, ClientMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Mesaj pentru cazul în care nu sunt produse disponibile
                    Toast.makeText(ProductDetailActivity.this, "Nu mai există stoc disponibil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class UpdateProductStockTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String productName = (String) params[0];
            int newStock = (int) params[1];

            // Conexiune la baza de date pentru a actualiza cantitatea produsului
            DBHelper dbHelper = new DBHelper();
            try (Connection con = dbHelper.CONN()) {
                if (con != null) {
                    String query = "UPDATE Products SET stock = ? WHERE name = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, newStock);
                    stmt.setString(2, productName);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SaveReservationTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String productName = (String) params[0];
            int quantity = (int) params[1];
            int userId = (int) params[2];

            // Conexiune la baza de date pentru a salva rezervarea
            DBHelper dbHelper = new DBHelper();
            try (Connection con = dbHelper.CONN()) {
                if (con != null) {
                    String query = "INSERT INTO reservations (product_name, quantity, user_id, reserved_at) VALUES (?, ?, ?, NOW())";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, productName);
                    stmt.setInt(2, quantity);
                    stmt.setInt(3, userId);  // Folosim user_id din SharedPreferences
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

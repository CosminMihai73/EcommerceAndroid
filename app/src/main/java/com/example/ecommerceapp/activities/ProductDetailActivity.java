package com.example.ecommerceapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productBrand, productPrice, productStock, productDescription;
    private Button addToCartButton, reserveButton;
    private ImageView productImage;  // ImageView-ul pentru imaginea produsului

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);  // Încarcă layout-ul pentru activitate

        // Inițializăm câmpurile UI
        productName = findViewById(R.id.productName);
        productBrand = findViewById(R.id.productBrand);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        productDescription = findViewById(R.id.productDescription);
        productImage = findViewById(R.id.productImage);  // Căutăm ImageView-ul pentru imagine

        addToCartButton = findViewById(R.id.addToCartButton);
        reserveButton = findViewById(R.id.reserveButton);

        // Obținem detaliile produsului din Intent
        String name = getIntent().getStringExtra("product_name");
        String brand = getIntent().getStringExtra("product_brand");
        double price = getIntent().getDoubleExtra("product_price", 0);
        int stock = getIntent().getIntExtra("product_stock", 0);
        String description = getIntent().getStringExtra("product_description");
        String imagePath = getIntent().getStringExtra("product_image");  // Calea imaginii produsului

        // Setăm datele în TextView-uri
        productName.setText(name);
        productBrand.setText(brand);
        productPrice.setText("Price: " + price);
        productStock.setText("Stock: " + stock);
        productDescription.setText(description);

        // Încarcă imaginea produsului folosind Glide
        Glide.with(this)
                .load(imagePath)  // Calea imaginii (URL sau cale locală)
                .into(productImage);  // Încarcă imaginea în ImageView

        // Adăugare în coș
        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(0, name, brand, price, stock, description, imagePath);  // Creăm un obiect Product
            CartManager cartManager = new CartManager(ProductDetailActivity.this);
            cartManager.addToCart(product);  // Adaugă produsul în coș

            Toast.makeText(ProductDetailActivity.this, "Produs adăugat în coș!", Toast.LENGTH_SHORT).show();
        });

        // Rezervare în showroom
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

    // Clasă pentru actualizarea stocului produsului
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

    // Clasă pentru salvarea rezervării în baza de date
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

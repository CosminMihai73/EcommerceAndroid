package com.example.ecommerceapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productBrand, productPrice, productStock, productDescription;
    private ImageView productImage;
    private Button addToCartButton;
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
        productImage = findViewById(R.id.productImage);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Obținem datele produsului din intent
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

        Glide.with(this).load(imagePath).into(productImage);

        // Setăm OnClickListener pentru butonul de adăugare în coș
        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(
                    0, name, brand, price, stock, description, imagePath);  // Creăm un obiect Product
            cartManager.addToCart(product);  // Adăugăm produsul în coș

            Toast.makeText(ProductDetailActivity.this, "Produs adăugat în coș!", Toast.LENGTH_SHORT).show();
        });
    }
}


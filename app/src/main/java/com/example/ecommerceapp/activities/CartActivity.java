package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.adapters.CartAdapter;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.R;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private CartManager cartManager;
    private TextView totalTextView;
    private Button placeOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = new CartManager(this);
        cartItems = cartManager.getCartItems();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        totalTextView = findViewById(R.id.totalTextView);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        updateTotal();

        // Setează OnClickListener pentru butonul de plasare a comenzii
        placeOrderButton.setOnClickListener(v -> {
            // Dacă coșul nu este gol, navighează la activitatea de plasare a comenzii
            if (!cartItems.isEmpty()) {
                // Log pentru a verifica produsele din coș
                for (CartItem item : cartItems) {
                    Log.d("Cart", "Product: " + item.getProductName() + ", Quantity: " + item.getQuantity());
                }

                // Trimitem produsele din coș prin Intent
                Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                intent.putExtra("cartItems", new Gson().toJson(cartItems)); // Convertim lista de CartItem într-un JSON
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Coșul de cumpărături este gol!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotal() {
        double totalPrice = cartManager.getSavedTotalPrice();

        // Formatează prețul total cu 2 zecimale
        DecimalFormat decimalFormat = new DecimalFormat("00.00");
        String formattedTotal = decimalFormat.format(totalPrice);

        totalTextView.setText("Total: " + formattedTotal + " RON");
    }
}

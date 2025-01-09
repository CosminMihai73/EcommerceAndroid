package com.example.ecommerceapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.adapters.PurchaseHistoryAdapter;
import com.example.ecommerceapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PurchaseHistoryAdapter adapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        recyclerView = findViewById(R.id.recyclerView);
        orderList = new ArrayList<>();
        adapter = new PurchaseHistoryAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Încarcă istoricul comenzilor pentru utilizatorul curent
        loadPurchaseHistory();
    }

    private void loadPurchaseHistory() {
        // Obține ID-ul utilizatorului din SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Nu s-a putut obține ID-ul utilizatorului.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rulează interogarea SQL într-un thread separat
        new Thread(() -> {
            List<Order> orders = getOrdersForUser(userId);
            runOnUiThread(() -> {
                if (orders.isEmpty()) {
                    Toast.makeText(PurchaseHistoryActivity.this, "Nu aveți comenzi.", Toast.LENGTH_SHORT).show();
                } else {
                    orderList.clear();
                    orderList.addAll(orders);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private List<Order> getOrdersForUser(int userId) {
        List<Order> orders = new ArrayList<>();
        DBHelper dbHelper = new DBHelper();

        try (Connection connection = dbHelper.CONN()) {
            if (connection != null) {
                String query = "SELECT * FROM Orders WHERE user_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int orderId = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    String address = rs.getString("address");
                    String paymentMethod = rs.getString("payment_method");
                    String deliveryMethod = rs.getString("delivery_method");
                    double totalPrice = rs.getDouble("total_price");
                    String createdAt = rs.getString("created_at");

                    // Adaugă comanda în listă
                    orders.add(new Order(orderId, fullName, address, paymentMethod, deliveryMethod, totalPrice, createdAt));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}

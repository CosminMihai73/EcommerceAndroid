package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.adapters.OrderItemsAdapter;
import com.example.ecommerceapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderItemsAdapter adapter;
    private List<OrderItem> orderItemsList;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);

        recyclerView = findViewById(R.id.recyclerViewOrderItems);
        orderItemsList = new ArrayList<>();
        adapter = new OrderItemsAdapter(this, orderItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", -1);

        if (orderId == -1) {
            Toast.makeText(this, "ID-ul comenzii nu a fost furnizat", Toast.LENGTH_SHORT).show();
            return;
        }

        loadOrderItems(orderId);
    }

    private void loadOrderItems(int orderId) {
        new Thread(() -> {
            List<OrderItem> items = getOrderItemsForOrder(orderId);
            runOnUiThread(() -> {
                if (items.isEmpty()) {
                    Toast.makeText(OrderDetailActivity.this, "Nu sunt produse pentru această comandă.", Toast.LENGTH_SHORT).show();
                } else {
                    orderItemsList.clear();
                    orderItemsList.addAll(items);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private List<OrderItem> getOrderItemsForOrder(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        DBHelper dbHelper = new DBHelper();

        try (Connection connection = dbHelper.CONN()) {
            if (connection != null) {
                String query = "SELECT * FROM orderitems WHERE order_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");

                    orderItems.add(new OrderItem(productName, quantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}

package com.example.ecommerceapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.adapters.ReservationHistoryAdapter;
import com.example.ecommerceapp.models.ReservationItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationHistoryAdapter adapter;
    private List<ReservationItem> reservationList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID-ul utilizatorului nu a fost găsit.", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewReservations);
        reservationList = new ArrayList<>();
        adapter = new ReservationHistoryAdapter(this, reservationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadReservationHistory(userId);
    }

    private void loadReservationHistory(int userId) {
        new Thread(() -> {
            List<ReservationItem> items = getReservationsForUser(userId);
            runOnUiThread(() -> {
                if (items.isEmpty()) {
                    Toast.makeText(ReservationHistoryActivity.this, "Nu aveți rezervări.", Toast.LENGTH_SHORT).show();
                } else {
                    reservationList.clear();
                    reservationList.addAll(items);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private List<ReservationItem> getReservationsForUser(int userId) {
        List<ReservationItem> reservationItems = new ArrayList<>();
        DBHelper dbHelper = new DBHelper();

        try (Connection connection = dbHelper.CONN()) {
            if (connection != null) {
                String query = "SELECT * FROM reservations WHERE user_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    String expirationDate = rs.getString("expiration_at");

                    reservationItems.add(new ReservationItem(productName, quantity, expirationDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationItems;
    }
}

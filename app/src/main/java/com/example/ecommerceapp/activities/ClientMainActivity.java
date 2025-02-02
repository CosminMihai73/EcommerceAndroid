package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button filterButton;
    private LinearLayout filterMenu;
    private Button applyFiltersButton;
    private Spinner brandFilter;
    private EditText minPriceFilter, maxPriceFilter, stockFilter;
    private List<String> brandList = new ArrayList<>();
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        recyclerView = findViewById(R.id.recyclerView);
        filterButton = findViewById(R.id.filterButton);
        filterMenu = findViewById(R.id.filterMenu);
        applyFiltersButton = findViewById(R.id.applyFiltersButton);
        brandFilter = findViewById(R.id.brandFilter);
        minPriceFilter = findViewById(R.id.minPriceFilter);
        maxPriceFilter = findViewById(R.id.maxPriceFilter);
        stockFilter = findViewById(R.id.stockFilter);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);


        Button viewCartButton = findViewById(R.id.viewCartButton);
        viewCartButton.setOnClickListener(v -> {

            Intent intent = new Intent(ClientMainActivity.this, CartActivity.class);
            startActivity(intent);
        });


        loadProducts();
        loadBrands();


        filterButton.setOnClickListener(v -> {

            if (filterMenu.getVisibility() == View.GONE) {
                filterMenu.setVisibility(View.VISIBLE);
            } else {
                filterMenu.setVisibility(View.GONE);
            }
        });


        applyFiltersButton.setOnClickListener(v -> {
            String selectedBrand = brandFilter.getSelectedItem().toString();
            String minPrice = minPriceFilter.getText().toString();
            String maxPrice = maxPriceFilter.getText().toString();
            String stock = stockFilter.getText().toString();


            if (selectedBrand.equals("Toate brandurile") && minPrice.isEmpty() && maxPrice.isEmpty() && stock.isEmpty()) {
                loadProducts();
            } else {
                loadFilteredProducts(selectedBrand, minPrice, maxPrice, stock);
            }
        });

        settingsButton = findViewById(R.id.settingsButton);


        settingsButton.setOnClickListener(v -> {

            Intent intent = new Intent(ClientMainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        Thread thread = new Thread(() -> {
            Connection connection = new DBHelper().CONN();
            if (connection != null) {
                try {
                    String query = "SELECT * FROM Products";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    // Curăță lista înainte de a adăuga produsele
                    productList.clear();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String brand = rs.getString("brand");
                        double price = rs.getDouble("price");
                        int stock = rs.getInt("stock");
                        String description = rs.getString("description");
                        String imagePath = rs.getString("image_path");

                        Product product = new Product(id, name, brand, price, stock, description, imagePath);
                        productList.add(product);
                    }
                    runOnUiThread(() -> productAdapter.notifyDataSetChanged());
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ClientMainActivity.this, "Eroare la încărcarea produselor", Toast.LENGTH_SHORT).show());
                }
            }
        });
        thread.start();
    }

    private void loadBrands() {

        Thread thread = new Thread(() -> {
            Connection connection = new DBHelper().CONN();
            if (connection != null) {
                try {
                    String query = "SELECT DISTINCT brand FROM Products";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    brandList.clear();
                    brandList.add("Toate brandurile");
                    while (rs.next()) {
                        String brand = rs.getString("brand");
                        brandList.add(brand);
                    }

                    runOnUiThread(() -> {

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ClientMainActivity.this, android.R.layout.simple_spinner_item, brandList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        brandFilter.setAdapter(adapter);
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ClientMainActivity.this, "Eroare la încărcarea brandurilor", Toast.LENGTH_SHORT).show());
                }
            }
        });
        thread.start();
    }

    private void loadFilteredProducts(String brand, String minPrice, String maxPrice, String stock) {
        Thread thread = new Thread(() -> {
            Connection connection = new DBHelper().CONN();
            if (connection != null) {
                try {
                    StringBuilder query = new StringBuilder("SELECT * FROM Products WHERE 1=1");

                    // Adăugăm filtrele
                    if (!brand.equals("Toate brandurile")) {
                        query.append(" AND brand = ?");
                    }
                    if (!minPrice.isEmpty()) {
                        query.append(" AND price >= ?");
                    }
                    if (!maxPrice.isEmpty()) {
                        query.append(" AND price <= ?");
                    }
                    if (!stock.isEmpty()) {
                        query.append(" AND stock >= ?");
                    }

                    PreparedStatement stmt = connection.prepareStatement(query.toString());

                    int index = 1;
                    if (!brand.equals("Toate brandurile")) {
                        stmt.setString(index++, brand);
                    }
                    if (!minPrice.isEmpty()) {
                        stmt.setDouble(index++, Double.parseDouble(minPrice));
                    }
                    if (!maxPrice.isEmpty()) {
                        stmt.setDouble(index++, Double.parseDouble(maxPrice));
                    }
                    if (!stock.isEmpty()) {
                        stmt.setInt(index++, Integer.parseInt(stock));
                    }

                    ResultSet rs = stmt.executeQuery();

                    // Curăță lista înainte de a adăuga produsele filtrate
                    productList.clear(); // Aceasta previne duplicarea
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String productBrand = rs.getString("brand");
                        double price = rs.getDouble("price");
                        int productStock = rs.getInt("stock");
                        String description = rs.getString("description");
                        String imagePath = rs.getString("image_path");

                        Product product = new Product(id, name, productBrand, price, productStock, description, imagePath);
                        productList.add(product);
                    }
                    // Notifică adapterul că datele s-au schimbat
                    runOnUiThread(() -> productAdapter.notifyDataSetChanged());
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ClientMainActivity.this, "Eroare la aplicarea filtrelor", Toast.LENGTH_SHORT).show());
                }
            }
        });
        thread.start();
    }

}

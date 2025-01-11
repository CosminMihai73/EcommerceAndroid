package com.example.ecommerceapp.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.adapters.ManageProductsAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageProductsAdapter adapter;
    private ArrayList<Product> productList;
    private DBHelper dbHelper;

    private EditText editTextName, editTextBrand, editTextPrice, editTextStock, editTextDescription, editTextImagePath;
    private Button saveProductButton, addProductButton, saveNewProductButton;
    private View editProductLayout, addProductLayout;
    private int editingProductId = -1;

    private EditText editTextAddName, editTextAddBrand, editTextAddPrice, editTextAddStock, editTextAddDescription, editTextAddImagePath;

    private static final String CHANNEL_ID = "product_management_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        dbHelper = new DBHelper();

        // Formularul de editare
        editProductLayout = findViewById(R.id.editProductLayout);
        editTextName = findViewById(R.id.editTextName);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextStock = findViewById(R.id.editTextStock);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextImagePath = findViewById(R.id.editTextImagePath);
        saveProductButton = findViewById(R.id.saveProductButton);

        // Formularul de adăugare a unui produs
        addProductLayout = findViewById(R.id.addProductLayout);
        editTextAddName = findViewById(R.id.editTextAddName);
        editTextAddBrand = findViewById(R.id.editTextAddBrand);
        editTextAddPrice = findViewById(R.id.editTextAddPrice);
        editTextAddStock = findViewById(R.id.editTextAddStock);
        editTextAddDescription = findViewById(R.id.editTextAddDescription);
        editTextAddImagePath = findViewById(R.id.editTextAddImagePath);
        saveNewProductButton = findViewById(R.id.saveNewProductButton);

        // Butonul de adăugare produs
        addProductButton = findViewById(R.id.buttonAddProduct);

        // Încarcă produsele din baza de date
        loadProducts();

        // Salvarea modificărilor la produs
        saveProductButton.setOnClickListener(v -> saveProduct());

        // Afișează formularul pentru adăugarea unui produs
        addProductButton.setOnClickListener(v -> {
            addProductLayout.setVisibility(View.VISIBLE);
            editProductLayout.setVisibility(View.GONE);
        });

        // Salvează un produs nou
        saveNewProductButton.setOnClickListener(v -> saveNewProduct());

        // Creează canalul de notificare
        createNotificationChannel();
    }

    private void loadProducts() {
        new Thread(() -> {
            Connection conn = dbHelper.CONN();
            if (conn != null) {
                try {
                    ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM products");
                    final ArrayList<Product> tempProductList = new ArrayList<>();

                    while (rs.next()) {
                        Product product = new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("brand"),
                                rs.getDouble("price"),
                                rs.getInt("stock"),
                                rs.getString("description"),
                                rs.getString("image_path")
                        );
                        tempProductList.add(product);
                    }

                    runOnUiThread(() -> {
                        if (adapter == null) {
                            adapter = new ManageProductsAdapter(tempProductList, new ManageProductsAdapter.OnProductClickListener() {
                                @Override
                                public void onEditClick(Product product) {
                                    editProductLayout.setVisibility(View.VISIBLE);
                                    addProductLayout.setVisibility(View.GONE);
                                    editingProductId = product.getId();
                                    editTextName.setText(product.getName());
                                    editTextBrand.setText(product.getBrand());
                                    editTextPrice.setText(String.valueOf(product.getPrice()));
                                    editTextStock.setText(String.valueOf(product.getStock()));
                                    editTextDescription.setText(product.getDescription());
                                    editTextImagePath.setText(product.getImagePath());
                                }

                                @Override
                                public void onDeleteClick(Product product) {
                                    deleteProduct(product);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            productList.clear();
                            productList.addAll(tempProductList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> Toast.makeText(ManageProductsActivity.this, "Eroare la incarcarea produselor", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void saveProduct() {
        final String name = editTextName.getText().toString();
        final String brand = editTextBrand.getText().toString();
        final double price = Double.parseDouble(editTextPrice.getText().toString());
        final int stock = Integer.parseInt(editTextStock.getText().toString());
        final String description = editTextDescription.getText().toString();
        final String imagePath = editTextImagePath.getText().toString();

        new Thread(() -> {
            Connection conn = dbHelper.CONN();
            if (conn != null) {
                try {
                    String query = "UPDATE products SET name = ?, brand = ?, price = ?, stock = ?, description = ?, image_path = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setString(2, brand);
                    stmt.setDouble(3, price);
                    stmt.setInt(4, stock);
                    stmt.setString(5, description);
                    stmt.setString(6, imagePath);
                    stmt.setInt(7, editingProductId);

                    stmt.executeUpdate();

                    runOnUiThread(() -> {
                        loadProducts();
                        editProductLayout.setVisibility(View.GONE);
                        editingProductId = -1;
                        Toast.makeText(ManageProductsActivity.this, "Produs modificat cu succes", Toast.LENGTH_SHORT).show();
                        sendNotification("Produs modificat", "Produsul a fost modificat cu succes.");
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ManageProductsActivity.this, "Eroare la modificarea produsului", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void saveNewProduct() {
        final String name = editTextAddName.getText().toString();
        final String brand = editTextAddBrand.getText().toString();
        final double price = Double.parseDouble(editTextAddPrice.getText().toString());
        final int stock = Integer.parseInt(editTextAddStock.getText().toString());
        final String description = editTextAddDescription.getText().toString();
        final String imagePath = editTextAddImagePath.getText().toString();

        new Thread(() -> {
            Connection conn = dbHelper.CONN();
            if (conn != null) {
                try {
                    String query = "INSERT INTO products (name, brand, price, stock, description, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setString(2, brand);
                    stmt.setDouble(3, price);
                    stmt.setInt(4, stock);
                    stmt.setString(5, description);
                    stmt.setString(6, imagePath);

                    stmt.executeUpdate();

                    runOnUiThread(() -> {
                        loadProducts();
                        addProductLayout.setVisibility(View.GONE);
                        Toast.makeText(ManageProductsActivity.this, "Produs adaugat cu succes", Toast.LENGTH_SHORT).show();
                        sendNotification("Produs adăugat", "Produsul a fost adăugat cu succes.");
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ManageProductsActivity.this, "Eroare la adaugarea produsului", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void deleteProduct(final Product product) {
        new Thread(() -> {
            Connection conn = dbHelper.CONN();
            if (conn != null) {
                try {
                    String query = "DELETE FROM products WHERE id = " + product.getId();
                    conn.createStatement().executeUpdate(query);

                    runOnUiThread(() -> {
                        loadProducts();
                        Toast.makeText(ManageProductsActivity.this, "Produs șters cu succes", Toast.LENGTH_SHORT).show();
                        sendNotification("Produs șters", "Produsul a fost șters cu succes.");
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ManageProductsActivity.this, "Eroare la ștergerea produsului", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Product Management Notifications";
            String description = "Notifications for product management actions";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

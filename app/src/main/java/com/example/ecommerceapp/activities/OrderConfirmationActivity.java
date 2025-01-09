package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    private EditText fullNameEditText, addressEditText;
    private RadioGroup paymentMethodGroup, deliveryMethodGroup;
    private Button submitOrderButton;
    private int userId; // ID-ul utilizatorului

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        deliveryMethodGroup = findViewById(R.id.deliveryMethodGroup);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        // Obține ID-ul utilizatorului din SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1); // User ID din login

        // Preia produsele din coș
        String cartItemsJson = getIntent().getStringExtra("cartItems");
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        List<CartItem> cartItems = new Gson().fromJson(cartItemsJson, type);

        submitOrderButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String paymentMethod = ((RadioButton) findViewById(paymentMethodGroup.getCheckedRadioButtonId())).getText().toString();
            String deliveryMethod = ((RadioButton) findViewById(deliveryMethodGroup.getCheckedRadioButtonId())).getText().toString();

            // Validare pentru metodele de plată și livrare
            paymentMethod = mapPaymentMethod(paymentMethod); // Mapează 'Numerar' la 'Cash'
            deliveryMethod = mapDeliveryMethod(deliveryMethod); // Mapează 'Livrare prin curier' la 'Curier'

            // Verifică dacă valorile sunt corecte
            if (!isValidEnumValue(paymentMethod, new String[]{"Card", "Cash"}) || !isValidEnumValue(deliveryMethod, new String[]{"Curier", "Showroom"})) {
                Toast.makeText(OrderConfirmationActivity.this, "Metodă de plată sau livrare invalidă", Toast.LENGTH_SHORT).show();
                return; // Oprește executarea dacă valoarea nu este validă
            }

            // Verifică dacă câmpurile nu sunt goale
            if (!fullName.isEmpty() && !address.isEmpty()) {
                new SubmitOrderTask().execute(fullName, address, paymentMethod, deliveryMethod, cartItems);
            } else {
                Toast.makeText(OrderConfirmationActivity.this, "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metodă pentru a mapa metoda de plată
    private String mapPaymentMethod(String paymentMethod) {
        if (paymentMethod.equals("Numerar")) {
            return "Cash";  // Mapează 'Numerar' la 'Cash'
        } else {
            return paymentMethod;  // Păstrează 'Card' așa cum este
        }
    }

    // Metodă pentru a mapa metoda de livrare
    private String mapDeliveryMethod(String deliveryMethod) {
        if (deliveryMethod.equals("Livrare prin curier")) {
            return "Curier";  // Mapează 'Livrare prin curier' la 'Curier'
        } else if (deliveryMethod.equals("Ridicare din showroom")) {
            return "Showroom";  // Mapează 'Ridicare din showroom' la 'Showroom'
        } else {
            return deliveryMethod;
        }
    }

    private class SubmitOrderTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String fullName = (String) params[0];
            String address = (String) params[1];
            String paymentMethod = (String) params[2];
            String deliveryMethod = (String) params[3];
            List<CartItem> cartItems = (List<CartItem>) params[4];

            // Calculează prețul total
            double totalPrice = calculateTotalPrice(cartItems);

            // Salvează comanda în baza de date
            saveOrderToDatabase(fullName, address, paymentMethod, deliveryMethod, totalPrice, cartItems);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Afișează un mesaj de confirmare
            Toast.makeText(OrderConfirmationActivity.this, "Comanda a fost plasată cu succes!", Toast.LENGTH_SHORT).show();

            // Navighează la ClientMainActivity
            Intent intent = new Intent(OrderConfirmationActivity.this, ClientMainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private double calculateTotalPrice(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductPrice() * item.getQuantity();
        }
        return total;
    }

    private void saveOrderToDatabase(String fullName, String address, String paymentMethod, String deliveryMethod, double totalPrice, List<CartItem> cartItems) {
        DBHelper dbHelper = new DBHelper();
        try (Connection con = dbHelper.CONN()) {
            if (con != null) {
                // 1. Crează comanda în tabelul Orders
                String orderQuery = "INSERT INTO Orders (user_id, full_name, address, payment_method, delivery_method, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, userId);
                stmt.setString(2, fullName);
                stmt.setString(3, address);
                stmt.setString(4, paymentMethod);
                stmt.setString(5, deliveryMethod);  // Livrare "Curier" sau "Showroom"
                stmt.setDouble(6, totalPrice); // Prețul total
                stmt.executeUpdate();

                // Obține ID-ul comenzii
                ResultSet rs = stmt.getGeneratedKeys();
                int orderId = -1;
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }

                // 2. Adaugă produsele în tabelul OrderItems
                for (CartItem item : cartItems) {
                    String orderItemQuery = "INSERT INTO OrderItems (order_id, product_name, quantity) VALUES (?, ?, ?)";
                    PreparedStatement orderItemStmt = con.prepareStatement(orderItemQuery);
                    orderItemStmt.setInt(1, orderId); // ID-ul comenzii
                    orderItemStmt.setString(2, item.getProductName()); // Numele produsului
                    orderItemStmt.setInt(3, item.getQuantity()); // Cantitatea produsului
                    orderItemStmt.executeUpdate();

                    // 3. Actualizează stocul produsului
                    String updateStockQuery = "UPDATE Products SET stock = stock - ? WHERE name = ?";
                    PreparedStatement updateStockStmt = con.prepareStatement(updateStockQuery);
                    updateStockStmt.setInt(1, item.getQuantity()); // Scade cantitatea
                    updateStockStmt.setString(2, item.getProductName()); // Numele produsului
                    updateStockStmt.executeUpdate();
                }

                // Șterge coșul de cumpărături
                CartManager cartManager = new CartManager(OrderConfirmationActivity.this);
                cartManager.saveCartItems(new ArrayList<>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEnumValue(String value, String[] validValues) {
        for (String validValue : validValues) {
            if (validValue.equals(value)) {
                return true;
            }
        }
        return false;
    }
}

package com.example.ecommerceapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.adapters.ReportAdapter;
import com.example.ecommerceapp.models.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReports;
    private ReportAdapter reportAdapter;
    private ArrayList<Report> reportList;
    private DBHelper dbHelper;

    private Button selectStartDateButton, selectEndDateButton, generateReportButton;
    private Spinner paymentMethodSpinner;
    private String startDate, endDate, paymentMethod;

    private double totalAmount = 0.0; // Variabila pentru totalul raportului

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();
        dbHelper = new DBHelper();

        selectStartDateButton = findViewById(R.id.selectStartDateButton);
        selectEndDateButton = findViewById(R.id.selectEndDateButton);
        generateReportButton = findViewById(R.id.generateReportButton);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);

        // Setează adapterul pentru Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // Selectează un item din Spinner
        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                paymentMethod = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Dacă nu s-a selectat nimic, setează un string gol
                paymentMethod = "";
            }
        });

        // Afișează DatePickerDialog pentru data de început
        selectStartDateButton.setOnClickListener(v -> {
            showDatePickerDialog(true);
        });

        // Afișează DatePickerDialog pentru data de sfârșit
        selectEndDateButton.setOnClickListener(v -> {
            showDatePickerDialog(false);
        });

        // Generează raportul pe baza selecțiilor
        generateReportButton.setOnClickListener(v -> {
            generateReport();
        });
    }

    // Funcția pentru a arăta DatePickerDialog
    private void showDatePickerDialog(final boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ReportsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        if (isStartDate) {
                            startDate = selectedDate;
                            selectStartDateButton.setText("Data de început: " + startDate);
                        } else {
                            endDate = selectedDate;
                            selectEndDateButton.setText("Data de sfârșit: " + endDate);
                        }
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    // Generarea raportului în funcție de selecțiile făcute
    private void generateReport() {
        // Verifică dacă utilizatorul a selectat o metodă de plată
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            Toast.makeText(ReportsActivity.this, "Te rog selectează o metodă de plată!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            Toast.makeText(ReportsActivity.this, "Te rog selectează datele de început și sfârșit!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = dbHelper.CONN();
                if (conn != null) {
                    try {
                        String query = "SELECT oi.product_name, oi.quantity, p.price, SUM(oi.quantity * p.price) AS total_value " +
                                "FROM orderitems oi " +
                                "JOIN orders o ON oi.order_id = o.id " +
                                "JOIN products p ON oi.product_name = p.name " +
                                "WHERE o.payment_method = ? AND o.created_at BETWEEN ? AND ? " +
                                "GROUP BY oi.product_name, oi.quantity, p.price";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, paymentMethod);
                        stmt.setString(2, startDate);
                        stmt.setString(3, endDate);

                        ResultSet rs = stmt.executeQuery();

                        final ArrayList<Report> tempReportList = new ArrayList<>();
                        totalAmount = 0.0;  // Resetăm totalAmount înainte de fiecare calcul

                        while (rs.next()) {
                            String productName = rs.getString("product_name");
                            int quantity = rs.getInt("quantity");
                            double price = rs.getDouble("price");
                            double totalValue = rs.getDouble("total_value");

                            // Adăugăm produsul în lista de rapoarte
                            Report report = new Report(productName, quantity, price, totalValue);
                            tempReportList.add(report);

                            // Calculăm suma totală
                            totalAmount += totalValue;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Afișăm rapoartele în RecyclerView
                                reportAdapter = new ReportAdapter(tempReportList);
                                recyclerViewReports.setAdapter(reportAdapter);

                                // Afișăm suma totală la final
                                TextView totalAmountTextView = findViewById(R.id.totalAmountTextView);
                                totalAmountTextView.setText("Suma totală: " + totalAmount+ " RON");
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReportsActivity.this, "Eroare la generarea raportului", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();  // Pornim thread-ul
    }
}

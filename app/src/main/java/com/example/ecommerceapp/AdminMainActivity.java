package com.example.ecommerceapp;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminMainActivity extends AppCompatActivity {

    private Button manageProductsButton, viewReportsButton, updateProductInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        manageProductsButton = findViewById(R.id.manageProductsButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);
        updateProductInfoButton = findViewById(R.id.updateProductInfoButton);

        // Gestionare Produse
//        manageProductsButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(AdminMainActivity.this, ManageProductsActivity.class);
////                startActivity(intent);
////            }
//        });

//        // Vizualizare Rapoarte
//        viewReportsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminMainActivity.this, ViewReportsActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // Actualizare Informații Produse
//        updateProductInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminMainActivity.this, UpdateProductInfoActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}

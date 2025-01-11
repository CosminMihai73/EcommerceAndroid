package com.example.ecommerceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.db.DBHelper;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.adapters.UsersAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ArrayList<User> userList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        dbHelper = new DBHelper();

        loadUsers();
    }

    private void loadUsers() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = dbHelper.CONN();
                if (conn != null) {
                    try {
                        String query = "SELECT id, username, email, type FROM users";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        final ArrayList<User> tempUserList = new ArrayList<>();
                        while (rs.next()) {
                            User user = new User(
                                    rs.getInt("id"),
                                    rs.getString("username"),
                                    rs.getString("email"),
                                    rs.getString("type")
                            );
                            tempUserList.add(user);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter = new UsersAdapter(tempUserList, new UsersAdapter.OnUserClickListener() {
                                    @Override
                                    public void onChangeUserTypeClick(User user) {

                                        changeUserType(user);
                                    }
                                });

                                recyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ManageUsersActivity.this, "Eroare la incarcarea utilizatorilor", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void changeUserType(final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = dbHelper.CONN();
                if (conn != null) {
                    try {

                        String newType = user.getType().equals("Client") ? "Administrator" : "Client";
                        String query = "UPDATE users SET type = ? WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, newType);
                        stmt.setInt(2, user.getId());

                        stmt.executeUpdate();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadUsers();
                                Toast.makeText(ManageUsersActivity.this, "Utilizatorul a fost modificat " + newType, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ManageUsersActivity.this, "Eroare la modifcarea utilizatorului", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }
}

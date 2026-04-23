package com.example.praktika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity7 extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvPrivacy, tvTerms, btnLogout;
    private SwitchCompat switchNotifications;

    // Нижняя навигация
    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        tvTerms = findViewById(R.id.tvTerms);
        btnLogout = findViewById(R.id.btnLogout);
        switchNotifications = findViewById(R.id.switchNotifications);

        // Нижняя навигация
        navHome = findViewById(R.id.navHome);
        navCatalog = findViewById(R.id.navCatalog);
        navProjects = findViewById(R.id.navProjects);
        navProfile = findViewById(R.id.navProfile);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Пользователь");
        String userEmail = prefs.getString("user_email", "email@example.com");

        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);

        // Мои заказы
        findViewById(R.id.btnMyOrders).setOnClickListener(v -> showMyOrders());

        // Переключатель уведомлений
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Уведомления включены" : "Уведомления выключены",
                    Toast.LENGTH_SHORT).show();
        });

        tvPrivacy.setOnClickListener(v ->
                Toast.makeText(this, "Политика конфиденциальности", Toast.LENGTH_SHORT).show());

        tvTerms.setOnClickListener(v ->
                Toast.makeText(this, "Пользовательское соглашение", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user_data", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
            ApiClient.clearAuth();

            Intent intent = new Intent(MainActivity7.this, MainActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity7.this, HomeActivity.class));
            finish();
        });
        navCatalog.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity7.this, CatalogActivity.class));
            finish();
        });
        navProjects.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity7.this, ProjectsActivity.class));
            finish();
        });
        navProfile.setOnClickListener(v -> {});
    }

    private void showMyOrders() {
        String userId = ApiClient.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            userId = prefs.getString("user_id", "");
        }

        if (userId.isEmpty()) {
            Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Загрузка заказов...", Toast.LENGTH_SHORT).show();

        ApiClient.getOrders(userId, new ApiClient.OrdersCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                runOnUiThread(() -> displayOrders(jsonResponse));
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity7.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void displayOrders(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray items = response.getJSONArray("items");

            if (items.length() == 0) {
                Toast.makeText(this, "У вас пока нет заказов", Toast.LENGTH_LONG).show();
                return;
            }

            StringBuilder orders = new StringBuilder();
            for (int i = 0; i < items.length(); i++) {
                JSONObject order = items.getJSONObject(i);
                String orderId = order.optString("id", "???");
                double total = order.optDouble("total", 0);
                String status = order.optString("status", "pending");
                orders.append("Заказ #").append(orderId.substring(0, Math.min(8, orderId.length())))
                        .append(": ").append(total).append(" ₽")
                        .append(" (").append(status).append(")\n");
            }

            Toast.makeText(this, orders.toString(), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
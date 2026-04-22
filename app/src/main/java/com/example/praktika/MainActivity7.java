package com.example.praktika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity7 extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvPrivacy, tvTerms, btnLogout;
    private SwitchCompat switchNotifications;

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

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Пользователь");
        String userEmail = prefs.getString("user_email", "email@example.com");

        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);

        // Мои заказы
        findViewById(R.id.btnMyOrders).setOnClickListener(v ->
                Toast.makeText(this, "Мои заказы", Toast.LENGTH_SHORT).show());

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
            // Очищаем данные пользователя
            getSharedPreferences("user_data", MODE_PRIVATE).edit().clear().apply();

            Intent intent = new Intent(MainActivity7.this, MainActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
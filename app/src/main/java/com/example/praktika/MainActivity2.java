package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivEye;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView tvBottomHint = findViewById(R.id.tvBottomHint);
        tvBottomHint.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
        });


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivEye = findViewById(R.id.ivEye);
        Button btnNext = findViewById(R.id.btnNext);
        TextView tvRegister = findViewById(R.id.tvRegister);
        Button btnVk = findViewById(R.id.btnVk);
        Button btnYandex = findViewById(R.id.btnYandex);

        // Глаз для пароля
        ivEye.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivEye.setImageResource(R.drawable.ic_eye_close);
                isPasswordVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivEye.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Кнопка Далее
        btnNext.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        // Зарегистрироваться
        tvRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Переход на регистрацию", Toast.LENGTH_SHORT).show();
        });

        // Социальные кнопки
        btnVk.setOnClickListener(v -> Toast.makeText(this, "Вход через VK", Toast.LENGTH_SHORT).show());
        btnYandex.setOnClickListener(v -> Toast.makeText(this, "Вход через Yandex", Toast.LENGTH_SHORT).show());
    }
}
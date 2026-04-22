package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity5 extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivEyeNew, ivEyeConfirm;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        try {
            etNewPassword = findViewById(R.id.etNewPassword);
            etConfirmPassword = findViewById(R.id.etConfirmPassword);
            ivEyeNew = findViewById(R.id.ivEyeNew);
            ivEyeConfirm = findViewById(R.id.ivEyeConfirm);
            Button btnSave = findViewById(R.id.btnSave);

            // Проверка на null
            if (etNewPassword == null || etConfirmPassword == null || btnSave == null) {
                Toast.makeText(this, "Ошибка инициализации экрана", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ivEyeNew.setOnClickListener(v -> {
                if (isNewPasswordVisible) {
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEyeNew.setImageResource(R.drawable.ic_eye_close);
                    isNewPasswordVisible = false;
                } else {
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEyeNew.setImageResource(R.drawable.ic_eye_open);
                    isNewPasswordVisible = true;
                }
                etNewPassword.setSelection(etNewPassword.getText().length());
            });

            ivEyeConfirm.setOnClickListener(v -> {
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEyeConfirm.setImageResource(R.drawable.ic_eye_close);
                    isConfirmPasswordVisible = false;
                } else {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEyeConfirm.setImageResource(R.drawable.ic_eye_open);
                    isConfirmPasswordVisible = true;
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            });

            btnSave.setOnClickListener(v -> {
                String password = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (password.isEmpty()) {
                    Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Сохраняем пароль
                getSharedPreferences("user_data", MODE_PRIVATE)
                        .edit()
                        .putString("user_password", password)
                        .apply();

                Toast.makeText(this, "Пароль сохранен!", Toast.LENGTH_SHORT).show();

                // Переход на экран установки PIN-кода
                Intent intent = new Intent(MainActivity5.this, MainActivity6.class);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
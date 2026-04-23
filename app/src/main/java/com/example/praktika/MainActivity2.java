package com.example.praktika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivEye;
    private Button btnNext;
    private TextView tvRegister;
    private boolean isPasswordVisible = false;
    private boolean isEmailValid = false;
    private boolean isPasswordNotEmpty = false;
    private boolean hasShownEmailError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initViews();
        setupTextWatchers();
        setupEmailFocusListener();
        setupEyeIcon();
        setupClickListeners();
        updateNextButtonState();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivEye = findViewById(R.id.ivEye);
        btnNext = findViewById(R.id.btnNext);
        tvRegister = findViewById(R.id.tvRegister);
        btnNext.setEnabled(false);
    }

    private void setupTextWatchers() {
        TextWatcher emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                isEmailValid = !TextUtils.isEmpty(email) &&
                        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

                if (hasShownEmailError && !TextUtils.isEmpty(s)) {
                    hasShownEmailError = false;
                    etEmail.setError(null);
                }
                updateNextButtonState();
            }
        };

        TextWatcher passWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                isPasswordNotEmpty = !TextUtils.isEmpty(password);
                etPassword.setError(null);
                updateNextButtonState();
            }
        };

        etEmail.addTextChangedListener(emailWatcher);
        etPassword.addTextChangedListener(passWatcher);
    }

    private void setupEmailFocusListener() {
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String email = etEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !isEmailValid && !hasShownEmailError) {
                    etEmail.setError("Введите корректный email");
                    etEmail.requestFocus();
                    hasShownEmailError = true;
                }
            }
        });
    }

    private void setupEyeIcon() {
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                android.graphics.drawable.Drawable[] drawables = etPassword.getCompoundDrawables();
                if (drawables[2] != null) {
                    int iconLeft = etPassword.getRight() - etPassword.getPaddingRight() - drawables[2].getBounds().width();
                    int iconRight = etPassword.getRight() - etPassword.getPaddingRight();
                    if (event.getRawX() >= iconLeft && event.getRawX() <= iconRight) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
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
    }

    private void setupClickListeners() {
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
            startActivity(intent);
        });

        btnNext.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            authenticateUser(email, password);
        });
    }

    private void authenticateUser(String email, String password) {
        btnNext.setText("Вход...");
        btnNext.setEnabled(false);

        ApiClient.login(email, password, new ApiClient.LoginCallback() {
            @Override
            public void onSuccess(String token, String userId, String email) {
                runOnUiThread(() -> {
                    saveAuthData(token, userId, email);
                    Toast.makeText(MainActivity2.this, "✅ Добро пожаловать!", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    String hasPin = prefs.getString("user_pin", null);

                    Intent intent;
                    if (hasPin == null || hasPin.isEmpty()) {
                        intent = new Intent(MainActivity2.this, HomeActivity.class);
                    } else {
                        intent = new Intent(MainActivity2.this, MainActivity3.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    btnNext.setText("Далее");
                    btnNext.setEnabled(true);
                    etPassword.setError("Неверный пароль");
                    Toast.makeText(MainActivity2.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveAuthData(String token, String userId, String email) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit()
                .putString("user_token", token)
                .putString("user_id", userId)
                .putString("user_email", email)
                .putBoolean("is_logged_in", true)
                .apply();
        ApiClient.setAuthData(token, userId, email);
    }

    private void updateNextButtonState() {
        if (isEmailValid && isPasswordNotEmpty) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.bg_button_continue);
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackgroundResource(R.drawable.bg_button_outline_blue);
        }
    }
}
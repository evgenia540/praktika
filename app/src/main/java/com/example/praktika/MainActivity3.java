package com.example.praktika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity3 extends AppCompatActivity {

    private StringBuilder pinCode = new StringBuilder();
    private String correctPin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        try {
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedPin = prefs.getString("user_pin", null);

            // Если PIN не установлен, сразу идем в Home
            if (savedPin == null || savedPin.isEmpty()) {
                goToHome();
                return;
            }

            correctPin = savedPin;

            setupButton(R.id.btn1, 1);
            setupButton(R.id.btn2, 2);
            setupButton(R.id.btn3, 3);
            setupButton(R.id.btn4, 4);
            setupButton(R.id.btn5, 5);
            setupButton(R.id.btn6, 6);
            setupButton(R.id.btn7, 7);
            setupButton(R.id.btn8, 8);
            setupButton(R.id.btn9, 9);
            setupButton(R.id.btn0, 0);

            CardView btnDelete = findViewById(R.id.btnDelete);
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> deleteDigit());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            goToHome();
        }
    }

    private void setupButton(int id, int digit) {
        CardView button = findViewById(id);
        if (button != null) {
            button.setOnClickListener(v -> addDigit(digit));
        }
    }

    private void addDigit(int digit) {
        if (pinCode.length() < 4) {
            pinCode.append(digit);
            updateDots();

            if (pinCode.length() == 4) {
                checkPinCode();
            }
        }
    }

    private void checkPinCode() {
        String enteredPin = pinCode.toString();
        if (enteredPin.equals(correctPin)) {
            Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
            goToHome();
        } else {
            Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show();
            pinCode.setLength(0);
            updateDots();
        }
    }

    private void deleteDigit() {
        if (pinCode.length() > 0) {
            pinCode.deleteCharAt(pinCode.length() - 1);
            updateDots();
        }
    }

    private void updateDots() {
        try {
            int[] dotIds = {R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4};
            for (int i = 0; i < dotIds.length; i++) {
                View dot = findViewById(dotIds[i]);
                if (dot != null) {
                    if (i < pinCode.length()) {
                        dot.setBackgroundResource(R.drawable.dot_filled);
                    } else {
                        dot.setBackgroundResource(R.drawable.dot_empty);
                    }
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки с точками
        }
    }

    private void goToHome() {
        Intent intent = new Intent(MainActivity3.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
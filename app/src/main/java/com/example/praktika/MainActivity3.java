package com.example.praktika;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView tvPinDisplay;
    private StringBuilder pinCode = new StringBuilder();
    private final int MAX_PIN = 6;
    private final String CORRECT_PIN = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        tvPinDisplay = findViewById(R.id.tvPinDisplay);

        // Кнопка 1
        findViewById(R.id.btn1).setOnClickListener(v -> addDigit(1));
        findViewById(R.id.btn2).setOnClickListener(v -> addDigit(2));
        findViewById(R.id.btn3).setOnClickListener(v -> addDigit(3));
        findViewById(R.id.btn4).setOnClickListener(v -> addDigit(4));
        findViewById(R.id.btn5).setOnClickListener(v -> addDigit(5));
        findViewById(R.id.btn6).setOnClickListener(v -> addDigit(6));
        findViewById(R.id.btn7).setOnClickListener(v -> addDigit(7));
        findViewById(R.id.btn8).setOnClickListener(v -> addDigit(8));
        findViewById(R.id.btn9).setOnClickListener(v -> addDigit(9));
        findViewById(R.id.btn0).setOnClickListener(v -> addDigit(0));

        // Кнопка удаления (⌫)
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (pinCode.length() > 0) {
                pinCode.deleteCharAt(pinCode.length() - 1);
                updateDisplay();
            }
        });
    }

    private void addDigit(int digit) {
        if (pinCode.length() < MAX_PIN) {
            pinCode.append(digit);
            updateDisplay();
        }

        // Когда набрал 6 цифр — проверяем
        if (pinCode.length() == MAX_PIN) {
            new Handler().postDelayed(this::checkPin, 200);
        }
    }

    private void updateDisplay() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < pinCode.length(); i++) {
            stars.append("●");
        }
        tvPinDisplay.setText(stars.toString());
    }

    private void checkPin() {
        if (pinCode.toString().equals(CORRECT_PIN)) {
            Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
            // Тут можно открыть главное меню
        } else {
            Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show();
            pinCode.setLength(0);
            updateDisplay();
        }
    }
}
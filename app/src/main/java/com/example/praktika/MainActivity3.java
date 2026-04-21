package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private StringBuilder pinCode = new StringBuilder();
    private final String CORRECT_PIN = "1234"; // Теперь 4 цифры

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

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

        // Кнопка удаления
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteDigit());
    }

    private void addDigit(int digit) {
        if (pinCode.length() < 4) { // Теперь 4 цифры
            pinCode.append(digit);
            updateDots();

            if (pinCode.length() == 4) {
                if (pinCode.toString().equals(CORRECT_PIN)) {
                    Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                    // Переход на главный экран
                    Intent intent = new Intent(MainActivity3.this, MainActivity7.class);
                    startActivity(intent);
                    finish();
                    pinCode.setLength(0);
                } else {
                    Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show();
                    pinCode.setLength(0);
                    updateDots();
                }
            }
        }
    }

    private void deleteDigit() {
        if (pinCode.length() > 0) {
            pinCode.deleteCharAt(pinCode.length() - 1);
            updateDots();
        }
    }

    private void updateDots() {
        int[] dotIds = {R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4}; // Только 4 точки
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
    }
}
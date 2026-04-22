package com.example.praktika;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity6 extends AppCompatActivity {

    private StringBuilder pinCode = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        // Настройка кнопок
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
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteDigit());
    }

    private void addDigit(int digit) {
        if (pinCode.length() < 4) {
            pinCode.append(digit);
            updateDots();

            if (pinCode.length() == 4) {
                SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                prefs.edit().putString("user_pin", pinCode.toString()).apply();

                Toast.makeText(this, "PIN-код установлен!", Toast.LENGTH_SHORT).show();

                // Переход на HomeActivity
                Intent intent = new Intent(MainActivity6.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
    }
}
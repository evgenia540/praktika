package com.example.praktika;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity7 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Добро пожаловать в приложение!");
    }
}
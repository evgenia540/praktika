package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Задержка 2 секунды, потом переход на LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Переход на экран входа
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                finish(); // Закрываем MainActivity, чтобы при нажатии "Назад" не возвращаться на заставку
            }
        }, 2000); // 2000 миллисекунд = 2 секунды
    }
}
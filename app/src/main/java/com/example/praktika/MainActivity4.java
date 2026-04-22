package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    private EditText etFirstName, etPatronymic, etLastName, etBirthDate, etEmail;
    private Spinner spinnerGender;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        etFirstName = findViewById(R.id.etFirstName);
        etPatronymic = findViewById(R.id.etPatronymic);
        etLastName = findViewById(R.id.etLastName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etEmail = findViewById(R.id.etEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnNext = findViewById(R.id.btnNext);

        // Настройка Spinner
        String[] genders = {"Мужской", "Женский"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        btnNext.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String patronymic = etPatronymic.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (firstName.isEmpty()) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastName.isEmpty()) {
                Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
                return;
            }
            if (birthDate.isEmpty()) {
                Toast.makeText(this, "Введите дату рождения", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем данные пользователя
            getSharedPreferences("user_data", MODE_PRIVATE)
                    .edit()
                    .putString("user_name", firstName + " " + lastName)
                    .putString("user_email", email)
                    .putString("user_birth_date", birthDate)
                    .putString("user_gender", spinnerGender.getSelectedItem().toString())
                    .apply();

            Toast.makeText(this, "Профиль создан!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
            startActivity(intent);
        });
    }
}
package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity4 extends AppCompatActivity {

    private EditText etFirstName, etPatronymic, etLastName, etBirthDate, etEmail;
    private Spinner spinnerGender;
    private Button btnNext;

    private static final String API_BASE = "http://2.nntc.nnov.ru:8900/api";
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
            String password = generateRandomPassword(); // Генерируем временный пароль

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

            registerUser(email, password, firstName, lastName, patronymic, birthDate);
        });
    }

    private String generateRandomPassword() {
        // Генерируем временный пароль (можно заменить на ввод от пользователя)
        return "12345678";
    }

    private void registerUser(String email, String password, String firstName,
                              String lastName, String patronymic, String birthDate) {

        btnNext.setText("Регистрация...");
        btnNext.setEnabled(false);

        String gender = spinnerGender.getSelectedItem().toString().equals("Мужской") ? "male" : "female";

        String json = "{" +
                "\"email\":\"" + escapeJson(email) + "\"," +
                "\"password\":\"" + escapeJson(password) + "\"," +
                "\"passwordConfirm\":\"" + escapeJson(password) + "\"," +
                "\"first_name\":\"" + escapeJson(firstName) + "\"," +
                "\"last_name\":\"" + escapeJson(lastName) + "\"," +
                "\"patronymic\":\"" + escapeJson(patronymic) + "\"," +
                "\"birthday\":\"" + birthDate + "\"," +
                "\"gender\":\"" + gender + "\"" +
                "}";

        android.util.Log.d("REGISTER_DEBUG", "Request: " + json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/users/records")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> {
                    btnNext.setText("Далее");
                    btnNext.setEnabled(true);
                    Toast.makeText(MainActivity4.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                android.util.Log.d("REGISTER_DEBUG", "Response code: " + response.code());
                android.util.Log.d("REGISTER_DEBUG", "Response body: " + responseData);

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Сохраняем данные локально
                        getSharedPreferences("user_data", MODE_PRIVATE)
                                .edit()
                                .putString("user_name", firstName + " " + lastName)
                                .putString("user_email", email)
                                .putString("user_birth_date", birthDate)
                                .putString("user_gender", spinnerGender.getSelectedItem().toString())
                                .putString("user_password", password)
                                .apply();

                        Toast.makeText(MainActivity4.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    String errorMsg = "Ошибка регистрации";
                    try {
                        JSONObject errorJson = new JSONObject(responseData);
                        if (errorJson.has("message")) {
                            errorMsg = errorJson.getString("message");
                        }
                        if (errorJson.has("data")) {
                            JSONObject data = errorJson.getJSONObject("data");
                            if (data.has("email")) {
                                errorMsg = "Пользователь с таким email уже существует";
                            }
                        }
                    } catch (JSONException e) {
                        // Игнорируем
                    }

                    String finalErrorMsg = errorMsg;
                    runOnUiThread(() -> {
                        btnNext.setText("Далее");
                        btnNext.setEnabled(true);
                        Toast.makeText(MainActivity4.this, finalErrorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
package com.example.praktika;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final String API_BASE = "http://2.nntc.nnov.ru:8900/api";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static String authToken = null;
    private static String currentUserId = null;
    private static String currentUserEmail = null;

    // ==================== АВТОРИЗАЦИЯ ====================

    public static void login(String email, String password, LoginCallback callback) {
        String json = "{\"identity\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(password) + "\"}";

        android.util.Log.d("API_DEBUG", "========== LOGIN REQUEST ==========");
        android.util.Log.d("API_DEBUG", "URL: " + API_BASE + "/collections/users/auth-with-password");
        android.util.Log.d("API_DEBUG", "Body: " + json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/users/auth-with-password")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                android.util.Log.e("API_DEBUG", "Login network error: " + e.getMessage());
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                android.util.Log.d("API_DEBUG", "Login response code: " + response.code());
                android.util.Log.d("API_DEBUG", "Login response body: " + responseData);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResp = new JSONObject(responseData);

                        if (jsonResp.has("token")) {
                            String token = jsonResp.getString("token");
                            String userId = null;
                            String userEmail = null;

                            if (jsonResp.has("record")) {
                                JSONObject record = jsonResp.getJSONObject("record");
                                userId = record.getString("id");
                                userEmail = record.getString("email");
                            }

                            authToken = token;
                            currentUserId = userId;
                            currentUserEmail = userEmail;

                            android.util.Log.d("API_DEBUG", "Login SUCCESS! Token: " + token.substring(0, Math.min(20, token.length())) + "...");

                            callback.onSuccess(token, userId, userEmail);
                        } else {
                            callback.onFailure("Ответ сервера не содержит токен");
                        }
                    } catch (JSONException e) {
                        android.util.Log.e("API_DEBUG", "JSON parse error: " + e.getMessage());
                        callback.onFailure("Ошибка парсинга ответа");
                    }
                } else {
                    String errorMsg = "Неверный email или пароль";
                    try {
                        JSONObject errorJson = new JSONObject(responseData);
                        if (errorJson.has("message")) {
                            errorMsg = errorJson.getString("message");
                        }
                    } catch (JSONException e) {
                        // Игнорируем
                    }
                    callback.onFailure(errorMsg);
                }
            }
        });
    }

    // ==================== РЕГИСТРАЦИЯ ====================

    public static void register(String email, String password, String firstName,
                                String lastName, String patronymic, String birthday,
                                String gender, RegisterCallback callback) {
        String json = "{" +
                "\"email\":\"" + escapeJson(email) + "\"," +
                "\"password\":\"" + escapeJson(password) + "\"," +
                "\"passwordConfirm\":\"" + escapeJson(password) + "\"," +
                "\"first_name\":\"" + escapeJson(firstName) + "\"," +
                "\"last_name\":\"" + escapeJson(lastName) + "\"," +
                "\"patronymic\":\"" + escapeJson(patronymic) + "\"," +
                "\"birthday\":\"" + birthday + "\"," +
                "\"gender\":\"" + gender + "\"" +
                "}";

        android.util.Log.d("API_DEBUG", "========== REGISTER REQUEST ==========");
        android.util.Log.d("API_DEBUG", "URL: " + API_BASE + "/collections/users/records");
        android.util.Log.d("API_DEBUG", "Body: " + json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/users/records")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                android.util.Log.e("API_DEBUG", "Register network error: " + e.getMessage());
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                android.util.Log.d("API_DEBUG", "Register response code: " + response.code());
                android.util.Log.d("API_DEBUG", "Register response body: " + responseData);

                if (response.isSuccessful()) {
                    android.util.Log.d("API_DEBUG", "Register SUCCESS!");
                    callback.onSuccess("Регистрация успешна");
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
                    callback.onFailure(errorMsg);
                }
            }
        });
    }

    // ==================== ТОВАРЫ ====================

    public static void getProducts(ProductsCallback callback) {
        android.util.Log.d("API_DEBUG", "========== GET PRODUCTS REQUEST ==========");
        android.util.Log.d("API_DEBUG", "URL: " + API_BASE + "/collections/products/records");

        Request request = new Request.Builder()
                .url(API_BASE + "/collections/products/records")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                android.util.Log.e("API_DEBUG", "Get products network error: " + e.getMessage());
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                android.util.Log.d("API_DEBUG", "Get products response code: " + response.code());
                android.util.Log.d("API_DEBUG", "Get products response body: " + responseData);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResp = new JSONObject(responseData);
                        JSONArray items = jsonResp.getJSONArray("items");
                        List<Product> products = new ArrayList<>();

                        android.util.Log.d("API_DEBUG", "Products count from server: " + items.length());

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String name = item.getString("name");
                            double price = item.getDouble("price");
                            String description = item.optString("description", "");

                            android.util.Log.d("API_DEBUG", "Product " + i + ": " + name + " - " + price + " ₽");

                            Product product = new Product(name, String.format("%.0f", price) + " ₽", 0, description);
                            products.add(product);
                        }

                        if (products.isEmpty()) {
                            android.util.Log.w("API_DEBUG", "No products found on server!");
                            callback.onFailure("На сервере нет товаров");
                        } else {
                            android.util.Log.d("API_DEBUG", "Products loaded successfully: " + products.size());
                            callback.onSuccess(products);
                        }

                    } catch (JSONException e) {
                        android.util.Log.e("API_DEBUG", "JSON parse error: " + e.getMessage());
                        callback.onFailure("Ошибка парсинга товаров: " + e.getMessage());
                    }
                } else {
                    android.util.Log.e("API_DEBUG", "HTTP error: " + response.code());
                    callback.onFailure("Ошибка сервера: " + response.code());
                }
            }
        });
    }

    // ==================== ДОБАВЛЕНИЕ ТОВАРА (для админа) ====================

    public static void addProduct(String name, double price, String description, String category, AddProductCallback callback) {
        String json = "{" +
                "\"name\":\"" + escapeJson(name) + "\"," +
                "\"price\":" + price + "," +
                "\"description\":\"" + escapeJson(description) + "\"," +
                "\"category\":\"" + escapeJson(category) + "\"" +
                "}";

        android.util.Log.d("API_DEBUG", "========== ADD PRODUCT REQUEST ==========");
        android.util.Log.d("API_DEBUG", "URL: " + API_BASE + "/collections/products/records");
        android.util.Log.d("API_DEBUG", "Body: " + json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/products/records")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", authToken != null ? authToken : "")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                android.util.Log.e("API_DEBUG", "Add product error: " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                android.util.Log.d("API_DEBUG", "Add product response code: " + response.code());
                android.util.Log.d("API_DEBUG", "Add product response body: " + responseData);

                if (response.isSuccessful()) {
                    callback.onSuccess("Товар добавлен");
                } else {
                    callback.onFailure("Ошибка: " + response.code());
                }
            }
        });
    }

    // ==================== ЗАКАЗЫ ====================

    public static void createOrder(String userId, double total, OrderCallback callback) {
        try {
            JSONObject order = new JSONObject();
            order.put("user_id", userId);
            order.put("total", total);
            order.put("status", "pending");

            RequestBody body = RequestBody.create(order.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_BASE + "/collections/orders/records")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", authToken != null ? authToken : "")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    android.util.Log.e("API_DEBUG", "Create order error: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        android.util.Log.d("API_DEBUG", "Order created successfully");
                        callback.onSuccess("Заказ оформлен");
                    } else {
                        android.util.Log.e("API_DEBUG", "Create order failed: " + response.code());
                        callback.onFailure("Ошибка: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            android.util.Log.e("API_DEBUG", "JSON error: " + e.getMessage());
            callback.onFailure("Ошибка формирования заказа");
        }
    }

    // ==================== ПРОЕКТЫ ====================

    public static void createProject(String userId, String name, String type, String startDate,
                                     String endDate, String projectFor, String source, String category,
                                     SimpleCallback callback) {
        try {
            JSONObject project = new JSONObject();
            project.put("user_id", userId);
            project.put("name", name);
            project.put("type", type);
            project.put("start_date", startDate);
            project.put("end_date", endDate);
            project.put("project_for", projectFor);
            project.put("source", source);
            project.put("category", category);

            RequestBody body = RequestBody.create(project.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_BASE + "/collections/projects/records")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", authToken != null ? authToken : "")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess("Проект создан");
                    } else {
                        callback.onFailure("Ошибка: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            callback.onFailure("Ошибка формирования проекта");
        }
    }

    // ==================== ГЕТТЕРЫ ====================

    public static String getAuthToken() {
        return authToken;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static void setAuthData(String token, String userId, String email) {
        authToken = token;
        currentUserId = userId;
        currentUserEmail = email;
    }

    public static void loadAuthFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        authToken = prefs.getString("user_token", null);
        currentUserId = prefs.getString("user_id", null);
        currentUserEmail = prefs.getString("user_email", null);
    }

    public static void clearAuth() {
        authToken = null;
        currentUserId = null;
        currentUserEmail = null;
    }

    public static boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ ====================

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // ==================== CALLBACK ИНТЕРФЕЙСЫ ====================

    public interface LoginCallback {
        void onSuccess(String token, String userId, String email);
        void onFailure(String error);
    }

    public interface RegisterCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface ProductsCallback {
        void onSuccess(List<Product> products);
        void onFailure(String error);
    }

    public interface AddProductCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
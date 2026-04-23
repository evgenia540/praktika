package com.example.praktika;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

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
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static String authToken = null;
    private static String currentUserId = null;
    private static String currentUserEmail = null;

    // ==================== АВТОРИЗАЦИЯ ====================

    public static void login(String email, String password, LoginCallback callback) {
        String json = "{\"identity\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(password) + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/users/auth-with-password")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResp = new JSONObject(responseData);
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

                        callback.onSuccess(token, userId, userEmail);
                    } catch (JSONException e) {
                        callback.onFailure("Ошибка парсинга ответа");
                    }
                } else {
                    callback.onFailure("Неверный email или пароль");
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

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/users/records")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess("Регистрация успешна");
                } else {
                    callback.onFailure("Ошибка регистрации");
                }
            }
        });
    }

    // ==================== ТОВАРЫ ====================

    public static void getProducts(ProductsCallback callback) {
        Request request = new Request.Builder()
                .url(API_BASE + "/collections/products/records")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResp = new JSONObject(responseData);
                        JSONArray items = jsonResp.getJSONArray("items");
                        List<Product> products = new ArrayList<>();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String name = item.getString("title");
                            double price = item.getDouble("price");
                            String description = item.optString("description", "");
                            // ВАЖНО: поле typeCloses с сервера для фильтрации
                            String category = item.optString("typeCloses", "");

                            String priceStr = String.format("%.0f", price) + " ₽";
                            Product product = new Product(name, priceStr, 0, description, category, "");
                            products.add(product);
                        }

                        callback.onSuccess(products);

                    } catch (JSONException e) {
                        callback.onFailure("Ошибка парсинга товаров");
                    }
                } else {
                    callback.onFailure("Ошибка сервера: " + response.code());
                }
            }
        });
    }

    // ==================== АКЦИИ И НОВОСТИ (БАННЕРЫ) ====================

    public static void getPromotions(PromotionsCallback callback) {
        String url = API_BASE + "/collections/promotions_and_news/records?sort=-created";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResp = new JSONObject(responseData);
                        JSONArray items = jsonResp.getJSONArray("items");
                        List<Promotion> promotions = new ArrayList<>();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String id = item.getString("id");
                            String collectionId = item.getString("collectionId");
                            String newsImage = item.optString("newsImage", "");

                            String title;
                            String description;
                            if (i == 0) {
                                title = "Лучшие цены";
                                description = "Скидки до 50% на первую покупку";
                            } else if (i == 1) {
                                title = "Новинки";
                                description = "Свежие поступления каждую неделю";
                            } else {
                                title = "Акция";
                                description = "Успейте купить";
                            }

                            Promotion promotion = new Promotion(id, collectionId, title, description, newsImage);
                            promotions.add(promotion);
                        }

                        callback.onSuccess(promotions);
                    } catch (JSONException e) {
                        callback.onFailure("Ошибка парсинга: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Ошибка сервера: " + response.code());
                }
            }
        });
    }

    public static String getPromotionImageUrl(Promotion promotion) {
        if (promotion == null || TextUtils.isEmpty(promotion.getId()) ||
                TextUtils.isEmpty(promotion.getImageName())) {
            return null;
        }
        return API_BASE + "/collections/promotions_and_news/records/" + promotion.getId() + "/" + promotion.getImageName();
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
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess("Заказ оформлен");
                    } else {
                        callback.onFailure("Ошибка: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            callback.onFailure("Ошибка формирования заказа");
        }
    }

    public static void getOrders(String userId, OrdersCallback callback) {
        String url = API_BASE + "/collections/orders/records?filter=(user_id='" + userId + "')";

        Request request = new Request.Builder()
                .url(url)
                .get()
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
                    callback.onSuccess(response.body() != null ? response.body().string() : "");
                } else {
                    callback.onFailure("Ошибка: " + response.code());
                }
            }
        });
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

    public static void getProjects(String userId, ProjectsCallback callback) {
        String url = API_BASE + "/collections/projects/records?filter=(user_id='" + userId + "')";

        Request request = new Request.Builder()
                .url(url)
                .get()
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
                    callback.onSuccess(response.body() != null ? response.body().string() : "");
                } else {
                    callback.onFailure("Ошибка: " + response.code());
                }
            }
        });
    }

    // ==================== ГЕТТЕРЫ ====================

    public static String getAuthToken() { return authToken; }
    public static String getCurrentUserId() { return currentUserId; }
    public static String getCurrentUserEmail() { return currentUserEmail; }

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

    // ==================== КЛАССЫ МОДЕЛЕЙ ====================

    public static class Promotion {
        private String id;
        private String collectionId;
        private String title;
        private String description;
        private String imageName;

        public Promotion(String id, String collectionId, String title, String description, String imageName) {
            this.id = id;
            this.collectionId = collectionId;
            this.title = title;
            this.description = description;
            this.imageName = imageName;
        }

        public String getId() { return id; }
        public String getCollectionId() { return collectionId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImageName() { return imageName; }
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

    public interface PromotionsCallback {
        void onSuccess(List<Promotion> promotions);
        void onFailure(String error);
    }

    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(String error);
    }

    public interface OrdersCallback {
        void onSuccess(String jsonResponse);
        void onFailure(String error);
    }

    public interface ProjectsCallback {
        void onSuccess(String jsonResponse);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
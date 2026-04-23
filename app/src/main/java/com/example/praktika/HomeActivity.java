package com.example.praktika;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private EditText etSearch;
    private MaterialButton btnFilterAll, btnFilterWomen, btnFilterMen;
    private LinearLayout cartBar;
    private TextView tvCartAction, tvCartTotal, tvEmptyProducts;

    // Баннеры
    private ImageView homePromoImagePrimary, homePromoImageSecondary;
    private TextView homePromoTitlePrimary, homePromoSubtitlePrimary;
    private TextView homePromoTitleSecondary, homePromoSubtitleSecondary;
    private OkHttpClient httpClient;

    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    private enum FilterType { ALL, WOMEN, MEN }
    private FilterType currentFilter = FilterType.ALL;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        httpClient = new OkHttpClient();

        initViews();
        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupBottomNavigation();

        cartBar.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));

        loadProductsFromServer();
        loadPromotionsFromServer();
    }

    private void initViews() {
        rvProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterWomen = findViewById(R.id.btnFilterWomen);
        btnFilterMen = findViewById(R.id.btnFilterMen);
        cartBar = findViewById(R.id.homeCartBar);
        tvCartAction = findViewById(R.id.homeTvCartAction);
        tvCartTotal = findViewById(R.id.homeTvCartTotal);
        tvEmptyProducts = findViewById(R.id.homeTvEmptyProducts);

        // Баннеры
        homePromoImagePrimary = findViewById(R.id.homePromoImagePrimary);
        homePromoImageSecondary = findViewById(R.id.homePromoImageSecondary);
        homePromoTitlePrimary = findViewById(R.id.homePromoTitlePrimary);
        homePromoSubtitlePrimary = findViewById(R.id.homePromoSubtitlePrimary);
        homePromoTitleSecondary = findViewById(R.id.homePromoTitleSecondary);
        homePromoSubtitleSecondary = findViewById(R.id.homePromoSubtitleSecondary);

        navHome = findViewById(R.id.navHome);
        navCatalog = findViewById(R.id.navCatalog);
        navProjects = findViewById(R.id.navProjects);
        navProfile = findViewById(R.id.navProfile);
    }

    // ==================== ЗАГРУЗКА КАРТИНОК ====================

    private void loadImageIntoView(String imageUrl, ImageView imageView, int defaultRes) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(defaultRes);
            return;
        }

        Request request = new Request.Builder()
                .url(imageUrl)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> imageView.setImageResource(defaultRes));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] imageBytes = response.body().bytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } else {
                    runOnUiThread(() -> imageView.setImageResource(defaultRes));
                }
            }
        });
    }

    // ==================== БАННЕРЫ ====================

    private void loadPromotionsFromServer() {
        ApiClient.getPromotions(new ApiClient.PromotionsCallback() {
            @Override
            public void onSuccess(List<ApiClient.Promotion> promotions) {
                runOnUiThread(() -> {
                    if (promotions != null && !promotions.isEmpty()) {
                        displayPromotions(promotions);
                    } else {
                        loadLocalPromotions();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    loadLocalPromotions();
                });
            }
        });
    }

    private void loadLocalPromotions() {
        if (homePromoTitlePrimary != null) {
            homePromoTitlePrimary.setText("Лучшие цены");
            homePromoSubtitlePrimary.setText("Скидки до 50%");
            homePromoImagePrimary.setImageResource(R.drawable.bg_promo_placeholder_teal);
        }
        if (homePromoTitleSecondary != null) {
            homePromoTitleSecondary.setText("Новинки");
            homePromoSubtitleSecondary.setText("Свежие поступления");
            homePromoImageSecondary.setImageResource(R.drawable.bg_promo_placeholder_blue);
        }
    }

    private void displayPromotions(List<ApiClient.Promotion> promotions) {
        if (promotions.size() > 0 && homePromoTitlePrimary != null) {
            ApiClient.Promotion promo1 = promotions.get(0);
            homePromoTitlePrimary.setText(promo1.getTitle());
            homePromoSubtitlePrimary.setText(promo1.getDescription());
            String imageUrl1 = ApiClient.getPromotionImageUrl(promo1);
            loadImageIntoView(imageUrl1, homePromoImagePrimary, R.drawable.bg_promo_placeholder_teal);
        }

        if (promotions.size() > 1 && homePromoTitleSecondary != null) {
            ApiClient.Promotion promo2 = promotions.get(1);
            homePromoTitleSecondary.setText(promo2.getTitle());
            homePromoSubtitleSecondary.setText(promo2.getDescription());
            String imageUrl2 = ApiClient.getPromotionImageUrl(promo2);
            loadImageIntoView(imageUrl2, homePromoImageSecondary, R.drawable.bg_promo_placeholder_blue);
        }
    }

    // ==================== ТОВАРЫ ====================

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productAdapter = new ProductAdapter(filteredList, this);
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProductsFromServer() {
        tvEmptyProducts.setText("Загрузка товаров...");
        tvEmptyProducts.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);

        ApiClient.getProducts(new ApiClient.ProductsCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                runOnUiThread(() -> {
                    if (products != null && !products.isEmpty()) {
                        productList.clear();
                        productList.addAll(products);
                        filteredList.clear();
                        filteredList.addAll(products);
                        productAdapter.notifyDataSetChanged();

                        tvEmptyProducts.setVisibility(View.GONE);
                        rvProducts.setVisibility(View.VISIBLE);
                        applyFilters();
                        updateCartBar();
                    } else {
                        loadLocalProducts();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    loadLocalProducts();
                });
            }
        });
    }

    private void loadLocalProducts() {
        productList.clear();
        productList.add(new Product("Рубашка Воскресенье", "300 ₽", 0,
                "Классическая рубашка из 100% хлопка."));
        productList.add(new Product("Шорты Вторник", "4000 ₽", 0,
                "Удобные летние шорты."));
        productList.add(new Product("Футболка Пятница", "800 ₽", 0,
                "Хлопковая футболка с принтом."));
        productList.add(new Product("Джинсы Суббота", "2500 ₽", 0,
                "Стильные джинсы прямого кроя."));
        productList.add(new Product("Куртка Зима", "5000 ₽", 0,
                "Теплая зимняя куртка."));

        filteredList.clear();
        filteredList.addAll(productList);
        productAdapter.notifyDataSetChanged();

        tvEmptyProducts.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
        applyFilters();
        updateCartBar();
    }

    // ==================== ПОИСК И ФИЛЬТРЫ ====================

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                applyFilters();
            }
        });
    }

    private void setupFilters() {
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = FilterType.ALL;
            updateFilterButtons();
            applyFilters();
        });
        btnFilterWomen.setOnClickListener(v -> {
            currentFilter = FilterType.WOMEN;
            updateFilterButtons();
            applyFilters();
        });
        btnFilterMen.setOnClickListener(v -> {
            currentFilter = FilterType.MEN;
            updateFilterButtons();
            applyFilters();
        });
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {});
        navCatalog.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CatalogActivity.class));
            finish();
        });
        navProjects.setOnClickListener(v -> {
            try {
                startActivity(new Intent(HomeActivity.this, ProjectsActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MainActivity7.class));
            finish();
        });
    }

    private void applyFilters() {
        if (productList == null) return;

        filteredList.clear();
        for (Product product : productList) {
            if (matchesQuery(product) && matchesFilter(product)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            tvEmptyProducts.setVisibility(View.VISIBLE);
            tvEmptyProducts.setText("Товары не найдены");
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyProducts.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }

        productAdapter.notifyDataSetChanged();
        updateCartBar();
    }

    private boolean matchesQuery(Product product) {
        if (TextUtils.isEmpty(currentQuery)) return true;
        String text = (product.getName() + " " + product.getDescription()).toLowerCase(Locale.getDefault());
        return text.contains(currentQuery);
    }

    private boolean matchesFilter(Product product) {
        if (currentFilter == FilterType.ALL) return true;
        String name = product.getName().toLowerCase(Locale.getDefault());
        if (currentFilter == FilterType.WOMEN) {
            return name.contains("рубашка") || name.contains("футболка") || name.contains("шорты") || name.contains("платье");
        } else {
            return name.contains("джинсы") || name.contains("куртка") || name.contains("брюки");
        }
    }

    private void updateFilterButtons() {
        updateButtonStyle(btnFilterAll, currentFilter == FilterType.ALL);
        updateButtonStyle(btnFilterWomen, currentFilter == FilterType.WOMEN);
        updateButtonStyle(btnFilterMen, currentFilter == FilterType.MEN);
    }

    private void updateButtonStyle(MaterialButton button, boolean selected) {
        if (selected) {
            button.setBackgroundResource(R.drawable.bg_button_continue);
            button.setTextColor(getColor(R.color.white));
        } else {
            button.setBackgroundResource(R.drawable.bg_button_outline_blue);
            button.setTextColor(getColor(R.color.blue_main));
        }
    }

    // ==================== КОРЗИНА ====================

    private void updateCartBar() {
        int totalCount = CartManager.getTotalQuantity(this);
        if (totalCount > 0) {
            cartBar.setVisibility(LinearLayout.VISIBLE);
            tvCartTotal.setText(CartManager.getTotalPriceText(this));
            tvCartAction.setText("В корзину (" + totalCount + ")");
        } else {
            cartBar.setVisibility(LinearLayout.GONE);
        }
    }

    public void refreshCart() {
        updateCartBar();
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBar();
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }
}
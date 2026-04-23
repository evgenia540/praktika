package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private EditText etSearch;
    private MaterialButton btnFilterAll, btnFilterWomen, btnFilterMen;
    private LinearLayout cartBar;
    private TextView tvCartAction, tvCartTotal, tvEmptyProducts;

    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    private enum FilterType { ALL, WOMEN, MEN }
    private FilterType currentFilter = FilterType.ALL;
    private String currentQuery = "";

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupBottomNavigation();

        cartBar.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));

        // Загружаем товары с сервера
        loadProductsFromServer();
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

        navHome = findViewById(R.id.navHome);
        navCatalog = findViewById(R.id.navCatalog);
        navProjects = findViewById(R.id.navProjects);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productAdapter = new ProductAdapter(filteredList, this);
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProductsFromServer() {
        if (isLoading) return;
        isLoading = true;

        tvEmptyProducts.setText("Загрузка товаров...");
        tvEmptyProducts.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);

        ApiClient.getProducts(new ApiClient.ProductsCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                runOnUiThread(() -> {
                    isLoading = false;
                    productList.clear();
                    productList.addAll(products);
                    filteredList.clear();
                    filteredList.addAll(products);
                    productAdapter.notifyDataSetChanged();

                    tvEmptyProducts.setVisibility(View.GONE);
                    rvProducts.setVisibility(View.VISIBLE);
                    applyFilters();
                    updateCartBar();

                    android.util.Log.d("HOME_DEBUG", "Загружено товаров: " + products.size());
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    isLoading = false;
                    android.util.Log.e("HOME_DEBUG", "Ошибка загрузки: " + error);
                    tvEmptyProducts.setText("Ошибка загрузки: " + error);

                    // Пробуем загрузить локальные товары как резерв
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

        Toast.makeText(this, "Загружены локальные товары", Toast.LENGTH_SHORT).show();
    }

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
                Toast.makeText(HomeActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            return name.contains("рубашка") || name.contains("футболка") || name.contains("шорты");
        } else {
            return name.contains("джинсы") || name.contains("куртка");
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
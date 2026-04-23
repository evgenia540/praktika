package com.example.praktika;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private EditText etSearch;
    private MaterialButton btnFilterAll, btnFilterWomen, btnFilterMen;
    private LinearLayout cartBar;
    private TextView tvCartAction, tvCartTotal, tvEmptyProducts;
    private ImageView ivProfile;

    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    private enum FilterType { ALL, WOMEN, MEN }
    private FilterType currentFilter = FilterType.ALL;
    private String currentQuery = "";

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupBottomNavigation();

        cartBar.setOnClickListener(v ->
                startActivity(new Intent(CatalogActivity.this, CartActivity.class)));

        ivProfile.setOnClickListener(v ->
                startActivity(new Intent(CatalogActivity.this, MainActivity7.class)));

        loadProductsFromServer();
    }

    private void initViews() {
        rvProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterWomen = findViewById(R.id.btnFilterWomen);
        btnFilterMen = findViewById(R.id.btnFilterMen);
        cartBar = findViewById(R.id.cartBar);
        tvCartAction = findViewById(R.id.tvCartAction);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvEmptyProducts = findViewById(R.id.catalogTvEmptyProducts);
        ivProfile = findViewById(R.id.ivProfile);

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

                    android.util.Log.d("CATALOG_DEBUG", "Загружено товаров: " + products.size());
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    isLoading = false;
                    android.util.Log.e("CATALOG_DEBUG", "Ошибка загрузки: " + error);
                    tvEmptyProducts.setText("Ошибка загрузки: " + error);
                    loadLocalProducts();
                });
            }
        });
    }

    private void loadLocalProducts() {
        productList.clear();
        productList.add(new Product("Рубашка Воскресенье", "300 ₽", 0,
                "Классическая рубашка из 100% хлопка.", "Женская одежда", ""));
        productList.add(new Product("Шорты Вторник", "4000 ₽", 0,
                "Удобные летние шорты.", "Женская одежда", ""));
        productList.add(new Product("Футболка Пятница", "800 ₽", 0,
                "Хлопковая футболка с принтом.", "Женская одежда", ""));
        productList.add(new Product("Джинсы Суббота", "2500 ₽", 0,
                "Стильные джинсы прямого кроя.", "Мужская одежда", ""));
        productList.add(new Product("Куртка Зима", "5000 ₽", 0,
                "Теплая зимняя куртка.", "Мужская одежда", ""));
        productList.add(new Product("Платье Весна", "3500 ₽", 0,
                "Элегантное платье из легкой ткани.", "Женская одежда", ""));
        productList.add(new Product("Брюки Осень", "2800 ₽", 0,
                "Классические брюки для офиса.", "Мужская одежда", ""));

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
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, HomeActivity.class));
            finish();
        });
        navCatalog.setOnClickListener(v -> {});
        navProjects.setOnClickListener(v -> {
            try {
                startActivity(new Intent(CatalogActivity.this, ProjectsActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, MainActivity7.class));
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

        android.util.Log.d("CATALOG_DEBUG", "Всего: " + productList.size() + ", Отфильтровано: " + filteredList.size());

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

    // ФИЛЬТРАЦИЯ ПО КАТЕГОРИИ (typeCloses с сервера)
    private boolean matchesFilter(Product product) {
        if (currentFilter == FilterType.ALL) return true;

        String category = product.getCategory();
        if (category == null || category.isEmpty()) return false;

        android.util.Log.d("CATALOG_DEBUG", "Фильтр: " + currentFilter + ", Товар: " + product.getName() + ", Категория: " + category);

        if (currentFilter == FilterType.WOMEN) {
            return category.contains("Женская");
        } else {
            return category.contains("Мужская");
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

    public void refreshCatalog() {
        loadProductsFromServer();
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
package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    // Навигация
    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    private enum FilterType { ALL, WOMEN, MEN }
    private FilterType currentFilter = FilterType.ALL;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Инициализация View
        initViews();

        // Настройка RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка товаров
        loadProducts();

        // Настройка поиска
        setupSearch();

        // Настройка фильтров
        setupFilters();

        // Корзина
        cartBar.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));

        // Нижняя навигация
        setupBottomNavigation();
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

        // Навигация
        navHome = findViewById(R.id.navHome);
        navCatalog = findViewById(R.id.navCatalog);
        navProjects = findViewById(R.id.navProjects);
        navProfile = findViewById(R.id.navProfile);
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
        // Главная (текущий экран)
        navHome.setOnClickListener(v -> {
            // Уже на главной
        });

        // Каталог
        navCatalog.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CatalogActivity.class);
            startActivity(intent);
            finish();
        });

        // Проекты
        navProjects.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(HomeActivity.this, ProjectsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Профиль
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity7.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        productList.add(new Product("Рубашка Воскресенье", "300 ₽", 0,
                "Классическая рубашка из 100% хлопка. Отличный выбор для повседневной носки."));
        productList.add(new Product("Шорты Вторник", "4000 ₽", 0,
                "Удобные летние шорты из натуральной ткани."));
        productList.add(new Product("Футболка Пятница", "800 ₽", 0,
                "Хлопковая футболка с принтом. Мягкая и дышащая."));
        productList.add(new Product("Джинсы Суббота", "2500 ₽", 0,
                "Стильные джинсы прямого кроя."));
        productList.add(new Product("Куртка Зима", "5000 ₽", 0,
                "Теплая зимняя куртка."));

        filteredList = new ArrayList<>(productList);
        productAdapter = new ProductAdapter(filteredList, this);
        rvProducts.setAdapter(productAdapter);

        updateFilterButtons();
        updateCartBar();
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
            tvEmptyProducts.setVisibility(TextView.VISIBLE);
            rvProducts.setVisibility(RecyclerView.GONE);
        } else {
            tvEmptyProducts.setVisibility(TextView.GONE);
            rvProducts.setVisibility(RecyclerView.VISIBLE);
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
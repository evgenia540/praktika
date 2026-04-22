package com.example.praktika;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем данные о товаре из Intent
        String productName = getIntent().getStringExtra("product_name");
        String productPrice = getIntent().getStringExtra("product_price");
        String productDescription = getIntent().getStringExtra("product_description");

        // Создаем layout программно
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 24, 24, 24);

        // Кнопка назад
        TextView backBtn = new TextView(this);
        backBtn.setText("← Назад");
        backBtn.setTextSize(18);
        backBtn.setTextColor(0xFF2D7AFF);
        backBtn.setPadding(0, 0, 0, 30);
        backBtn.setOnClickListener(v -> finish());
        mainLayout.addView(backBtn);

        // Название товара
        TextView nameText = new TextView(this);
        nameText.setText(productName);
        nameText.setTextSize(28);
        nameText.setTypeface(null, Typeface.BOLD);
        nameText.setPadding(0, 0, 0, 16);
        mainLayout.addView(nameText);

        // Цена
        TextView priceText = new TextView(this);
        priceText.setText(productPrice);
        priceText.setTextSize(24);
        priceText.setTextColor(0xFF2D7AFF);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setPadding(0, 0, 0, 24);
        mainLayout.addView(priceText);

        // Разделитель
        View divider = new View(this);
        divider.setBackgroundColor(0xFFE0E0E0);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        dividerParams.setMargins(0, 0, 0, 24);
        divider.setLayoutParams(dividerParams);
        mainLayout.addView(divider);

        // Описание
        TextView descLabel = new TextView(this);
        descLabel.setText("Описание");
        descLabel.setTextSize(18);
        descLabel.setTypeface(null, Typeface.BOLD);
        descLabel.setPadding(0, 0, 0, 8);
        mainLayout.addView(descLabel);

        TextView descText = new TextView(this);
        descText.setText(productDescription != null ? productDescription : "Описание товара отсутствует");
        descText.setTextSize(16);
        descText.setTextColor(0xFF666666);
        descText.setPadding(0, 0, 0, 32);
        mainLayout.addView(descText);

        // Кнопка "В корзину"
        Button addToCartBtn = new Button(this);
        addToCartBtn.setText("Добавить в корзину");
        addToCartBtn.setTextSize(16);
        addToCartBtn.setTextColor(0xFFFFFFFF);
        addToCartBtn.setBackgroundColor(0xFF2D7AFF);
        addToCartBtn.setPadding(0, 16, 0, 16);
        addToCartBtn.setOnClickListener(v -> {
            Toast.makeText(this, productName + " добавлен в корзину!", Toast.LENGTH_SHORT).show();
            finish();
        });
        mainLayout.addView(addToCartBtn);

        setContentView(mainLayout);
    }
}
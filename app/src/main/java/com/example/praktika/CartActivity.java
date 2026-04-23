package com.example.praktika;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView tvEmptyCart;
    private TextView tvTotalAmount;
    private LinearLayout rowTotal;
    private Button btnCheckout;
    private ImageView ivTrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());

        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rowTotal = findViewById(R.id.rowTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        ivTrash = findViewById(R.id.ivTrash);

        ivTrash.setOnClickListener(v -> {
            CartManager.clear(this);
            renderCart();
        });

        btnCheckout.setOnClickListener(v -> createOrder());

        renderCart();
    }

    private void createOrder() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        List<CartManager.CartEntry> entries = CartManager.getEntries(this);

        if (userId.isEmpty()) {
            Toast.makeText(this, "Войдите в аккаунт для оформления заказа", Toast.LENGTH_SHORT).show();
            return;
        }

        if (entries.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = CartManager.getTotalPrice(this);

        btnCheckout.setText("Оформляем...");
        btnCheckout.setEnabled(false);

        ApiClient.createOrder(userId, total, new ApiClient.OrderCallback() {
            @Override
            public void onSuccess(String orderId) {
                runOnUiThread(() -> {
                    CartManager.clear(CartActivity.this);
                    renderCart();
                    btnCheckout.setText("Заказать");
                    btnCheckout.setEnabled(true);
                    Toast.makeText(CartActivity.this, "✅ Заказ оформлен!", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    // Если сервер не отвечает - работаем локально
                    CartManager.clear(CartActivity.this);
                    renderCart();
                    btnCheckout.setText("Заказать");
                    btnCheckout.setEnabled(true);
                    Toast.makeText(CartActivity.this, "📱 Заказ сохранен локально", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderCart();
    }

    private void renderCart() {
        List<CartManager.CartEntry> entries = CartManager.getEntries(this);
        cartItemsContainer.removeAllViews();

        if (entries.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            rowTotal.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.GONE);
            ivTrash.setVisibility(View.GONE);
            return;
        }

        tvEmptyCart.setVisibility(View.GONE);
        rowTotal.setVisibility(View.VISIBLE);
        btnCheckout.setVisibility(View.VISIBLE);
        ivTrash.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartManager.CartEntry entry : entries) {
            View itemView = inflater.inflate(R.layout.item_cart_product, cartItemsContainer, false);

            TextView tvTitle = itemView.findViewById(R.id.tvCartItemTitle);
            TextView tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            TextView tvQty = itemView.findViewById(R.id.tvCartItemQty);
            View btnMinus = itemView.findViewById(R.id.btnQtyMinus);
            View btnPlus = itemView.findViewById(R.id.btnQtyPlus);
            View btnRemove = itemView.findViewById(R.id.ivCartItemRemove);

            tvTitle.setText(entry.product.getName());
            tvPrice.setText(entry.product.getPrice());
            tvQty.setText(String.format(Locale.getDefault(), "%d шт", entry.quantity));

            btnPlus.setOnClickListener(v -> {
                CartManager.increment(this, entry.product.getName());
                renderCart();
            });

            btnMinus.setOnClickListener(v -> {
                CartManager.decrement(this, entry.product.getName());
                renderCart();
            });

            btnRemove.setOnClickListener(v -> {
                CartManager.remove(this, entry.product.getName());
                renderCart();
            });

            cartItemsContainer.addView(itemView);
        }

        tvTotalAmount.setText(CartManager.getTotalPriceText(this));
    }
}
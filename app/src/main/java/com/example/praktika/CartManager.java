package com.example.praktika;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartManager {
    private static final String PREF_NAME = "cart_state";
    private static final String KEY_ITEMS = "items_json";

    private static final Map<String, CartEntry> ITEMS = new LinkedHashMap<>();
    private static boolean loaded = false;

    public static synchronized void add(Context context, Product product) {
        ensureLoaded(context);
        CartEntry entry = ITEMS.get(product.getName());
        if (entry == null) {
            ITEMS.put(product.getName(), new CartEntry(product, 1));
        } else {
            entry.quantity++;
        }
        save(context);
    }

    public static synchronized void toggle(Context context, Product product) {
        ensureLoaded(context);
        if (ITEMS.containsKey(product.getName())) {
            ITEMS.remove(product.getName());
        } else {
            ITEMS.put(product.getName(), new CartEntry(product, 1));
        }
        save(context);
    }

    public static synchronized void increment(Context context, String productName) {
        ensureLoaded(context);
        CartEntry entry = ITEMS.get(productName);
        if (entry != null) {
            entry.quantity++;
            save(context);
        }
    }

    public static synchronized void decrement(Context context, String productName) {
        ensureLoaded(context);
        CartEntry entry = ITEMS.get(productName);
        if (entry != null) {
            entry.quantity--;
            if (entry.quantity <= 0) {
                ITEMS.remove(productName);
            }
            save(context);
        }
    }

    public static synchronized void remove(Context context, String productName) {
        ensureLoaded(context);
        ITEMS.remove(productName);
        save(context);
    }

    public static synchronized void clear(Context context) {
        ensureLoaded(context);
        ITEMS.clear();
        save(context);
    }

    public static synchronized boolean isInCart(Context context, String productName) {
        ensureLoaded(context);
        return ITEMS.containsKey(productName);
    }

    public static synchronized int getTotalQuantity(Context context) {
        ensureLoaded(context);
        int total = 0;
        for (CartEntry entry : ITEMS.values()) {
            total += entry.quantity;
        }
        return total;
    }

    public static synchronized double getTotalPrice(Context context) {
        ensureLoaded(context);
        double total = 0;
        for (CartEntry entry : ITEMS.values()) {
            String priceStr = entry.product.getPrice().replace(" ₽", "").replace(" ", "");
            try {
                total += Double.parseDouble(priceStr) * entry.quantity;
            } catch (NumberFormatException ignored) {
            }
        }
        return total;
    }

    public static synchronized String getTotalPriceText(Context context) {
        return String.format(Locale.getDefault(), "%.0f ₽", getTotalPrice(context));
    }

    public static synchronized List<CartEntry> getEntries(Context context) {
        ensureLoaded(context);
        return new ArrayList<>(ITEMS.values());
    }

    private static void ensureLoaded(Context context) {
        if (loaded) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ITEMS, "{}");

        ITEMS.clear();

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray names = obj.names();
            if (names != null) {
                for (int i = 0; i < names.length(); i++) {
                    String name = names.getString(i);
                    JSONObject entryObj = obj.getJSONObject(name);

                    String productName = entryObj.getString("productName");
                    String productPrice = entryObj.getString("productPrice");
                    int productImageRes = entryObj.optInt("productImageRes", 0);
                    String productDescription = entryObj.getString("productDescription");
                    int quantity = entryObj.getInt("quantity");

                    Product product = new Product(productName, productPrice, productImageRes, productDescription);
                    ITEMS.put(name, new CartEntry(product, quantity));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loaded = true;
    }

    private static void save(Context context) {
        try {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, CartEntry> entry : ITEMS.entrySet()) {
                JSONObject entryObj = new JSONObject();
                entryObj.put("productName", entry.getValue().product.getName());
                entryObj.put("productPrice", entry.getValue().product.getPrice());
                entryObj.put("productImageRes", entry.getValue().product.getImageRes());
                entryObj.put("productDescription", entry.getValue().product.getDescription());
                entryObj.put("quantity", entry.getValue().quantity);
                obj.put(entry.getKey(), entryObj);
            }

            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_ITEMS, obj.toString())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CartEntry {
        public Product product;
        public int quantity;

        public CartEntry(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}
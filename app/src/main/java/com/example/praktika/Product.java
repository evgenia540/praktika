package com.example.praktika;

public class Product {
    private String name;
    private String price;
    private int imageRes;
    private String description;
    private String category;  // Хранит typeCloses с сервера (Женская одежда / Мужская одежда)
    private String imageUrl;

    public Product(String name, String price, int imageRes, String description) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.description = description;
        this.category = "";
        this.imageUrl = "";
    }

    public Product(String name, String price, int imageRes, String description, String category, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
}
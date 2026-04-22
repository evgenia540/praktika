package com.example.praktika;

public class Product {
    private String name;
    private String price;
    private int imageRes;
    private String description;

    public Product(String name, String price, int imageRes, String description) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.description = description;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public String getDescription() { return description; }
}
package com.example.icecream_android;

public class FlavorIceCream {
    private String name;
    private double price;
    private int quantity;
    private String uuid;

    public FlavorIceCream(String name, double price, int quantity, String uuid) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUuid() {
        return uuid;
    }
}


package com.example.icecream_android;

public class FlavorIceCream {
    private String name;
    private double quantity;
    private int price;
    private String uuid;

    public FlavorIceCream(String name, int price, double quantity, String uuid) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getUuid() {
        return uuid;
    }
}
package com.example.icecream_android;

public class FlavorIceCream {
    private String name;
    private int price;
    private String imageName;

    public FlavorIceCream(String name, int price, String imageName) {
        this.name = name;
        this.price = price;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageName() {
        return imageName;
    }
}

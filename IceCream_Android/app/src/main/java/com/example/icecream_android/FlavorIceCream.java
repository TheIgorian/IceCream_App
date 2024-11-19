package com.example.icecream_android;

public class FlavorIceCream {
    private final String name;
    private final int price;
    private final int imageNumber;

    public FlavorIceCream(String name, int price, int imageNumber) {
        this.name = name;
        this.price = price;
        this.imageNumber = imageNumber;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageNumber() {
        return imageNumber;
    }
}

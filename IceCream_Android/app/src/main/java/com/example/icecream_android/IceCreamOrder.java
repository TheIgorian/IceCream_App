package com.example.icecream_android;

import java.util.List;

public class IceCreamOrder {
    private String horn;
    private List<String> flavors;
    private List<String> toppings;
    private List<Integer> flavors_prices;
    private int count = 1;
    private double totalPrice;

    public IceCreamOrder(String horn, List<String> flavors, List<String> toppings, List<Integer> flavors_prices) {
        this.horn = horn;
        this.flavors = flavors;
        this.toppings = toppings;
        this.flavors_prices = flavors_prices;
        this.totalPrice = calculateTotalPrice();
    }

    private double calculateTotalPrice() {
        int sum = 0;
        for (Integer flavorPrice : flavors_prices) {
            sum += flavorPrice;
        }
        return sum * count;
    }

    public String getHorn() {
        return horn;
    }

    public List<String> getFlavors() {
        return flavors;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public List<Integer> getFlavors_prices() {
        return flavors_prices;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

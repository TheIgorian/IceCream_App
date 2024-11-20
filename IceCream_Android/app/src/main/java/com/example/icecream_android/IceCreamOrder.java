package com.example.icecream_android;

import java.util.List;

public class IceCreamOrder {
    private String horn;
    private List<String> flavors;
    private List<String> toppings;
    private List<Integer> flavors_prices;
    private List<Integer> toppings_prices;
    private int count_flavors = 1;
    private int count_toppings = 1;

    public IceCreamOrder(String horn, List<String> flavors, List<String> toppings, List<Integer> flavors_prices, List<Integer> toppings_prices) {
        this.horn = horn;
        this.flavors = flavors;
        this.toppings = toppings;
        this.flavors_prices = flavors_prices;
        this.toppings_prices = toppings_prices;
    }

    private double calculateTotalPrice() {
        int sumF = 0, sumT = 0;
        for (Integer flavorPrice : flavors_prices) {
            sumF += flavorPrice;
        }
        sumF *= count_flavors;
        for (Integer toppingPrices : toppings_prices){
            sumT += toppingPrices;
        }
        sumT *= count_toppings;
        return sumF +sumT;
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

    public List<Integer> getToppings_prices() {
        return toppings_prices;
    }

    public double getTotalPrice() {
        return calculateTotalPrice();
    }

    public int getCount_flavors() {
        return count_flavors;
    }

    public void setCount_flavors(int count_flavors) {
        this.count_flavors = count_flavors;
    }

    public int getCount_toppings() {
        return count_toppings;
    }

    public void setCount_toppings(int count_toppings) {
        this.count_toppings = count_toppings;
    }
}

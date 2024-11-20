package com.example.icecream_android;

public class TypeHorn {
    private String name;
    private int quantity;
    private String uuid;

    public TypeHorn(String name, int quantity, String uuid) {
        this.name = name;
        this.quantity = quantity;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() { return quantity; }

    public String getUuid() {
        return uuid;
    }
}


package com.example.icecream_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        RecyclerView recyclerViewIceCream = findViewById(R.id.recyclerViewIceCream);
        recyclerViewIceCream.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView recyclerViewTopping = findViewById(R.id.recyclerViewTopping);
        recyclerViewTopping.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView recyclerViewHorn = findViewById(R.id.recyclerViewHorn);
        recyclerViewHorn.setLayoutManager(new GridLayoutManager(this, 2));

        // JSON-данные
        String jsonDataIceCream = "{'result': [['Полуниця', 500.0, 500, 'strawberry'], ['Малина', 500.0, 500, 'raspberry'], ['Лаванда', 500.0, 500, 'lavender'], ['Шоколад', 500.0, 500, 'chocolate'], ['Карамель', 500.0, 500, 'caramel'], ['Ваніль', 450.0, 500, 'vanilla'], ['Кокос', 500.0, 500, 'coconut'], ['Лимон', 500.0, 500, 'lemon'], [\"М'ятя\", 500.0, 500, 'mint']]}";
        String jsonDataTopping = "{'result': [['Мед', 200.0, 10, 'top_honey'], ['Карамель', 200.0, 15, 'top_caramel_syrup'], ['Вафлі', 200.0, 10, 'top_wafer_crumbs'], ['Зефір', 250.0, 10, 'top_marshmallow'], ['Горішки', 200.0, 10, 'top_nuts'], ['Шоко сироп', 199.79, 15, 'top_chocolate_syrup']]}";
        String jsonDataHorn = "{'result': [['Звичайний', 210, 'common'], ['Солоний', 210, 'solt'], ['Бумажний', 410, 'paper'], ['Солодкий', 240, 'sugar']]}";

        List<FlavorIceCream> iceCreamList = parseJsonDataIceCream(jsonDataIceCream);
        FlavorIceCreamAdapter adapterIceCream = new FlavorIceCreamAdapter(this, iceCreamList);
        recyclerViewIceCream.setAdapter(adapterIceCream);

        List<FlavorTopping> toppingList = parseJsonDataTopping(jsonDataTopping);
        FlavorToppingAdapter adapterTopping = new FlavorToppingAdapter(this, toppingList);
        recyclerViewTopping.setAdapter(adapterTopping);

        List<TypeHorn> hornList = parseJsonDataHorn(jsonDataHorn);
        TypeHornAdapter adapterHorn = new TypeHornAdapter(this, hornList);
        recyclerViewHorn.setAdapter(adapterHorn);
    }

    private List<FlavorIceCream> parseJsonDataIceCream(String jsonDataIceCream) {
        List<FlavorIceCream> iceCreamList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonDataIceCream);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONArray itemArray = resultArray.getJSONArray(i);
                String name = itemArray.getString(0);
                int price = itemArray.getInt(2);
                double quantity = itemArray.getDouble(1);
                String uuid = itemArray.getString(3);

                iceCreamList.add(new FlavorIceCream(name, price, quantity, uuid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iceCreamList;
    }

    private List<FlavorTopping> parseJsonDataTopping(String jsonDataTopping) {
        List<FlavorTopping> toppingList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonDataTopping);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONArray itemArray = resultArray.getJSONArray(i);
                String name = itemArray.getString(0);
                int price = itemArray.getInt(2);
                double quantity = itemArray.getDouble(1);
                String uuid = itemArray.getString(3);

                toppingList.add(new FlavorTopping(name, price, quantity, uuid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toppingList;
    }

    private List<TypeHorn> parseJsonDataHorn(String jsonDataHorn) {
        List<TypeHorn> hornList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonDataHorn);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONArray itemArray = resultArray.getJSONArray(i);
                String name = itemArray.getString(0);
                int quantity = itemArray.getInt(1);
                String uuid = itemArray.getString(2);

                hornList.add(new TypeHorn(name, quantity, uuid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hornList;
    }
}
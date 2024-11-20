package com.example.icecream_android;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private double totalSum = 0.0;
    List<IceCreamOrder> orderList;
    IceCreamOrderAdapter orderAdapter;

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

        RecyclerView recyclerViewCheck = findViewById(R.id.check);
        recyclerViewCheck.setLayoutManager(new LinearLayoutManager(this));

        // JSON-данные
        String jsonDataIceCream = "{'result': [['Полуниця', 500.0, 500, 'strawberry'], ['Малина', 500.0, 500, 'raspberry'], ['Лаванда', 500.0, 500, 'lavender'], ['Шоколад', 500.0, 500, 'chocolate'], ['Карамель', 500.0, 500, 'caramel'], ['Ваніль', 450.0, 500, 'vanilla'], ['Кокос', 500.0, 500, 'coconut'], ['Лимон', 500.0, 500, 'lemon'], [\"М'ятя\", 500.0, 500, 'mint']]}";
        String jsonDataTopping = "{'result': [['Мед', 200.0, 10, 'top_honey'], ['Карамель', 200.0, 15, 'top_caramel_syrup'], ['Вафлі', 200.0, 10, 'top_wafer_crumbs'], ['Зефір', 250.0, 10, 'top_marshmallow'], ['Горішки', 200.0, 10, 'top_nuts'], ['Шоко сироп', 199.79, 15, 'top_chocolate_syrup']]}";
        String jsonDataHorn = "{'result': [['Звичайний', 210, 'common'], ['Солоний', 210, 'solt'], ['Бумажний', 410, 'paper'], ['Солодкий', 240, 'sugar']]}";

        List<FlavorIceCream> iceCreamList = parseJsonDataIceCream(jsonDataIceCream);
        List<FlavorTopping> toppingList = parseJsonDataTopping(jsonDataTopping);
        List<TypeHorn> hornList = parseJsonDataHorn(jsonDataHorn);

        FlavorIceCreamAdapter adapterIceCream = new FlavorIceCreamAdapter(this, iceCreamList);
        FlavorToppingAdapter adapterTopping = new FlavorToppingAdapter(this, toppingList);
        TypeHornAdapter adapterHorn = new TypeHornAdapter(this, hornList);

        recyclerViewIceCream.setAdapter(adapterIceCream);
        recyclerViewTopping.setAdapter(adapterTopping);
        recyclerViewHorn.setAdapter(adapterHorn);


        // Инициализация глобальных переменных
        orderList = new ArrayList<>();
        orderAdapter = new IceCreamOrderAdapter(orderList, this);
        recyclerViewCheck.setAdapter(orderAdapter);

        Button addToCheck = findViewById(R.id.addIceCreamCheck);
        addToCheck.setOnClickListener(v -> {
            List<TypeHorn> allSelectedHorn = adapterHorn.getSelectedItems();
            List<FlavorIceCream> allSelectedIceCream = adapterIceCream.getSelectedItems();
            List<FlavorTopping> allSelectedTopping = adapterTopping.getSelectedItems();

            if (!allSelectedHorn.isEmpty()) {
                String hornText = allSelectedHorn.get(0).getName();
                List<String> flavors = new ArrayList<>();
                List<Integer> flavors_prices = new ArrayList<>();
                for (FlavorIceCream flavor : allSelectedIceCream) {
                    flavors.add(flavor.getName());
                    flavors_prices.add(flavor.getPrice());
                }
                List<String> toppings = new ArrayList<>();
                List<Integer> toppings_prices = new ArrayList<>();
                for (FlavorTopping topping : allSelectedTopping) {
                    toppings.add(topping.getName());
                    toppings_prices.add(topping.getPrice());
                }

                // Создаём новый заказ
                IceCreamOrder newOrder = new IceCreamOrder(hornText, flavors, toppings, flavors_prices, toppings_prices);

                orderList.add(newOrder);
                orderAdapter.notifyDataSetChanged();

                adapterIceCream.clearSelection();
                adapterTopping.clearSelection();
                adapterHorn.clearSelection();

                updateTotalSum();
            }
        });
    }

    public void updateTotalSum() {
        totalSum = 0.0;
        for (IceCreamOrder order : orderAdapter.getOrderList()) {
            totalSum += order.getTotalPrice();
        }
        Log.d("DEBUG", "Total sum updated: " + totalSum);
        TextView sumCheckTextView = findViewById(R.id.sumCheck);
        sumCheckTextView.setText("Сума чека: " + totalSum + "₴");
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
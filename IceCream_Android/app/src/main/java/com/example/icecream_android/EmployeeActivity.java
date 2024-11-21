package com.example.icecream_android;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        String jsonDataHorn = "{'result': [['Звичайний', 210, 'common'], ['Солоний', 210, 'solt'], ['Паперовий', 410, 'paper'], ['Солодкий', 240, 'sugar']]}";

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

            // Перевірка: якщо вибрано лише ріжок, заборонити додавання
            if (!allSelectedHorn.isEmpty() && allSelectedIceCream.isEmpty()) {
                Toast.makeText(this, "Додайте хоча б один вид морозива", Toast.LENGTH_SHORT).show();
                return; // Припинити виконання, якщо умова виконується
            }

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

                // Створюємо нове замовлення
                IceCreamOrder newOrder = new IceCreamOrder(hornText, flavors, toppings, flavors_prices, toppings_prices);

                orderList.add(newOrder);
                orderAdapter.notifyDataSetChanged();

                adapterIceCream.clearSelection();
                adapterTopping.clearSelection();
                adapterHorn.clearSelection();

                updateTotalSum();
            }
        });

        Button payButton = findViewById(R.id.PayButton);
        payButton.setOnClickListener(v -> {
            double totalSum = this.totalSum;

            if (orderList.isEmpty()) {
                Toast.makeText(EmployeeActivity.this, "Чек порожній. Додайте хоча б одне замовлення.", Toast.LENGTH_SHORT).show();
                return; // Вихід, якщо немає замовлень
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Оплата");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 20, 50, 20);

            TextView totalTextView = new TextView(this);
            totalTextView.setText("Сума чека: " + totalSum + "₴");
            layout.addView(totalTextView);

            EditText inputAmount = new EditText(this);
            inputAmount.setHint("Введіть суму");
            inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            layout.addView(inputAmount);

            TextView changeTextView = new TextView(this);
            changeTextView.setText("Решта: 0₴");
            layout.addView(changeTextView);

            // Ініціалізація кнопок
            builder.setView(layout);

            builder.setPositiveButton("Card", (dialog, which) -> {
                showPrintCheckDialog("Оплата карткою завершена");
            });

            AlertDialog dialog = builder.create(); // Створюємо діалог перед встановленням кнопок
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cash", (d, which) -> {
                showPrintCheckDialog("Оплата готівкою завершена");
            });

            dialog.setOnShowListener(d -> {
                Button cashButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cashButton.setEnabled(false); // Вимкнена кнопка спочатку

                // Слухач змін тексту
                inputAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            double enteredAmount = Double.parseDouble(s.toString());
                            if (enteredAmount >= totalSum) {
                                cashButton.setEnabled(true); // Активуємо кнопку Cash
                                double change = enteredAmount - totalSum;
                                changeTextView.setText("Решта: " + change + "₴");
                            } else {
                                cashButton.setEnabled(false); // Вимикаємо кнопку Cash
                                changeTextView.setText("Недостатньо коштів");
                            }
                        } catch (NumberFormatException e) {
                            cashButton.setEnabled(false); // Вимикаємо кнопку Cash при неправильному вводі
                            changeTextView.setText("Решта: 0₴");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            });

            dialog.show();
        });
        //Пошук Зіжок, Морозиво, Топінг
        EditText searchHornEditText = findViewById(R.id.SearchHornEditText);
        EditText searchIceCreamEditText = findViewById(R.id.SearchIceCreamEditText);
        EditText searchToppingEditText = findViewById(R.id.SearchToppingEditText);


        searchHornEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { adapterHorn.filterHorn(s.toString()); }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        searchIceCreamEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { adapterIceCream.filterIceCream(s.toString()); }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        searchToppingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { adapterTopping.filterTopping(s.toString()); }
            @Override
            public void afterTextChanged(Editable s) { }
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

    private void clearOrder() {
        orderList.clear();
        orderAdapter.notifyDataSetChanged();
        totalSum = 0.0;
        TextView sumCheckTextView = findViewById(R.id.sumCheck);
        sumCheckTextView.setText("Сума чека: " + totalSum + "₴");
    }

    private void showPrintCheckDialog(String paymentMessage) {
        AlertDialog.Builder printBuilder = new AlertDialog.Builder(this);
        printBuilder.setTitle(paymentMessage);
        printBuilder.setMessage("Чи потрібно друкувати чек?");

        printBuilder.setPositiveButton("Так", (dialog, which) -> {
            // Імітація друку чеку
            Toast.makeText(this, "Чек друкується...", Toast.LENGTH_SHORT).show();
            clearOrder(); // Очищення замовлення
        });

        printBuilder.setNegativeButton("Ні", (dialog, which) -> {
            Toast.makeText(this, "Оплата завершена без друку чеку", Toast.LENGTH_SHORT).show();
            clearOrder(); // Очищення замовлення
        });

        printBuilder.show();
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
package com.example.icecream_android;

import android.adservices.topics.Topic;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EmployeeActivity extends AppCompatActivity {

    private double totalSum = 0.0;
    List<IceCreamOrder> orderList;
    IceCreamOrderAdapter orderAdapter;
    private List<FlavorIceCream> flavorIceCreams;
    private List<TypeHorn> typeHorns;
    private List<FlavorTopping> flavorToppings;
    SharedPreferences sharedPreferences;
    private FlavorIceCreamAdapter adapterIceCream;
    private FlavorToppingAdapter adapterTopping;
    private TypeHornAdapter adapterHorn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String idPoint = sharedPreferences.getString("point", null);

        RecyclerView recyclerViewIceCream = findViewById(R.id.recyclerViewIceCream);
        recyclerViewIceCream.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView recyclerViewTopping = findViewById(R.id.recyclerViewTopping);
        recyclerViewTopping.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView recyclerViewHorn = findViewById(R.id.recyclerViewHorn);
        recyclerViewHorn.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView recyclerViewCheck = findViewById(R.id.check);
        recyclerViewCheck.setLayoutManager(new LinearLayoutManager(this));

        flavorIceCreams = new ArrayList<>();
        flavorToppings = new ArrayList<>();
        typeHorns = new ArrayList<>();

        // URL для запиту
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("function_name", "get_all_object_for_company");
            requestJson.put("param_dict", new JSONObject()
                    .put("point", idPoint)
            );
        } catch (JSONException e) {
            Toast.makeText(EmployeeActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Ініціалізація HttpClientHelper для запиту
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        // Парсинг JSON-відповіді
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        JSONArray flavorsArray = jsonObject.getJSONArray("flavor");
                        JSONArray hornsArray = jsonObject.getJSONArray("cone");
                        JSONArray toppingsArray = jsonObject.getJSONArray("additive");

                        // Очистка списков перед заполнением
                        flavorIceCreams.clear();
                        typeHorns.clear();
                        flavorToppings.clear();

                        // Заполнение списков
                        for (int i = 0; i < flavorsArray.length(); i++) {
                            JSONArray flavorArray = flavorsArray.getJSONArray(i);
                            String nameFlavor = flavorArray.getString(0);
                            double quantityFlavor = flavorArray.getDouble(1);
                            int priceFlavor = flavorArray.getInt(2);
                            String imageFlavor = flavorArray.getString(3);
                            flavorIceCreams.add(new FlavorIceCream(nameFlavor, priceFlavor, quantityFlavor, imageFlavor));
                        }
                        for (int i = 0; i < hornsArray.length(); i++) {
                            JSONArray hornArray = hornsArray.getJSONArray(i);
                            String nameHorn = hornArray.getString(0);
                            int quantityHorn = hornArray.getInt(1);
                            String imageHorn = hornArray.getString(2);
                            typeHorns.add(new TypeHorn(nameHorn, quantityHorn, imageHorn));
                        }
                        for (int i = 0; i < toppingsArray.length(); i++) {
                            JSONArray toppingArray = toppingsArray.getJSONArray(i);
                            String nameTopping = toppingArray.getString(0);
                            double quantityTopping = toppingArray.getDouble(1);
                            int priceTopping = toppingArray.getInt(2);
                            String imageTopping = toppingArray.getString(3);
                            flavorToppings.add(new FlavorTopping(nameTopping, priceTopping, quantityTopping, imageTopping));
                        }

                        // Создание адаптеров после заполнения списков
                        adapterIceCream = new FlavorIceCreamAdapter(EmployeeActivity.this, flavorIceCreams);
                        adapterTopping = new FlavorToppingAdapter(EmployeeActivity.this, flavorToppings);
                        adapterHorn = new TypeHornAdapter(EmployeeActivity.this, typeHorns);

                        recyclerViewIceCream.setAdapter(adapterIceCream);
                        recyclerViewTopping.setAdapter(adapterTopping);
                        recyclerViewHorn.setAdapter(adapterHorn);
                        Log.d("JSON Response", response);

                    } catch (Exception e) {
                        Toast.makeText(EmployeeActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    System.out.println("Error: " + error);
                });
            }
        });

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

        Button buttonSales = findViewById(R.id.SalesButton);
        buttonSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Поточна дата за замовчуванням
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());

                // Отримання даних з SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String idPoint = sharedPreferences.getString("point", null);

                // Ініціалізація JSON-запиту
                JSONObject requestJson = new JSONObject();
                try {
                    // Додаємо назву функції
                    requestJson.put("function_name", "get_order");

                    // Створюємо об'єкт param_dict і додаємо параметри: point і date
                    JSONObject paramDict = new JSONObject();
                    paramDict.put("point", idPoint);
                    paramDict.put("date", currentDate);  // Додаємо поточну дату

                    // Додаємо param_dict до основного запиту
                    requestJson.put("param_dict", paramDict);
                } catch (JSONException e) {
                    // Обробка помилки JSON
                    Toast.makeText(EmployeeActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Створення діалогового вікна
                // Створення діалогового вікна
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeActivity.this);
                builder.setTitle("Дані продажів за " + currentDate); // Початковий заголовок

                // Інфлюємо XML макет
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_sales, null);
                builder.setView(dialogView);

                final TextView salesText = dialogView.findViewById(R.id.salesText);
                final Button buttonDate = dialogView.findViewById(R.id.buttonDate);

                // Збереження об'єкта діалогу для подальших змін
                final AlertDialog dialog = builder.create();

                // Відображення діалогу
                dialog.show();

                HttpClientHelper httpClientHelper = new HttpClientHelper();
                httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            try {
                                // Парсинг JSON-відповіді
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray salesArray = jsonResponse.getJSONArray("result");

                                // Ініціалізація StringBuilder для збереження повідомлення
                                StringBuilder message = new StringBuilder();
                                for (int i = 0; i < salesArray.length(); i++) {
                                    JSONArray sale = salesArray.getJSONArray(i);
                                    message.append("Ріжок: ").append(sale.getString(0)).append("\n")
                                            .append("Смак: ").append(sale.getString(1)).append("\n")
                                            .append("Добавки: ").append(sale.getString(2)).append("\n")
                                            .append("Кількість: ").append(sale.getInt(3)).append("\n")
                                            .append("Ціна: ").append(sale.isNull(4) ? "N/A" : sale.getDouble(4) + "₴").append("\n\n");
                                }

                                // Оновлюємо текст у TextView
                                salesText.setText(message.toString());
                            } catch (Exception e) {
                                Toast.makeText(EmployeeActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(EmployeeActivity.this, "Помилка запиту: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });

                // Обробник для кнопки "Вибрати дату"
                buttonDate.setOnClickListener(view -> {
                    // Вибір дати
                    DatePickerDialog datePickerDialog = new DatePickerDialog(EmployeeActivity.this,
                            (view1, year, month, dayOfMonth) -> {
                                // Оновлення обраної дати
                                calendar.set(year, month, dayOfMonth);
                                String selectedDate = dateFormat.format(calendar.getTime());

                                // Оновлення заголовка діалогу
                                dialog.setTitle("Дані продажів за " + selectedDate); // Оновлено заголовок

                                // Оновлення JSON-запиту з новою датою
                                try {
                                    JSONObject newRequestJson = new JSONObject();
                                    newRequestJson.put("function_name", "get_order");

                                    JSONObject newParamDict = new JSONObject();
                                    newParamDict.put("point", idPoint);
                                    newParamDict.put("date", selectedDate); // Використовуємо нову дату

                                    newRequestJson.put("param_dict", newParamDict);

                                    // Надсилаємо новий запит
                                    httpClientHelper.sendPostRequest(getString(R.string.api_url), newRequestJson, new HttpClientHelper.ResponseCallback() {
                                        @Override
                                        public void onSuccess(String response) {
                                            runOnUiThread(() -> {
                                                try {
                                                    // Парсинг нової JSON-відповіді
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    JSONArray salesArray = jsonResponse.getJSONArray("result");

                                                    // Якщо масив порожній, вивести повідомлення про відсутність даних
                                                    if (salesArray.length() == 0) {
                                                        salesText.setText("Немає даних для вибраної дати.");
                                                    } else {
                                                        // Оновлення даних в діалозі
                                                        StringBuilder updatedMessage = new StringBuilder();
                                                        for (int i = 0; i < salesArray.length(); i++) {
                                                            JSONArray sale = salesArray.getJSONArray(i);
                                                            updatedMessage.append("Ріжок: ").append(sale.getString(0)).append("\n")
                                                                    .append("Смак: ").append(sale.getString(1)).append("\n")
                                                                    .append("Добавки: ").append(sale.getString(2)).append("\n")
                                                                    .append("Кількість: ").append(sale.getInt(3)).append("\n")
                                                                    .append("Ціна: ").append(sale.isNull(4) ? "N/A" : sale.getDouble(4) + "₴").append("\n\n");
                                                        }

                                                        // Оновлюємо текст повідомлення в діалозі
                                                        salesText.setText(updatedMessage.toString());
                                                    }
                                                } catch (Exception e) {
                                                    Toast.makeText(EmployeeActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(EmployeeActivity.this, "Помилка запиту: " + error, Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    });
                                } catch (JSONException e) {
                                    Toast.makeText(EmployeeActivity.this, "Помилка оновлення JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                });

                dialog.show();
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

}
package com.example.icecream_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerViewFlavor, recyclerViewHorn, recyclerViewTopping;
    private List<String[]> employeeList;

    private LinearLayout iceCreamLayout;
    private LinearLayout analysticLayout;

    SharedPreferences sharedPreferences;

    private List<FlavorIceCream> flavorIceCreams;
    private List<TypeHorn> typeHorns;
    private List<FlavorTopping> flavorToppings;
    private LinearLayout usersLayout;
    private LinearLayout currentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewHorn = findViewById(R.id.recyclerHorns);
        recyclerViewFlavor = findViewById(R.id.recyclerFlavors);
        recyclerViewTopping = findViewById(R.id.recyclerToppings);

        recyclerViewHorn.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFlavor.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopping.setLayoutManager(new LinearLayoutManager(this));

        iceCreamLayout = findViewById(R.id.iceCreamLayout);
        analysticLayout = findViewById(R.id.analysticLayout);
        usersLayout = findViewById(R.id.UsersLayout);

        // Инициализация TextView
        TextView iceCreamTextView = findViewById(R.id.IceCreamTextView);
        TextView analysticTextView = findViewById(R.id.AnalysticTextView);
        TextView usersTextView = findViewById(R.id.UsersTextView);

        // Установка начального состояния
        currentLayout = iceCreamLayout; // По умолчанию показываем iceCreamLayout
        showOnlyLayout(currentLayout);

        // Установка обработчиков кликов
        iceCreamTextView.setOnClickListener(view -> showOnlyLayout(iceCreamLayout));
        analysticTextView.setOnClickListener(view -> showOnlyLayout(analysticLayout));
        usersTextView.setOnClickListener(view -> showOnlyLayout(usersLayout));

        // Ініціалізація списку працівників
        employeeList = new ArrayList<>();
        // Додавання заголовків як перший елемент у списку
        employeeList.add(new String[]{"First Name", "Last Name", "Position", "Login", "Phone"});

        // Отримання даних з SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String idPoint = sharedPreferences.getString("point", null);

        // URL для запиту
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("function_name", "get_employee_for_company");
            requestJson.put("param_dict", new JSONObject()
                    .put("point", idPoint)
            );
        } catch (JSONException e) {
            Toast.makeText(EmployerActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        JSONArray employeesArray = jsonResponse.getJSONArray("result");


                        // Парсинг кожного працівника з JSON
                        for (int i = 0; i < employeesArray.length(); i++) {
                            JSONArray employeeArray = employeesArray.getJSONArray(i);

                            // Отримуємо дані для кожного працівника
                            String firstName = employeeArray.getString(0);
                            String lastName = employeeArray.getString(1);
                            String position = employeeArray.getString(2);
                            String login = employeeArray.getString(3);
                            String phone = employeeArray.getString(4);

                            // Додавання працівника до списку
                            employeeList.add(new String[]{firstName, lastName, position, login, phone});
                        }

                        // Оновлення адаптера після отримання нових даних
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(EmployerActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    // Обробка помилки
                    // Наприклад, показати повідомлення користувачу
                    System.out.println("Error: " + error);
                });
            }
        });

        // Створення LayoutManager для RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Встановлення адаптера
        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            // ViewHolder для одного елемента
            class EmployeeViewHolder extends RecyclerView.ViewHolder {
                TextView tvFirstName, tvLastName, tvPosition, tvLogin, tvPhone;
                ImageButton btnDeleteEmployee;

                public EmployeeViewHolder(View itemView) {
                    super(itemView);
                    tvFirstName = itemView.findViewById(R.id.tvFirstName);
                    tvLastName = itemView.findViewById(R.id.tvLastName);
                    tvPosition = itemView.findViewById(R.id.tvPosition);
                    tvLogin = itemView.findViewById(R.id.tvLogin);
                    tvPhone = itemView.findViewById(R.id.tvPhone);
                    btnDeleteEmployee = itemView.findViewById(R.id.btnDeleteEmployee);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Ініціалізація елемента макету для кожного елемента списку
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_employee, parent, false);
                return new EmployeeViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                // Заповнення даними з employeeList
                EmployeeViewHolder employeeHolder = (EmployeeViewHolder) holder;
                String[] employee = employeeList.get(position);

                employeeHolder.tvFirstName.setText(employee[0]);
                employeeHolder.tvLastName.setText(employee[1]);
                employeeHolder.tvPosition.setText(employee[2]);
                employeeHolder.tvLogin.setText(employee[3]);
                employeeHolder.tvPhone.setText(employee[4]);

                employeeHolder.btnDeleteEmployee.setOnClickListener(v -> {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION) {
                        return; // Якщо елемент більше не дійсний
                    }

                    String login = employeeList.get(currentPosition)[3];

                    // Видалення через HTTP-запит
                    JSONObject requestJson = new JSONObject();
                    try {
                        requestJson.put("function_name", "delete_employee");
                        requestJson.put("param_dict", new JSONObject().put("login", login));
                    } catch (JSONException e) {
                        Toast.makeText(holder.itemView.getContext(), "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Надсилаємо запит на видалення
                    HttpClientHelper httpClientHelper = new HttpClientHelper();
                    httpClientHelper.sendPostRequest(holder.itemView.getContext().getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            ((EmployerActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                                try {
                                    // Парсинг JSON-відповіді
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean result = jsonResponse.getBoolean("result");

                                    if (result) {
                                        // Видалення з локального списку
                                        employeeList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, employeeList.size());
                                        Toast.makeText(holder.itemView.getContext(), "Працівника видалено", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmployerActivity.this, "Помилка видалення: даного логіна не існує", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    // Обробка винятку
                                    Toast.makeText(holder.itemView.getContext(), "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            ((EmployerActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                                Toast.makeText(holder.itemView.getContext(), "Помилка видалення: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                });
            }

            @Override
            public int getItemCount() {
                return employeeList.size();
            }
        });

        Button btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnAddEmployee.setOnClickListener(view -> {
            // Створення діалогового вікна
            View dialogView = LayoutInflater.from(EmployerActivity.this).inflate(R.layout.dialog_add_employee, null);

            AlertDialog dialog = new AlertDialog.Builder(EmployerActivity.this)
                    .setTitle("Додати працівника")
                    .setView(dialogView)
                    .setPositiveButton("Додати", null) // Спочатку передаємо null
                    .setNegativeButton("Скасувати", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                    // Отримання введених даних
                    EditText etFirstName = dialogView.findViewById(R.id.etFirstName);
                    EditText etLastName = dialogView.findViewById(R.id.etLastName);
                    Spinner spRole = dialogView.findViewById(R.id.spRole);
                    EditText etPhone = dialogView.findViewById(R.id.etPhone);
                    EditText etLogin = dialogView.findViewById(R.id.etLogin);
                    EditText etPassword = dialogView.findViewById(R.id.etPassword);

                    String firstName = etFirstName.getText().toString().trim();
                    String lastName = etLastName.getText().toString().trim();
                    String role = spRole.getSelectedItem().toString();
                    String phone = etPhone.getText().toString().trim();
                    String login = etLogin.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    // Перевірка на порожні поля
                    if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || login.isEmpty() || password.isEmpty()) {
                        Toast.makeText(EmployerActivity.this, "Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                        return; // Діалог залишається відкритим
                    }

                    JSONObject employeeJson = new JSONObject();
                    try {
                        employeeJson.put("function_name", "add_employee");
                        employeeJson.put("param_dict", new JSONObject()
                                .put("first_name", firstName)
                                .put("last_name", lastName)
                                .put("position", role)
                                .put("login", login)
                                .put("phone", phone)
                                .put("password", password)
                                .put("point_id", idPoint)
                        );
                    } catch (JSONException e) {
                        Toast.makeText(EmployerActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    httpClientHelper.sendPostRequest(getString(R.string.api_url), employeeJson, new HttpClientHelper.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            runOnUiThread(() -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean result = jsonResponse.getBoolean("result");

                                    if (result) {
                                        // Додавання до списку
                                        employeeList.add(new String[]{firstName, lastName, role, login, phone});
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        Toast.makeText(EmployerActivity.this, "Працівника додано", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss(); // Закрити діалог при успіху
                                    } else {
                                        Toast.makeText(EmployerActivity.this, "Даний login вже існує", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(EmployerActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                });
            });

            dialog.show();

        });

        JSONObject requestJsonIceCreams = new JSONObject();
        try {
            requestJson.put("function_name", "get_all_object_for_company");
            requestJson.put("param_dict", new JSONObject()
                    .put("point", idPoint)
            );
        } catch (JSONException e) {
            Toast.makeText(EmployerActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

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

                        recyclerViewHorn.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                            // ViewHolder для одного элемента
                            class TypeHornViewHolder extends RecyclerView.ViewHolder {
                                TextView tvName, tvQuantity;

                                public TypeHornViewHolder(View itemView) {
                                    super(itemView);
                                    tvName = itemView.findViewById(R.id.tvName);
                                    tvQuantity = itemView.findViewById(R.id.tvQuantity);
                                }
                            }

                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                // Инициализация элемента макета для каждого элемента списка
                                View itemView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_horn, parent, false);
                                return new TypeHornViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                // Заполнение данными из hornList
                                TypeHornViewHolder hornHolder = (TypeHornViewHolder) holder;
                                TypeHorn horn = typeHorns.get(position);

                                hornHolder.tvName.setText(horn.getName());
                                hornHolder.tvQuantity.setText(String.valueOf(horn.getQuantity()));
                            }

                            @Override
                            public int getItemCount() {
                                return typeHorns.size();
                            }
                        });

                        recyclerViewFlavor.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                            class FlavorIceCreamViewHolder extends RecyclerView.ViewHolder {
                                TextView tvName, tvPrice, tvQuantity;

                                public FlavorIceCreamViewHolder(View itemView) {
                                    super(itemView);
                                    tvName = itemView.findViewById(R.id.tvName);
                                    tvPrice = itemView.findViewById(R.id.tvPrice);
                                    tvQuantity = itemView.findViewById(R.id.tvQuantity);
                                }
                            }

                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                // Инициализация элемента макета для каждого элемента списка
                                View itemView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_flavor, parent, false);
                                return new FlavorIceCreamViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                // Заполнение данными из flavorList
                                FlavorIceCreamViewHolder flavorHolder = (FlavorIceCreamViewHolder) holder;
                                FlavorIceCream flavor = flavorIceCreams.get(position);

                                flavorHolder.tvName.setText(flavor.getName());
                                flavorHolder.tvPrice.setText(String.valueOf(flavor.getPrice()));
                                flavorHolder.tvQuantity.setText(String.valueOf(flavor.getQuantity()));
                            }

                            @Override
                            public int getItemCount() {
                                return flavorIceCreams.size();
                            }
                        });

                        recyclerViewTopping.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                            // ViewHolder для одного элемента
                            class FlavorToppingViewHolder extends RecyclerView.ViewHolder {
                                TextView tvName, tvPrice, tvQuantity;

                                public FlavorToppingViewHolder(View itemView) {
                                    super(itemView);
                                    tvName = itemView.findViewById(R.id.tvName);
                                    tvPrice = itemView.findViewById(R.id.tvPrice);
                                    tvQuantity = itemView.findViewById(R.id.tvQuantity);
                                }
                            }

                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                // Инициализация элемента макета для каждого элемента списка
                                View itemView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_topping, parent, false);
                                return new FlavorToppingViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                // Заполнение данными из toppingList
                                FlavorToppingViewHolder toppingHolder = (FlavorToppingViewHolder) holder;
                                FlavorTopping topping = flavorToppings.get(position);

                                toppingHolder.tvName.setText(topping.getName());
                                toppingHolder.tvPrice.setText(String.valueOf(topping.getPrice()));
                                toppingHolder.tvQuantity.setText(String.valueOf(topping.getQuantity()));
                            }

                            @Override
                            public int getItemCount() {
                                return flavorToppings.size();
                            }
                        });

                        Log.d("JSON Response", response);

                    } catch (Exception e) {
                        Toast.makeText(EmployerActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    }

    private void showOnlyLayout(LinearLayout layoutToShow) {
        if (currentLayout != null) {
            currentLayout.setVisibility(View.GONE);
        }
        layoutToShow.setVisibility(View.VISIBLE);
        currentLayout = layoutToShow;
    }
}
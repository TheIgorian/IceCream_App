package com.example.icecream_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerViewFlavor, recyclerViewHorn, recyclerViewTopping,
            recyclerViewHornAnalytics, recyclerViewFlavorAnalytics, recyclerViewToppingAnalytics;
    private List<String[]> employeeList;

    private LinearLayout iceCreamLayout;
    private LinearLayout analyticsLayout;

    SharedPreferences sharedPreferences;

    private List<FlavorIceCream> flavorIceCreams, flavorIceCreamsAnalytics;
    private List<TypeHorn> typeHorns, typeHornsAnalytics;
    private List<FlavorTopping> flavorToppings, flavorToppingsAnalytics;
    private LinearLayout usersLayout;
    private LinearLayout currentLayout;

    private FlavorIceCreamAdapter adapterIceCream;
    private FlavorToppingAdapter adapterTopping;
    private TypeHornAdapter adapterHorn;

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

        recyclerViewHornAnalytics = findViewById(R.id.recyclerAnalyticsHorns);
        recyclerViewFlavorAnalytics = findViewById(R.id.recyclerAnalyticsFlavors);
        recyclerViewToppingAnalytics = findViewById(R.id.recyclerAnalyticsToppings);

        iceCreamLayout = findViewById(R.id.iceCreamLayout);
        analyticsLayout = findViewById(R.id.analysticLayout);
        usersLayout = findViewById(R.id.UsersLayout);

        TextView iceCreamTextView = findViewById(R.id.IceCreamTextView);
        TextView analysticTextView = findViewById(R.id.AnalysticTextView);
        TextView usersTextView = findViewById(R.id.UsersTextView);

        currentLayout = iceCreamLayout;
        showOnlyLayout(currentLayout);

        iceCreamTextView.setOnClickListener(view -> showOnlyLayout(iceCreamLayout));
        analysticTextView.setOnClickListener(view -> showOnlyLayout(analyticsLayout));
        usersTextView.setOnClickListener(view -> showOnlyLayout(usersLayout));

        employeeList = new ArrayList<>();
        employeeList.add(new String[]{"First Name", "Last Name", "Position", "Login", "Phone"});

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String idPoint = sharedPreferences.getString("point", null);

        recyclerViewHornAnalytics.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFlavorAnalytics.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewToppingAnalytics.setLayoutManager(new LinearLayoutManager(this));

        flavorIceCreamsAnalytics = new ArrayList<>();
        flavorToppingsAnalytics = new ArrayList<>();
        typeHornsAnalytics = new ArrayList<>();

        recyclerViewFlavorAnalytics.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            class FlavorIceCreamAnalyticsViewHolder extends RecyclerView.ViewHolder {
                TextView tvName, tvPrice, tvQuantity;

                public FlavorIceCreamAnalyticsViewHolder(View itemView) {
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
                return new FlavorIceCreamAnalyticsViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                // Заполнение данными из flavorList
                FlavorIceCreamAnalyticsViewHolder flavorHolder = (FlavorIceCreamAnalyticsViewHolder) holder;
                FlavorIceCream flavor = flavorIceCreamsAnalytics.get(position);

                flavorHolder.tvName.setText(flavor.getName());
                flavorHolder.tvPrice.setText(String.valueOf(flavor.getPrice()));
                flavorHolder.tvQuantity.setText(String.valueOf(flavor.getQuantity()));
            }

            @Override
            public int getItemCount() {
                return flavorIceCreamsAnalytics.size();
            }
        });
        recyclerViewToppingAnalytics.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            class FlavorToppingAnalyticsViewHolder extends RecyclerView.ViewHolder {
                TextView tvName, tvPrice, tvQuantity;

                public FlavorToppingAnalyticsViewHolder(View itemView) {
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
                return new FlavorToppingAnalyticsViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                // Заполнение данными из flavorList
                FlavorToppingAnalyticsViewHolder flavorHolder = (FlavorToppingAnalyticsViewHolder) holder;
                FlavorTopping flavorTopping = flavorToppingsAnalytics.get(position);

                flavorHolder.tvName.setText(flavorTopping.getName());
                flavorHolder.tvPrice.setText(String.valueOf(flavorTopping.getPrice()));
                flavorHolder.tvQuantity.setText(String.valueOf(flavorTopping.getQuantity()));
            }

            @Override
            public int getItemCount() {
                return flavorToppingsAnalytics.size();
            }
        });
        recyclerViewHornAnalytics.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            class FlavorHornsAnalyticsViewHolder extends RecyclerView.ViewHolder {
                TextView tvName, tvQuantity;

                public FlavorHornsAnalyticsViewHolder(View itemView) {
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
                return new FlavorHornsAnalyticsViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                // Заполнение данными из flavorList
                FlavorHornsAnalyticsViewHolder flavorHolder = (FlavorHornsAnalyticsViewHolder) holder;
                TypeHorn typeHorn = typeHornsAnalytics.get(position);

                flavorHolder.tvName.setText(typeHorn.getName());
                flavorHolder.tvQuantity.setText(String.valueOf(typeHorn.getQuantity()));
            }

            @Override
            public int getItemCount() {
                return typeHornsAnalytics.size();
            }
        });

        JSONObject requestJsonAnalytics = new JSONObject();
        try {
            requestJsonAnalytics.put("function_name", "get_stat_all");
            requestJsonAnalytics.put("param_dict", new JSONObject().put("point", idPoint));
        } catch (JSONException e) {
            Toast.makeText(EmployerActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        HttpClientHelper httpClientHelper = new HttpClientHelper();
        httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJsonAnalytics, new HttpClientHelper.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        // Обработка JSON-ответа
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        JSONArray flavorsArrayAnalytics = jsonObject.getJSONArray("flavor");
                        JSONArray hornsArrayAnalytics = jsonObject.getJSONArray("cone");
                        JSONArray toppingsArrayAnalytics = jsonObject.getJSONArray("additive");

                        // Очистка списков
                        flavorIceCreamsAnalytics.clear();
                        typeHornsAnalytics.clear();
                        flavorToppingsAnalytics.clear();

                        // Парсинг данных для вкусов мороженого
                        for (int i = 0; i < flavorsArrayAnalytics.length(); i++) {
                            JSONArray flavorArray = flavorsArrayAnalytics.getJSONArray(i);
                            String nameFlavor = flavorArray.getString(0);
                            double quantityFlavor = flavorArray.getDouble(1);
                            flavorIceCreamsAnalytics.add(new FlavorIceCream(nameFlavor, 0, quantityFlavor, null));
                        }

                        Collections.sort(flavorIceCreamsAnalytics, (f1, f2) -> Double.compare(f2.getQuantity(), f1.getQuantity()));

                        for (int i = 0; i < hornsArrayAnalytics.length(); i++) {
                            JSONArray hornArray = hornsArrayAnalytics.getJSONArray(i);
                            String nameHorn = hornArray.getString(0);
                            int quantityHorn = hornArray.getInt(1);
                            typeHornsAnalytics.add(new TypeHorn(nameHorn, quantityHorn, null));
                        }

                        Collections.sort(typeHornsAnalytics, (h1, h2) -> Integer.compare(h2.getQuantity(), h1.getQuantity()));

                        for (int i = 0; i < toppingsArrayAnalytics.length(); i++) {
                            JSONArray toppingArray = toppingsArrayAnalytics.getJSONArray(i);
                            String nameTopping = toppingArray.getString(0);
                            double quantityTopping = toppingArray.getDouble(1);
                            flavorToppingsAnalytics.add(new FlavorTopping(nameTopping, 0, quantityTopping, null));
                        }

                        Collections.sort(flavorToppingsAnalytics, (t1, t2) -> Double.compare(t2.getQuantity(), t1.getQuantity()));

                        recyclerViewFlavorAnalytics.getAdapter().notifyDataSetChanged();
                        recyclerViewToppingAnalytics.getAdapter().notifyDataSetChanged();
                        recyclerViewHornAnalytics.getAdapter().notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(EmployerActivity.this, "Ошибка обработки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray employeesArray = jsonResponse.getJSONArray("result");

                        for (int i = 0; i < employeesArray.length(); i++) {
                            JSONArray employeeArray = employeesArray.getJSONArray(i);

                            String firstName = employeeArray.getString(0);
                            String lastName = employeeArray.getString(1);
                            String position = employeeArray.getString(2);
                            String login = employeeArray.getString(3);
                            String phone = employeeArray.getString(4);

                            employeeList.add(new String[]{firstName, lastName, position, login, phone});
                        }

                        recyclerView.getAdapter().notifyDataSetChanged();

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_employee, parent, false);
                return new EmployeeViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
                        return;
                    }

                    String login = employeeList.get(currentPosition)[3];

                    JSONObject requestJson = new JSONObject();
                    try {
                        requestJson.put("function_name", "delete_employee");
                        requestJson.put("param_dict", new JSONObject().put("login", login));
                    } catch (JSONException e) {
                        Toast.makeText(holder.itemView.getContext(), "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HttpClientHelper httpClientHelper = new HttpClientHelper();
                    httpClientHelper.sendPostRequest(holder.itemView.getContext().getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            ((EmployerActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean result = jsonResponse.getBoolean("result");

                                    if (result) {
                                        employeeList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, employeeList.size());
                                        Toast.makeText(holder.itemView.getContext(), "Працівника видалено", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmployerActivity.this, "Помилка видалення: даного логіна не існує", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
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
            View dialogView = LayoutInflater.from(EmployerActivity.this).inflate(R.layout.dialog_add_employee, null);

            AlertDialog dialog = new AlertDialog.Builder(EmployerActivity.this)
                    .setTitle("Додати працівника")
                    .setView(dialogView)
                    .setPositiveButton("Додати", null)
                    .setNegativeButton("Скасувати", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
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

                    if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || login.isEmpty() || password.isEmpty()) {
                        Toast.makeText(EmployerActivity.this, "Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                        return;
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
                                        employeeList.add(new String[]{firstName, lastName, role, login, phone});
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        Toast.makeText(EmployerActivity.this, "Працівника додано", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
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

        recyclerViewHorn.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewFlavor.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewTopping.setLayoutManager(new GridLayoutManager(this, 2));

        flavorIceCreams = new ArrayList<>();
        flavorToppings = new ArrayList<>();
        typeHorns = new ArrayList<>();

        adapterIceCream = new FlavorIceCreamAdapter(this, flavorIceCreams);
        adapterTopping = new FlavorToppingAdapter(this, flavorToppings);
        adapterHorn = new TypeHornAdapter(this, typeHorns);

        JSONObject requestJsonIceCreams = new JSONObject();
        try {
            requestJsonIceCreams.put("function_name", "get_all_object_for_company");
            requestJsonIceCreams.put("param_dict", new JSONObject()
                    .put("point", idPoint)
            );
        } catch (JSONException e) {
            Toast.makeText(EmployerActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJsonIceCreams, new HttpClientHelper.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        JSONArray flavorsArray = jsonObject.getJSONArray("flavor");
                        JSONArray hornsArray = jsonObject.getJSONArray("cone");
                        JSONArray toppingsArray = jsonObject.getJSONArray("additive");

                        flavorIceCreams.clear();
                        typeHorns.clear();
                        flavorToppings.clear();

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
                        adapterIceCream = new FlavorIceCreamAdapter(EmployerActivity.this, flavorIceCreams);
                        adapterTopping = new FlavorToppingAdapter(EmployerActivity.this, flavorToppings);
                        adapterHorn = new TypeHornAdapter(EmployerActivity.this, typeHorns);

                        recyclerViewFlavor.setAdapter(adapterIceCream);
                        recyclerViewTopping.setAdapter(adapterTopping);
                        recyclerViewHorn.setAdapter(adapterHorn);

                        adapterIceCream.notifyDataSetChanged();
                        adapterTopping.notifyDataSetChanged();
                        adapterHorn.notifyDataSetChanged();

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

        TextView logoutTextView = findViewById(R.id.Logout);
        logoutTextView.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        TextView signLikeEmployee = findViewById(R.id.SignLikeEmployee);
        signLikeEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerActivity.this, EmployeeActivity.class);
            intent.putExtra("fromEmployer", true);
            startActivity(intent);
            finish();
        });

        Button addHornButton = findViewById(R.id.addHorn);
        addHornButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployerActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_horn, null);

            Spinner hornSpinner = dialogView.findViewById(R.id.hornSpinner);
            EditText quantityInput = dialogView.findViewById(R.id.quantityInput);
            Button confirmButton = dialogView.findViewById(R.id.confirmButton);
            Button cancelButton = dialogView.findViewById(R.id.cancelButton);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            confirmButton.setOnClickListener(view -> {
                String selectedHorn = hornSpinner.getSelectedItem().toString();
                String quantity = quantityInput.getText().toString();

                JSONObject employeeJson = new JSONObject();
                try {
                    employeeJson.put("function_name", "add_cone");
                    employeeJson.put("param_dict", new JSONObject()
                            .put("cone_name", selectedHorn)
                            .put("number", quantity)
                            .put("point", idPoint)
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

                                if (jsonResponse.has("result") && !jsonResponse.isNull("result")) {
                                    Toast.makeText(EmployerActivity.this, "Ріжок додано", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(EmployerActivity.this, "Дані ріжка оновилися", Toast.LENGTH_SHORT).show();

                                }
                                Intent intent = new Intent(EmployerActivity.this, EmployerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                startActivity(intent);
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

                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        });

        Button addFlavorButton = findViewById(R.id.addFlavor);
        addFlavorButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployerActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_flavor, null);

            Spinner flavorSpinner = dialogView.findViewById(R.id.flavorSpinner);
            EditText volumeInput = dialogView.findViewById(R.id.volumeInput);
            EditText priceInput = dialogView.findViewById(R.id.priceInput);
            Button confirmButton = dialogView.findViewById(R.id.confirmButton);
            Button cancelButton = dialogView.findViewById(R.id.cancelButton);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            confirmButton.setOnClickListener(view -> {
                String selectedFlavor = flavorSpinner.getSelectedItem().toString();
                String volume = volumeInput.getText().toString();
                String price = priceInput.getText().toString();

                JSONObject employeeJson = new JSONObject();
                try {
                    employeeJson.put("function_name", "add_ice_cream");
                    employeeJson.put("param_dict", new JSONObject()
                            .put("flavor_name", selectedFlavor)
                            .put("mass", volume)
                            .put("price", price)
                            .put("point", idPoint)
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

                                if (jsonResponse.has("result") && !jsonResponse.isNull("result")) {
                                    Toast.makeText(EmployerActivity.this, "Морозиво додано", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(EmployerActivity.this, "Дані морозива оновилися", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(EmployerActivity.this, EmployerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                startActivity(intent);
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

                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        });

        Button addToppingButton = findViewById(R.id.addTopping);
        addToppingButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployerActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_topping, null);

            Spinner toppingSpinner = dialogView.findViewById(R.id.toppingSpinner);
            EditText volumeInput = dialogView.findViewById(R.id.volumeInput);
            EditText priceInput = dialogView.findViewById(R.id.priceInput);
            Button confirmButton = dialogView.findViewById(R.id.confirmButton);
            Button cancelButton = dialogView.findViewById(R.id.cancelButton);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            confirmButton.setOnClickListener(view -> {
                String selectedTopping = toppingSpinner.getSelectedItem().toString();
                String volume = volumeInput.getText().toString();
                String price = priceInput.getText().toString();

                JSONObject employeeJson = new JSONObject();
                try {
                    employeeJson.put("function_name", "add_additive");
                    employeeJson.put("param_dict", new JSONObject()
                            .put("additive_name", selectedTopping)
                            .put("mass", volume)
                            .put("price", price)
                            .put("point", idPoint)
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

                                if (jsonResponse.has("result") && !jsonResponse.isNull("result")) {
                                    Toast.makeText(EmployerActivity.this, "Топінг додано", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(EmployerActivity.this, "Дані топінга оновилися", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(EmployerActivity.this, EmployerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                startActivity(intent);
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

                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
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
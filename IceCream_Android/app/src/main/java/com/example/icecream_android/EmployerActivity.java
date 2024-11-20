package com.example.icecream_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String[]> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer);

        // Ініціалізація RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

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

                public EmployeeViewHolder(View itemView) {
                    super(itemView);
                    tvFirstName = itemView.findViewById(R.id.tvFirstName);
                    tvLastName = itemView.findViewById(R.id.tvLastName);
                    tvPosition = itemView.findViewById(R.id.tvPosition);
                    tvLogin = itemView.findViewById(R.id.tvLogin);
                    tvPhone = itemView.findViewById(R.id.tvPhone);
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
            }

            @Override
            public int getItemCount() {
                return employeeList.size();
            }
        });
    }
}

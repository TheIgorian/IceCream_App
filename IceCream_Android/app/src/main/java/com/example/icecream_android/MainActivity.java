package com.example.icecream_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Знаходимо елементи на макеті
        EditText usernameEditText = findViewById(R.id.editText_username);
        EditText passwordEditText = findViewById(R.id.editText_password);
        Button loginButton = findViewById(R.id.button_login);

        // Обробка натискання кнопки
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отримуємо введені дані
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Перевірка чи поля не пусті
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Будь ласка, введіть логін і пароль!", Toast.LENGTH_SHORT).show();
                } else {
                    // Формування JSON-об'єкта
                    JSONObject requestJson = new JSONObject();
                    try {
                        requestJson.put("function_name", "password_verification");
                        requestJson.put("param_dict", new JSONObject()
                                .put("employee", username)
                                .put("password", password)
                        );
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Помилка формування JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Відправка запиту через HttpClientHelper
                    HttpClientHelper httpClientHelper = new HttpClientHelper();
                    httpClientHelper.sendPostRequest(getString(R.string.api_url), requestJson, new HttpClientHelper.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            runOnUiThread(() -> {
                                try {
                                    // Парсинг JSON-відповіді
                                    JSONObject jsonResponse = new JSONObject(response);
                                    if (jsonResponse.has("result") && !jsonResponse.isNull("result")) {
                                        JSONArray results = jsonResponse.getJSONArray("result");
                                        // Отримання значень id_point, id_employee, role
                                        String idPoint = results.getString(0);
                                        String idEmployee = results.getString(1);
                                        String role = results.getString(2);

                                        // Збереження значень у SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("point", idPoint);
                                        editor.putString("employee", idEmployee);
                                        editor.putString("role", role);
                                        editor.apply();

                                        // Перевірка ролі та передача в різні активності
                                        Intent intent = null;
                                        if ("Адмін".equals(role)) {
                                            // Якщо роль Адмін, переходимо до AdminActivity
                                            intent = new Intent(MainActivity.this, EmployerActivity.class);
                                        } else if ("Працівник".equals(role)) {
                                            // Якщо роль Працівник, переходимо до EmployeeActivity
                                            intent = new Intent(MainActivity.this, EmployeeActivity.class);
                                        }
                                        if (intent != null) {
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Невідома роль!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Якщо ключ "result" відсутній або null
                                        Toast.makeText(MainActivity.this, "Помилка: Невірний логін або пароль!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Помилка обробки JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "Помилка: " + error, Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                }
            }
        });
    }
}
package com.example.icecream_android;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // JSON-данные
        String jsonData = "{'result': [['Шоколад', 500.0, 500, '2f5c9485-c58c-46e3-960d-3802a274c2db'], ... ]}";

        List<FlavorIceCream> iceCreamList = parseJsonData(jsonData);
        FlavorIceCreamAdapter adapter = new FlavorIceCreamAdapter(this, iceCreamList);
        recyclerView.setAdapter(adapter);
    }

    private List<FlavorIceCream> parseJsonData(String jsonData) {
        List<FlavorIceCream> iceCreamList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONArray itemArray = resultArray.getJSONArray(i);
                String name = itemArray.getString(0);
                double price = itemArray.getDouble(1);
                int quantity = itemArray.getInt(2);
                String uuid = itemArray.getString(3);

                iceCreamList.add(new FlavorIceCream(name, price, quantity, uuid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iceCreamList;
    }
}
package com.example.icecream_android;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<FlavorIceCream> iceCreamList = new ArrayList<>();
        iceCreamList.add(new FlavorIceCream("Кокос", 125, 1));
        iceCreamList.add(new FlavorIceCream("Ваніль", 100, 2));
        iceCreamList.add(new FlavorIceCream("Лимон", 110, 3));
        iceCreamList.add(new FlavorIceCream("Лаванда", 135, 4));
        iceCreamList.add(new FlavorIceCream("Карамель", 115, 5));
        iceCreamList.add(new FlavorIceCream("Малина", 120, 6));
        iceCreamList.add(new FlavorIceCream("Полуниця", 110, 7));
        iceCreamList.add(new FlavorIceCream("М'ята", 105, 8));
        iceCreamList.add(new FlavorIceCream("Шоколад", 100, 9));

        FlavorIceCreamAdapter adapter = new FlavorIceCreamAdapter(this, iceCreamList);
        recyclerView.setAdapter(adapter);
    }
}
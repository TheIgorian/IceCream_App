package com.example.icecream_android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private FlavorIceCreamAdapter adapter;
    private List<FlavorIceCream> iceCreamList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new FlavorIceCreamAdapter(this, iceCreamList);
        recyclerView.setAdapter(adapter);

        fetchIceCreamData();
    }

    private void fetchIceCreamData() {
        String url = "https://example.com/api/icecream"; // Замість цього вставте реальну URL API

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resultArray = response.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONArray item = resultArray.getJSONArray(i);
                                String name = item.getString(0);
                                double weight = item.getDouble(1);
                                int price = item.getInt(2);
                                String imageName = item.getString(3);

                                iceCreamList.add(new FlavorIceCream(name, price, imageName));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(EmployeeActivity.this, "Помилка обробки JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EmployeeActivity.this, "Помилка запиту: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
}

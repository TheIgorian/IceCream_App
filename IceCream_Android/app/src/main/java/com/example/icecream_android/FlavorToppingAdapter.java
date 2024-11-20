package com.example.icecream_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlavorToppingAdapter extends RecyclerView.Adapter<FlavorToppingAdapter.ViewHolder> {

    private final Context context;
    private final List<FlavorTopping> toppingList;
    private List<FlavorTopping> filteredList;

    public FlavorToppingAdapter(Context context, List<FlavorTopping> iceCreamList) {
        this.context = context;
        this.toppingList = iceCreamList;
        this.filteredList = new ArrayList<>(iceCreamList);
    }

    private final List<FlavorTopping> selectedItems = new ArrayList<>();

    public List<FlavorTopping> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ice_cream, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlavorTopping item = filteredList.get(position);

        // Установка данных
        holder.toppingDetails.setText(item.getName());
        holder.quantityText.setText("Ціна: " + item.getPrice());

        // Установка изображения (пример на основе UUID или номера)
        int imageResId = context.getResources().getIdentifier(item.getUuid(), "drawable", context.getPackageName());
        holder.toppingImage.setImageResource(imageResId);

        // Проверка, выбран ли элемент, и установка соответствующего фона
        if (selectedItems.contains(item)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Выделить элемент
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Убрать выделение
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Убрати виділення
            } else {
                if (selectedItems.size() < 3) { // Перевірка на максимум 3
                    selectedItems.add(item);
                    holder.itemView.setBackgroundColor(Color.LTGRAY); // Виділити елемент
                } else {
                    Toast.makeText(context, "Можна обрати не більше 3 видів топінга", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    @SuppressLint("NotifyDataSetChanged")
    public void filterTopping(String query) {
        // Очищуємо список перед фільтрацією
        filteredList.clear();

        if (query.isEmpty()) {
            // Якщо запит порожній, показуємо весь список
            filteredList.addAll(toppingList);
        } else {
            // Додаємо тільки ті елементи, які відповідають запиту
            String lowerCaseQuery = query.toLowerCase();
            for (FlavorTopping item : toppingList) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(item); // Додаємо відповідний елемент
                }
            }
        }
        // Оновлюємо RecyclerView
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView toppingImage;
        TextView toppingDetails, quantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toppingImage = itemView.findViewById(R.id.iceCreamImage);
            toppingDetails = itemView.findViewById(R.id.iceCreamDetails);
            quantityText = itemView.findViewById(R.id.quantity);
        }
    }
}

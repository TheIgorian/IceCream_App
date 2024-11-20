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

public class TypeHornAdapter extends RecyclerView.Adapter<TypeHornAdapter.ViewHolder> {

    private final Context context;
    private final List<TypeHorn> hornList;
    private List<TypeHorn> filteredList;

    public TypeHornAdapter(Context context, List<TypeHorn> iceCreamList) {
        this.context = context;
        this.hornList = iceCreamList;
        this.filteredList = new ArrayList<>(iceCreamList);
    }

    private final List<TypeHorn> selectedItems = new ArrayList<>();

    public List<TypeHorn> getSelectedItems() {
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
        TypeHorn item = filteredList.get(position);

        // Установка даних
        holder.hornDetails.setText(item.getName());
        holder.quantityText.setText("Кількість: " + item.getQuantity());

        // Установка зображення
        int imageResId = context.getResources().getIdentifier(item.getUuid(), "drawable", context.getPackageName());
        holder.hornImage.setImageResource(imageResId);

        // Перевірка, чи вибраний елемент
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
                if (selectedItems.size() < 1) { // Перевірка на максимум 1
                    selectedItems.add(item);
                    holder.itemView.setBackgroundColor(Color.LTGRAY); // Виділити елемент
                } else {
                    Toast.makeText(context, "Можна обрати тільки один ріжок", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() { return filteredList.size(); }

    @SuppressLint("NotifyDataSetChanged")
    public void filterHorn(String query) {
        // Очищуємо список перед фільтрацією
        filteredList.clear();

        if (query.isEmpty()) {
            // Якщо запит порожній, показуємо весь список
            filteredList.addAll(hornList);
        } else {
            // Додаємо тільки ті елементи, які відповідають запиту
            String lowerCaseQuery = query.toLowerCase();
            for (TypeHorn item : hornList) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(item); // Додаємо відповідний елемент
                }
            }
        }
        // Оновлюємо RecyclerView
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView hornImage;
        TextView hornDetails, quantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hornImage = itemView.findViewById(R.id.iceCreamImage);
            hornDetails = itemView.findViewById(R.id.iceCreamDetails);
            quantityText = itemView.findViewById(R.id.quantity);
        }
    }
}

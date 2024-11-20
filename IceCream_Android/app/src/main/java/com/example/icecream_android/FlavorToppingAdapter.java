package com.example.icecream_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlavorToppingAdapter extends RecyclerView.Adapter<FlavorToppingAdapter.ViewHolder> {

    private final Context context;
    private final List<FlavorTopping> ToppingList;

    public FlavorToppingAdapter(Context context, List<FlavorTopping> iceCreamList) {
        this.context = context;
        this.ToppingList = iceCreamList;
    }

    private final List<FlavorTopping> selectedItems = new ArrayList<>();

    public List<FlavorTopping> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged(); // Оновлює всі елементи адаптера
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
        FlavorTopping item = ToppingList.get(position);

        // Установка данных
        holder.toppingDetails.setText(item.getName());
        holder.quantityText.setText("Кількість: " + item.getQuantity());

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
                holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Убрать выделение
            } else {
                selectedItems.add(item);
                holder.itemView.setBackgroundColor(Color.LTGRAY); // Выделить элемент
            }
        });
    }

    @Override
    public int getItemCount() { return ToppingList.size(); }

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

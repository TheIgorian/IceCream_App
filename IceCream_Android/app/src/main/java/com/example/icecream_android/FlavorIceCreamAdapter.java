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

public class FlavorIceCreamAdapter extends RecyclerView.Adapter<FlavorIceCreamAdapter.ViewHolder> {

    private final Context context;
    private final List<FlavorIceCream> iceCreamList;

    public FlavorIceCreamAdapter(Context context, List<FlavorIceCream> iceCreamList) {
        this.context = context;
        this.iceCreamList = iceCreamList;
    }

    private final List<FlavorIceCream> selectedItems = new ArrayList<>();

    public List<FlavorIceCream> getSelectedItems() {
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
        FlavorIceCream item = iceCreamList.get(position);

        // Установка данных
        holder.iceCreamDetails.setText(item.getName());
        holder.quantityText.setText("Ціна: " + item.getPrice());

        // Установка изображения (пример на основе UUID или номера)
        int imageResId = context.getResources().getIdentifier(item.getUuid(), "drawable", context.getPackageName());
        holder.iceCreamImage.setImageResource(imageResId);

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
    public int getItemCount() {
        return iceCreamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iceCreamImage;
        TextView iceCreamDetails, quantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iceCreamImage = itemView.findViewById(R.id.iceCreamImage);
            iceCreamDetails = itemView.findViewById(R.id.iceCreamDetails);
            quantityText = itemView.findViewById(R.id.quantity);
        }
    }
}

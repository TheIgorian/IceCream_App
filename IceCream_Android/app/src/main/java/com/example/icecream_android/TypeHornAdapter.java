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

public class TypeHornAdapter extends RecyclerView.Adapter<TypeHornAdapter.ViewHolder> {

    private final Context context;
    private final List<TypeHorn> HornList;

    public TypeHornAdapter(Context context, List<TypeHorn> iceCreamList) {
        this.context = context;
        this.HornList = iceCreamList;
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
        TypeHorn item = HornList.get(position);

        // Установка данных
        holder.hornDetails.setText(item.getName());
        holder.quantityText.setText("Кількість: " + item.getQuantity());

        // Установка изображения (пример на основе UUID или номера)
        int imageResId = context.getResources().getIdentifier(item.getUuid(), "drawable", context.getPackageName());
        holder.hornImage.setImageResource(imageResId);

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
    public int getItemCount() { return HornList.size(); }

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

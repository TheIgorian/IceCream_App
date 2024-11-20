package com.example.icecream_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlavorToppingAdapter extends RecyclerView.Adapter<FlavorToppingAdapter.ViewHolder> {

    private final Context context;
    private final List<FlavorTopping> ToppingList;

    public FlavorToppingAdapter(Context context, List<FlavorTopping> iceCreamList) {
        this.context = context;
        this.ToppingList = iceCreamList;
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
        holder.ToppingDetails.setText(item.getName() + " - " + item.getPrice() + " грн");
        holder.quantityText.setText("Кількість: " + item.getQuantity());

        // Установка изображения (пример на основе UUID или номера)
        //String imageName = "ice_" + position; // Логика получения изображения
        int imageResId = context.getResources().getIdentifier(item.getUuid(), "drawable", context.getPackageName());
        holder.ToppingImage.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() { return ToppingList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ToppingImage;
        TextView ToppingDetails, quantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ToppingImage = itemView.findViewById(R.id.iceCreamImage);
            ToppingDetails = itemView.findViewById(R.id.iceCreamDetails);
            quantityText = itemView.findViewById(R.id.quantity);
        }
    }
}

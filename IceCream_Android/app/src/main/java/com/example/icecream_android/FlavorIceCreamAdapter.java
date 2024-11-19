package com.example.icecream_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlavorIceCreamAdapter extends RecyclerView.Adapter<FlavorIceCreamAdapter.ViewHolder> {

    private final Context context;
    private final List<FlavorIceCream> iceCreamList;

    public FlavorIceCreamAdapter(Context context, List<FlavorIceCream> iceCreamList) {
        this.context = context;
        this.iceCreamList = iceCreamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ice_cream, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlavorIceCream item = iceCreamList.get(position);

        // Установка данных
        holder.iceCreamDetails.setText(item.getName() + " - " + item.getPrice() + " грн");
        holder.quantityText.setText("Кількість: " + item.getQuantity());

        // Установка изображения (пример на основе UUID или номера)
        String imageName = "ice_" + position; // Логика получения изображения
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        holder.iceCreamImage.setImageResource(imageResId);
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

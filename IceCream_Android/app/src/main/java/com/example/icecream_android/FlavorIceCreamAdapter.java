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

    // Конструктор адаптера
    public FlavorIceCreamAdapter(Context context, List<FlavorIceCream> iceCreamList) {
        this.context = context;
        this.iceCreamList = iceCreamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание представления для элемента
        View view = LayoutInflater.from(context).inflate(R.layout.item_ice_cream, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Получение текущего элемента
        FlavorIceCream iceCream = iceCreamList.get(position);

        String imageName = "ice_" + iceCream.getImageNumber();
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        holder.iceCreamImage.setImageResource(imageResId);

        // Установка текста (название + цена)
        holder.iceCreamDetails.setText(iceCream.getName() + " - " + iceCream.getPrice() + " грн");
    }

    @Override
    public int getItemCount() {
        return iceCreamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iceCreamImage;
        TextView iceCreamDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iceCreamImage = itemView.findViewById(R.id.iceCreamImage);
            iceCreamDetails = itemView.findViewById(R.id.iceCreamDetails);
        }
    }
}

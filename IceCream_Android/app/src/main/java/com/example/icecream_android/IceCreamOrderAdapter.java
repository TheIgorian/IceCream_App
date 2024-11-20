package com.example.icecream_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IceCreamOrderAdapter extends RecyclerView.Adapter<IceCreamOrderAdapter.ViewHolder> {

    private final List<IceCreamOrder> orderList;
    private final EmployeeActivity activity; // Reference to EmployeeActivity

    public IceCreamOrderAdapter(List<IceCreamOrder> orderList, EmployeeActivity activity) {
        this.orderList = orderList;
        this.activity = activity; // Assign the activity reference
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_check, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IceCreamOrder order = orderList.get(position);

        String hornText = order.getHorn() + " ріжок. ";
        String flavorsText = "Смак: " + String.join(", ", order.getFlavors()) + ". ";
        String toppingsText = "Топінг: " + String.join(", ", order.getToppings()) + ". ";

        holder.iceCreamName.setText(hornText + flavorsText + toppingsText);

        int sum = order.getFlavors_prices().stream().mapToInt(Integer::intValue).sum();
        int count = order.getCount();
        double totalPrice = sum * count;

        holder.iceCreamPrice.setText("Ціна: " + totalPrice + "₴");

        holder.increaseButton.setOnClickListener(v -> {
            int newCount = count + 1;
            order.setCount(newCount);
            notifyDataSetChanged();
            activity.updateTotalSum(); // Call updateTotalSum in EmployeeActivity
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (count > 1) {
                int newCount = count - 1;
                order.setCount(newCount);
                notifyDataSetChanged();
                activity.updateTotalSum(); // Call updateTotalSum in EmployeeActivity
            }
        });

        holder.itemCount.setText(String.valueOf(count));
    }

    public List<IceCreamOrder> getOrderList() {
        return orderList;
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView iceCreamName;
        TextView iceCreamPrice;
        TextView decreaseButton;
        TextView itemCount;
        TextView increaseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iceCreamName = itemView.findViewById(R.id.iceCreamName);
            iceCreamPrice = itemView.findViewById(R.id.iceCreamPrice);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            itemCount = itemView.findViewById(R.id.itemCount);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }
    }
}

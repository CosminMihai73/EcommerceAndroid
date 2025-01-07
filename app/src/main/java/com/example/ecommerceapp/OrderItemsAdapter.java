package com.example.ecommerceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder> {

    private Context context;
    private List<OrderItem> orderItemsList;

    public OrderItemsAdapter(Context context, List<OrderItem> orderItemsList) {
        this.context = context;
        this.orderItemsList = orderItemsList;
    }

    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        OrderItem item = orderItemsList.get(position);
        holder.productName.setText("Produs: " + item.getProductName());
        holder.quantity.setText("Cantitate: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView productName, quantity;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}

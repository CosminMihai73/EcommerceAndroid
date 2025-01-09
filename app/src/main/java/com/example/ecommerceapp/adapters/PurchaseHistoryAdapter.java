package com.example.ecommerceapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.OrderDetailActivity;

import java.util.List;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseViewHolder> {

    private Context context;
    private List<Order> orderList;

    public PurchaseHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public PurchaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.fullName.setText("Nume: " + order.getFullName());
        holder.address.setText("Adresă: " + order.getAddress());
        holder.paymentMethod.setText("Plată: " + order.getPaymentMethod());
        holder.deliveryMethod.setText("Livrare: " + order.getDeliveryMethod());
        holder.totalPrice.setText("Total: " + order.getTotalPrice() + " RON");
        holder.createdAt.setText("Data: " + order.getCreatedAt());

        // Setează click listener pentru a trimite utilizatorul la detaliile comenzii
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getOrderId());  // Trimite ID-ul comenzii
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, address, paymentMethod, deliveryMethod, totalPrice, createdAt;

        public PurchaseViewHolder(View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            address = itemView.findViewById(R.id.address);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            deliveryMethod = itemView.findViewById(R.id.deliveryMethod);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            createdAt = itemView.findViewById(R.id.createdAt);
        }
    }
}

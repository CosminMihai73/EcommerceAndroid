package com.example.ecommerceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReservationHistoryAdapter extends RecyclerView.Adapter<ReservationHistoryAdapter.ReservationViewHolder> {

    private Context context;
    private List<ReservationItem> reservationList;

    public ReservationHistoryAdapter(Context context, List<ReservationItem> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        ReservationItem item = reservationList.get(position);
        holder.productName.setText("Produs: " + item.getProductName());
        holder.quantity.setText("Cantitate: " + item.getQuantity());
        holder.expirationDate.setText("Expiră pe: " + item.getExpirationDate());
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView productName, quantity, expirationDate;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            expirationDate = itemView.findViewById(R.id.expirationDate);
        }
    }
}

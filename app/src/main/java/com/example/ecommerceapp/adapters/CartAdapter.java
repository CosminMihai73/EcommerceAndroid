package com.example.ecommerceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.CartActivity;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartManager cartManager;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        cartManager = new CartManager(context);
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("000.00");
        String formattedPrice = decimalFormat.format(cartItem.getProductPrice());

        holder.productName.setText(cartItem.getProductName());
        holder.productQuantity.setText("Cantitate: " + cartItem.getQuantity());
        holder.productPrice.setText("Preț: " + formattedPrice);

        Glide.with(context)
                .load(cartItem.getImagePath())
                .into(holder.productImage);

        holder.incrementButton.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartManager.saveCartItems(cartItems);
            notifyDataSetChanged();
            ((CartActivity) context).updateTotal();
        });

        holder.decrementButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartManager.saveCartItems(cartItems);
                notifyDataSetChanged();
                ((CartActivity) context).updateTotal();
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            cartItems.remove(position);
            cartManager.saveCartItems(cartItems);
            notifyDataSetChanged();
            ((CartActivity) context).updateTotal();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productPrice;
        ImageView productImage;
        Button incrementButton, decrementButton, deleteButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

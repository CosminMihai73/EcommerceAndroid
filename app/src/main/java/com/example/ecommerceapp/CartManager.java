package com.example.ecommerceapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "cart_preferences";
    private static final String CART_KEY = "cart_items";
    private static final String TOTAL_KEY = "total_price";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveCartItems(List<CartItem> cartItems) {
        String cartJson = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_KEY, cartJson).apply();

        // Recalculăm totalul și îl salvăm
        double totalPrice = getTotalPrice();
        sharedPreferences.edit().putFloat(TOTAL_KEY, (float) totalPrice).apply();  // Salvăm prețul total
    }

    public List<CartItem> getCartItems() {
        String cartJson = sharedPreferences.getString(CART_KEY, "[]");
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(cartJson, type);
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem cartItem : getCartItems()) {
            total += cartItem.getProductPrice() * cartItem.getQuantity();
        }

        // Formatează totalul cu 2 zecimale și asigură-te că are 3 caractere înainte de virgulă
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return Double.parseDouble(decimalFormat.format(total));
    }

    public double getSavedTotalPrice() {
        return sharedPreferences.getFloat(TOTAL_KEY, 0);
    }

    public void addToCart(Product product) {
        List<CartItem> cartItems = getCartItems();
        boolean productExists = false;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductName().equals(product.getName()) && cartItem.getProductBrand().equals(product.getBrand())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            cartItems.add(new CartItem(product.getName(), product.getBrand(), product.getPrice(), 1, product.getImagePath()));
        }

        saveCartItems(cartItems);
    }
}

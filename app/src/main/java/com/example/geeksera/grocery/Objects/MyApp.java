package com.example.geeksera.grocery.Objects;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.example.geeksera.grocery.R;
import com.example.geeksera.grocery.Services.ServiceForCarts;

import java.util.LinkedList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by GeeksEra on 12/24/2017.
 */

public class MyApp extends MultiDexApplication {

    List<Product> Cart = new LinkedList<>();

    boolean Copen;
    int Discount = 0;

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
    }

    Float Total = 0f;
    Float SubTotal = 0f;

    public Float getTotal() {
        return Total;
    }

    public void setTotal(Float total) {
        Total = total;
    }

    public Float getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(Float subTotal) {
        SubTotal = subTotal;
    }

    public boolean isCopen() {
        return Copen;
    }

    public void setCopen(boolean copen) {
        Copen = copen;
    }

    public List<Product> getCart() {
        return Cart;
    }

    public Boolean setCart(Product cart) {

        for (Product p : Cart) {
            if (p.getProductId().equals(cart.getProductId())) {
                return false;
            }
        }
        Intent myService = new Intent(getApplicationContext(), ServiceForCarts.class);
        if (isMyServiceRunning(ServiceForCarts.class))
            stopService(myService);
        startService(myService);

        Cart.add(cart);
        return true;
    }

    public void UpdateQuantity(int update, int Position) {
        Product product = Cart.get(Position);
        int Quantity = product.getQuantity();
        Quantity = Quantity + update;
        if (Quantity < 1) {
            product.setQuantity(1);
        } else
            product.setQuantity(Quantity);
        Cart.set(Position, product);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ionicons.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public void RemoveCart(int Position) {
        Cart.remove(Position);
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

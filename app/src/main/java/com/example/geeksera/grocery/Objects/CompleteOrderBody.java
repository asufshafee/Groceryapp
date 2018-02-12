package com.example.geeksera.grocery.Objects;

import java.util.List;

/**
 * Created by GeeksEra on 12/29/2017.
 */

public class CompleteOrderBody {
    String pickupTime;
    String phone;
    String type;
    String transactionId;
    List<cartItems> cartItems;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPickupTime() {

        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<com.example.geeksera.grocery.Objects.cartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<com.example.geeksera.grocery.Objects.cartItems> cartItems) {
        this.cartItems = cartItems;
    }
}

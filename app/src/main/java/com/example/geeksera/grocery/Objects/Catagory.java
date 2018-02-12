package com.example.geeksera.grocery.Objects;

import java.io.Serializable;

/**
 * Created by GeeksEra on 12/23/2017.
 */

public class Catagory implements Serializable {
    String Price, url;

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

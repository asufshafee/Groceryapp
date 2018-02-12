package com.example.geeksera.grocery.Objects;

import java.io.Serializable;

/**
 * Created by GeeksEra on 12/22/2017.
 */

public class Product implements Serializable {

    private String name;
    private String Price;
    private String image;
    private String productId;
    private String model;
    private String description;
    private String Specification;
    private String ingredients;
    private String warnings;
    private String condition;
    private int Quantity = 1;
    private int available_stock_qty = 1;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecification() {
        return Specification;
    }

    public void setSpecification(String specification) {
        Specification = specification;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAvailable_stock_qty() {
        return available_stock_qty;
    }

    public void setAvailable_stock_qty(int available_stock_qty) {
        this.available_stock_qty = available_stock_qty;
    }
}

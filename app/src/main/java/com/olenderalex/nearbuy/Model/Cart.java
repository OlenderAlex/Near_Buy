package com.olenderalex.nearbuy.Model;

public class Cart {
    String id,productName,price,discount,quantity;


    public Cart(){

    }

    public Cart(String id, String productName, String price, String discount, String quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

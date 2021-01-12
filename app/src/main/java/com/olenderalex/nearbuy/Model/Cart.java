package com.olenderalex.nearbuy.Model;

public class Cart {
  private String id,productName,price,discount,quantity,image;
    private String uploaded_at_date,uploaded_at_time;


    public Cart(){

    }

    public Cart(String id, String productName, String price,
                String discount, String quantity, String image,
                String uploaded_at_date, String uploaded_at_time) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.image=image;
        this.uploaded_at_date = uploaded_at_date;
        this.uploaded_at_time = uploaded_at_time;
    }

    public String getUploaded_at_date() {
        return uploaded_at_date;
    }

    public void setUploaded_at_date(String uploaded_at_date) {
        this.uploaded_at_date = uploaded_at_date;
    }

    public String getUploaded_at_time() {
        return uploaded_at_time;
    }

    public void setUploaded_at_time(String uploaded_at_time) {
        this.uploaded_at_time = uploaded_at_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

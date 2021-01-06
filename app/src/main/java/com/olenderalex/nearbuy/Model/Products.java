package com.olenderalex.nearbuy.Model;

public class Products {

    private String productName ,description ,category ,price;
    private String id , uploaded_at_date ,uploaded_at_time;
    private String image;


    public Products(){

    }

    public Products(String productName, String description, String category, String price, String id, String uploaded_at_date, String uploaded_at_time, String image) {
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.id = id;
        this.uploaded_at_date = uploaded_at_date;
        this.uploaded_at_time = uploaded_at_time;
        this.image = image;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}

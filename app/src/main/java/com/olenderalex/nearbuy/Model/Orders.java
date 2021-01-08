package com.olenderalex.nearbuy.Model;

import static com.olenderalex.nearbuy.Utils.Util.totalPrice;

public class Orders {

    private String total_price, address, city, uploaded_at_date, uploaded_at_time;
    private String name, phone, state;

    public Orders() {

    }

    public Orders(String total_price, String address, String city, String uploaded_at_date, String uploaded_at_time, String name, String phone, String state) {
        this.total_price = total_price;
        this.address = address;
        this.city = city;
        this.uploaded_at_date = uploaded_at_date;
        this.uploaded_at_time = uploaded_at_time;
        this.name = name;
        this.phone = phone;
        this.state = state;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

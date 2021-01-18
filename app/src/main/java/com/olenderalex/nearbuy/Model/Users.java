package com.olenderalex.nearbuy.Model;

import androidx.annotation.NonNull;

public class Users {
    private String name ,phone,password ,image ,address,city;

    public Users(){

    }



    public Users(String name, String id, String phone, String password, String image, String address, String city) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.city=city;
    }
    public Users(Users user) {
        this.name = user.name;
        this.phone = user.phone;
        this.password = user.password;
        this.image =user. image;
        this.address = user.address;
        this.city=user.city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

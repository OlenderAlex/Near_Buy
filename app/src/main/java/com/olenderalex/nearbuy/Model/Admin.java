package com.olenderalex.nearbuy.Model;

public class Admin {
    private String name ,password, login ;
    public Admin(){

    }

    public Admin(Admin admin) {
        this.name = admin.name;
        this.password = admin.password;
        this.login = admin.login;
    }
    public Admin(String name, String phone, String login) {
        this.name = name;
        this.password = phone;
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String phone) {
        this.password = phone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}

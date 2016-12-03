package com.yanclement.geophone.model;

/**
 * Created by YPierru on 29/11/2016.
 */

public class Contact {

    private String name;
    private String phone;

    public Contact(String name,String phone) {
        this.name = name;
        this.phone=phone;
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

}

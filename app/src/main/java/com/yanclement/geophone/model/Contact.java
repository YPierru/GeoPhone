package com.yanclement.geophone.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YPierru on 29/11/2016.
 */

public class Contact {

    private String name;
    private String phone;
    private Date date;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Contact(String name,String phone,Date date) {
        this.name = name;
        this.phone=phone;
        this.date=date;
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

    public Date getDate() {
        return date;
    }

    public String getStringDate(){
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

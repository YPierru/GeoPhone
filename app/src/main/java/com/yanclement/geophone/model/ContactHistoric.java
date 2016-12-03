package com.yanclement.geophone.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YPierru on 01/12/2016.
 */

public class ContactHistoric extends Contact {
    private Date date;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ContactHistoric(String name,String phone,Date date) {
        super(name,phone);
        this.date=date;
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

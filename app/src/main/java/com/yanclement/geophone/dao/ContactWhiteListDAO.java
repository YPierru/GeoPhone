package com.yanclement.geophone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yanclement.geophone.model.Contact;

/**
 * Created by YPierru on 02/12/2016.
 */

public class ContactWhiteListDAO extends DatabaseDAO {


    public ContactWhiteListDAO(Context context) {
        super(context);
    }

    public long addContacWhiteList(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NAME_COLUMN, contact.getName());
        values.put(MySQLiteHelper.PHONE_COLUMN, contact.getPhone());

        return database.insert(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE, null, values);
    }


    public Cursor getCursorContactWhiteList() {

        Cursor cursor = database.query(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE,
                new String[] { MySQLiteHelper.ID_COLUMN,
                        MySQLiteHelper.NAME_COLUMN,
                        MySQLiteHelper.PHONE_COLUMN}, null, null, null, null,
                null);

        return cursor;
    }

}

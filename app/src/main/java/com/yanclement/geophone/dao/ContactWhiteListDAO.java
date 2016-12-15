package com.yanclement.geophone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yanclement.geophone.model.Contact;

/**
 * Created by YPierru on 02/12/2016.
 *
 */

/**
 * Operations on the database for white list contacts
 */
public class ContactWhiteListDAO extends DatabaseDAO {


    public ContactWhiteListDAO(Context context) {
        super(context);
    }


    /**
     * Add contact if unique
     * @param contact
     * @return
     */
    public long addContacWhiteList(Contact contact) {
        Cursor cursor =  database.rawQuery("SELECT count(*) from "
                                            +MySQLiteHelper.CONTACT_WHITE_LIST_TABLE_NAME
                                            +" where "
                                            +MySQLiteHelper.NAME_COLUMN
                                            +"="
                                            +"\""+contact.getName()+"\""
                                            +" AND "
                                            +MySQLiteHelper.PHONE_COLUMN
                                            +"="
                                            +"\""+contact.getPhone()+"\""
                                            +";",null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        if(count==0) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.NAME_COLUMN, contact.getName());
            values.put(MySQLiteHelper.PHONE_COLUMN, contact.getPhone());

            return database.insert(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE_NAME, null, values);
        }else{
            return -10;
        }
    }

    /**
     * Delete a given contact
     * @param name
     * @param phone
     */
    public void deleteContactWhiteList(String name, String phone){
        String whereClause = MySQLiteHelper.NAME_COLUMN+"=? AND "+MySQLiteHelper.PHONE_COLUMN+"=?";
        String[] whereArgs = new String[] { name,phone };
        database.delete(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * Delete all contacts
     */
    public void deleteAllContactsWhiteList(){
        database.delete(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE_NAME, null, null);
    }


    /**
     * Return cursor of the table
     * @return
     */
    public Cursor getCursorContactWhiteList() {
        return database.query(MySQLiteHelper.CONTACT_WHITE_LIST_TABLE_NAME,
                new String[] { MySQLiteHelper.ID_COLUMN,
                        MySQLiteHelper.NAME_COLUMN,
                        MySQLiteHelper.PHONE_COLUMN}, null, null, null, null,
                null);
    }

}

package com.yanclement.geophone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yanclement.geophone.model.ContactHistoric;

/**
 * Created by YPierru on 02/12/2016.
 */
/**
 * Operations on the database for historic contacts
 */
public class ContactHistoricDAO extends DatabaseDAO{

    public ContactHistoricDAO(Context context) {
        super(context);
    }

    public long addContactHistoric(ContactHistoric contactHistoric) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NAME_COLUMN, contactHistoric.getName());
        values.put(MySQLiteHelper.PHONE_COLUMN, contactHistoric.getPhone());
        values.put(MySQLiteHelper.DATE_COLUMN, contactHistoric.getStringDate());

        return database.insert(MySQLiteHelper.CONTACT_HISTORIC_TABLE_NAME, null, values);
    }

    public Cursor getCursorContactHistoric() {

        Cursor cursor = database.query(MySQLiteHelper.CONTACT_HISTORIC_TABLE_NAME,
                new String[] { MySQLiteHelper.ID_COLUMN,
                        MySQLiteHelper.NAME_COLUMN,
                        MySQLiteHelper.PHONE_COLUMN,
                        MySQLiteHelper.DATE_COLUMN}, null, null, null, null,
                null);


        return cursor;
    }

    public void deleteContactHistoric(String name, String phone){
        String whereClause = MySQLiteHelper.NAME_COLUMN+"=? AND "+MySQLiteHelper.PHONE_COLUMN+"=?";
        String[] whereArgs = new String[] { name,phone };
        database.delete(MySQLiteHelper.CONTACT_HISTORIC_TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteAllContactsHistoric(){
        database.delete(MySQLiteHelper.CONTACT_HISTORIC_TABLE_NAME, null, null);
    }

}

package com.yanclement.geophone.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by YPierru on 02/12/2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "geophonedb";
    private static final int DATABASE_VERSION = 1;

    public static final String CONTACT_HISTORIC_TABLE = "contact_h";
    public static final String CONTACT_WHITE_LIST_TABLE = "contact_wl";
    public static final String SETTINGS_TABLE = "settings";

    /**
     * Commun column
     */
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";

    /**
     * Contact historic column
     */
    public static final String DATE_COLUMN = "date";

    /**
     * Settings column
     */
    public static final String TEXT_ALERT_COLUMN = "text_alert";
    public static final String FLASH_COLUMN = "flash";
    public static final String VIBRATE_COLUMN = "vibrate";
    public static final String RINGTONE_COLUMN = "ringtone";

    public static final String CREATE_CONTACT_HISTORIC_TABLE = "CREATE TABLE "
            + CONTACT_HISTORIC_TABLE
            + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME_COLUMN + " TEXT, "
            + PHONE_COLUMN + " TEXT, "
            + DATE_COLUMN + " TEXT "
            + ")";

    public static final String CREATE_CONTACT_WHITE_LIST_TABLE = "CREATE TABLE "
            + CONTACT_WHITE_LIST_TABLE
            + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME_COLUMN + " TEXT, "
            + PHONE_COLUMN + " TEXT "
            + ")";


    public static final String CREATE_SETTINGS_TABLE = "CREATE TABLE "
            + SETTINGS_TABLE
            + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + TEXT_ALERT_COLUMN + " TEXT, "
            + FLASH_COLUMN + " INTEGER, "
            + VIBRATE_COLUMN+ " INTEGER, "
            + RINGTONE_COLUMN + " INTEGER "
            + ")";



    private static MySQLiteHelper instance;

    public static synchronized MySQLiteHelper getHelper(Context context) {
        if (instance == null)
            instance = new MySQLiteHelper(context);
        return instance;
    }

    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_HISTORIC_TABLE);
        db.execSQL(CREATE_CONTACT_WHITE_LIST_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CONTACT_HISTORIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CONTACT_WHITE_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SETTINGS_TABLE);
        onCreate(db);
    }

}

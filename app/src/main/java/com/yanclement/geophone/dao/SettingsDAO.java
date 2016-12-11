package com.yanclement.geophone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.yanclement.geophone.model.Settings;

/**
 * Created by YPierru on 02/12/2016.
 */

public class SettingsDAO extends DatabaseDAO {

    private static final String WHERE_ID_EQUALS = MySQLiteHelper.ID_COLUMN+ " =?";

    public SettingsDAO(Context context) {
        super(context);
    }

    public long saveSetting(Settings settings) {

        Cursor cursor =  database.rawQuery("SELECT count(*) from "+MySQLiteHelper.SETTINGS_TABLE_NAME +";",null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        if(count==0) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TEXT_ALERT_COLUMN, settings.getAlertText());
            values.put(MySQLiteHelper.FLASH_COLUMN, settings.getFlash());
            values.put(MySQLiteHelper.VIBRATE_COLUMN, settings.getVibrate());
            values.put(MySQLiteHelper.RINGTONE_COLUMN, settings.getRingtone());
            values.put(MySQLiteHelper.WAKEUP_ANONYMOUS_COLUMN, settings.getWakeupAnonymous());

            return database.insert(MySQLiteHelper.SETTINGS_TABLE_NAME, null, values);
        }

        return -1;

    }

    public long updateSettings(Settings settings) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TEXT_ALERT_COLUMN,settings.getAlertText());
        values.put(MySQLiteHelper.FLASH_COLUMN, settings.getFlash());
        values.put(MySQLiteHelper.VIBRATE_COLUMN, settings.getVibrate());
        values.put(MySQLiteHelper.RINGTONE_COLUMN, settings.getRingtone());
        values.put(MySQLiteHelper.WAKEUP_ANONYMOUS_COLUMN, settings.getWakeupAnonymous());


        long result = database.update(MySQLiteHelper.SETTINGS_TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(Settings.ID) });

        Log.d("Update Result:", "=" + result);
        return result;

    }

    public Settings getSettings() {

        Cursor cursor = database.query(MySQLiteHelper.SETTINGS_TABLE_NAME,
                new String[] { MySQLiteHelper.TEXT_ALERT_COLUMN,
                        MySQLiteHelper.FLASH_COLUMN,
                        MySQLiteHelper.VIBRATE_COLUMN,
                        MySQLiteHelper.RINGTONE_COLUMN,
                        MySQLiteHelper.WAKEUP_ANONYMOUS_COLUMN}, null, null, null, null,
                null);

        cursor.moveToFirst();
        return new Settings(cursor.getString(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4));

    }

}

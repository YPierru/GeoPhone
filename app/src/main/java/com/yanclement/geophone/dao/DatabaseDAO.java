package com.yanclement.geophone.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by YPierru on 02/12/2016.
 */

public class DatabaseDAO {

    protected SQLiteDatabase database;
    private MySQLiteHelper mySQLiteHelper;
    private Context mContext;

    public DatabaseDAO(Context context) {
        this.mContext = context;
        mySQLiteHelper = MySQLiteHelper.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(mySQLiteHelper == null)
            mySQLiteHelper = MySQLiteHelper.getHelper(mContext);
        database = mySQLiteHelper.getWritableDatabase();
    }

	public void close() {
        mySQLiteHelper.close();
		database = null;
	}
}

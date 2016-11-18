package com.yanclement.geophone;

import android.util.Log;

/**
 * Created by YPierru on 05/10/2016.
 */

public class Logger {

    private static boolean isLogOn;

    public static void logI(String toPrint){
        if(isLogOn) {
            Log.d(Constants.TAG_INFORMATION, toPrint);
        }
    }

    public static void enableLog(){
        isLogOn=true;
    }

    public static void disableLog(){
        isLogOn=false;
    }

}

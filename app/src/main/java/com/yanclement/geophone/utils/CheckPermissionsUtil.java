package com.yanclement.geophone.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by YPierru on 15/12/2016.
 */

public class CheckPermissionsUtil {

    /**
     * Function used to check if the application have the correct rights.
     * This function is used in IntroActivity and MainActivity
     * @param activity
     * @return
     */
    public static boolean checkPermissions(Activity activity){
        int permissionCheckReadContact= ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CONTACTS);
        int permissionCheckReceiveSMS = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECEIVE_SMS);
        int permissionCheckReadSMS = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_SMS);
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheckCamera = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
        boolean check=false;


        if( permissionCheckReadSMS== PackageManager.PERMISSION_GRANTED &&
                permissionCheckReceiveSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReadContact==PackageManager.PERMISSION_GRANTED &&
                permissionCheckFineLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCoarseLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCamera==PackageManager.PERMISSION_GRANTED){
            check=true;
        }else if(permissionCheckReadSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReceiveSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReadContact==PackageManager.PERMISSION_DENIED ||
                permissionCheckFineLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCoarseLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCamera==PackageManager.PERMISSION_DENIED){
            check=false;
        }

        return check;

    }

}

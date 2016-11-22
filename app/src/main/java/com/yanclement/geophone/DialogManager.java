package com.yanclement.geophone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by YPierru on 22/11/2016.
 */

public class DialogManager {

    /**
     * Show the AlertDialog displayed if user deny permissions
     * Exit the application or send the user to the settings
     */
    public static void permissionsKO(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_permission_title))
                .setMessage(activity.getResources().getString(R.string.dialog_permission_message))
                .setPositiveButton(activity.getResources().getString(R.string.dialog_permission_posbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        activity.getApplicationContext().startActivity(intent);
                        activity.finish();
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.dialog_permission_negbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Show the Alert dialog when the user enter a bad input for the phone/contact field
     * @param activity
     */
    public static void inputInvalid(Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.dialog_bad_input_title))
                .setMessage(activity.getResources().getString(R.string.dialog_permission_message))
                .setNeutralButton(activity.getResources().getString(R.string.dialog_bad_input_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

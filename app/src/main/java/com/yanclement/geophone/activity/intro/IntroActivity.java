package com.yanclement.geophone.activity.intro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yanclement.geophone.Constants;
import com.yanclement.geophone.R;
import com.yanclement.geophone.activity.MainActivity;
import com.yanclement.geophone.utils.CheckPermissionsUtil;
import com.yanclement.geophone.utils.DialogUtils;

/**
 * First view shown. Ask permissions from the user
 */
public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_to_use);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.statusbar_activity_intro));
        setSupportActionBar(toolbar);

        permissionManagement();
    }

    /**
     * At the starting of the app, check if needed permission are granted.
     * Ask for permission if one of them is not
     */
    private void permissionManagement(){

        if(CheckPermissionsUtil.checkPermissions(this)){
            startActivity(new Intent(IntroActivity.this,MainActivity.class));
            finish();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_ask_permissions_title))
                    .setMessage(getString(R.string.dialog_ask_permissions_message))
                    .setPositiveButton(getString(R.string.dialog_ask_permissions_pos_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(IntroActivity.this,new String[]{android.Manifest.permission.READ_SMS,
                                    android.Manifest.permission.RECEIVE_SMS,
                                    android.Manifest.permission.READ_CONTACTS,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.CAMERA}, Constants.ID_PERMISSION_REQUEST);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_ask_permissions_neg_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DialogUtils.permissionsKO(IntroActivity.this);
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .setIcon(R.drawable.ic_warning_black_24px)
                    .show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Constants.ID_PERMISSION_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(IntroActivity.this,MainActivity.class));
                    finish();
                } else {
                    DialogUtils.permissionsKO(this);
                }
                return;
            }
        }
    }

}

package com.yanclement.geophone.activity.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yanclement.geophone.Constants;
import com.yanclement.geophone.R;
import com.yanclement.geophone.activity.MainActivity;
import com.yanclement.geophone.utils.DialogUtils;

public class IntroToUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_to_use);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permissionManagement();
    }

    /**
     * At the starting of the app, check if needed permission are granted.
     * Ask for permission if one of them is not
     */
    private void permissionManagement(){
        int permissionCheckReadContact= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        int permissionCheckReceiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
        int permissionCheckReadSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheckCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if( permissionCheckReadSMS== PackageManager.PERMISSION_GRANTED &&
                permissionCheckReceiveSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReadContact==PackageManager.PERMISSION_GRANTED &&
                permissionCheckFineLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCoarseLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCamera==PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(IntroToUseActivity.this,MainActivity.class));
            finish();
        }else if(permissionCheckReadSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReceiveSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReadContact==PackageManager.PERMISSION_DENIED ||
                permissionCheckFineLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCoarseLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCamera==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.CAMERA}, Constants.ID_PERMISSION_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Constants.ID_PERMISSION_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(IntroToUseActivity.this,MainActivity.class));
                    finish();
                } else {
                    DialogUtils.permissionsKO(this);
                }
                return;
            }
        }
    }

}

package com.yanclement.geophone;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;


/**
 * Created by YPierru on 18/11/2016.
 */

public class ServiceListenerSMS extends Service {

    private SMSBroadcastReceiver smsReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Bundle bdl=intent.getExtras();

        String behavior = bdl.getString(Constants.SERVICE_BEHAVIOR);

        if(behavior.equals(Constants.SERVICE_START)){
            listenSMSReceiver();
        }else if(behavior.equals(Constants.SERVICE_STOP)){
            stopSelf();
        }

        return Service.START_NOT_STICKY;
    }

    private void listenSMSReceiver() {

        smsReceiver=new SMSBroadcastReceiver() {

            @Override
            protected void onNewSMS(String message, String phone) {
                Logger.logI("new SMS Received");
                Logger.logI("["+phone+"]->"+message);
            }
        };

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        this.registerReceiver(smsReceiver, intentFilter);
    }


    @Override
    public void onDestroy(){
        unregisterReceiver(this.smsReceiver);
        super.onDestroy();
    }
}

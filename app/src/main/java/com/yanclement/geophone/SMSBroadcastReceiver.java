package com.yanclement.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by YPierru on 18/11/2016.
 */

public abstract class SMSBroadcastReceiver extends BroadcastReceiver {

    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
    private String messageBody;
    private String phoneNumber;
    private Object[] pdus;
    private SmsMessage[] messages;

    @Override
    public void onReceive(Context context, Intent intent) {
        messageBody="";

        if (intent.getAction().equals(ACTION_RECEIVE_SMS)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                pdus= (Object[]) bundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    }else{
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                }

                if (messages.length > -1) {
                    for(int i=0;i<messages.length;i++){
                        messageBody+=messages[i].getMessageBody();
                    }
                    phoneNumber = messages[0].getDisplayOriginatingAddress().replace("+33", "0");
                    onNewSMS(messageBody, phoneNumber);
                }
            }
        }
    }

    protected abstract void onNewSMS(String sms, String phone);

}

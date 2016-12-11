package com.yanclement.geophone.model;

/**
 * Created by YPierru on 02/12/2016.
 */

public class Settings {

    private String alertText;
    /**
     * 1=true
     * 0=false
     * (sqlite does not support boolean)
     */
    private int wakeupAnonymous;
    private int flash;
    private int vibrate;
    private int ringtone;

    public static final int ID=1;


    public Settings(String alertText, int flash, int vibrate, int ringtone,int wakeupAnonymous) {
        this.alertText = alertText;
        this.flash = flash;
        this.vibrate = vibrate;
        this.ringtone = ringtone;
        this.wakeupAnonymous = wakeupAnonymous;
    }

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }

    public int getFlash() {
        return flash;
    }

    public void setFlash(int flash) {
        this.flash = flash;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public int getWakeupAnonymous() {
        return wakeupAnonymous;
    }

    public void setWakeupAnonymous(int wakeupAnonymous) {
        this.wakeupAnonymous = wakeupAnonymous;
    }
}

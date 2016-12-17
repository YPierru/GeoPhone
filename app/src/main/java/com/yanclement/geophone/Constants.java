package com.yanclement.geophone;

/**
 * Created by YPierru on 18/11/2016.
 */

public class Constants {

    public static final String TAG_INFORMATION = "GEOPHONE_DEBUG";

    public static final String SMS_CMD_TAG="[GeoPhone]";
    public static final String SMS_CMD_COO_REQUEST=SMS_CMD_TAG+"GPS_COO_RQ";
    public static final String SMS_CMD_DELIMITER="X__X";
    public static final String SMS_CMD_COO_GPS_RESPONSE="GPS_COO_RP";


    public static final String SEARCHED_PHONE_LOCATION="SEARCHED_PHONE_LOCATION";
    public static final String SEARCHED_PHONE_ID="SEARCHED_PHONE_ID";
    public static final String SEARCHED_PHONE_SETTINGS_ID="SETTINGS";

    /**
     * Permission request identifier
     */
    public static final int ID_PERMISSION_REQUEST=01;

    public static final int ID_DL_ITEM_MAIN_ACTIVITY=1;
    public static final int ID_DL_ITEM_CONTACT_ACTIVITY=2;
    public static final int ID_DL_ITEM_WAKEUP_ANONYMOUS=3;
    public static final int ID_DL_ITEM_ALERT_TEXT=4;
    public static final int ID_DL_ITEM_VIBRATOR=5;
    public static final int ID_DL_ITEM_FLASH=6;
    public static final int ID_DL_ITEM_RINGTONE=7;

    public static final String LABEL_UNKNOW_CONTACT="Inconnu";

}

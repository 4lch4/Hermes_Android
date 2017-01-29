package com.dleaman.hermes.models;

/**
 * Created by dleam on 1/28/2017.
 */

public class Constants {
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String[] PERMISSIONS = {
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.INTERNET"
    };

    public Constants() {

    }
}

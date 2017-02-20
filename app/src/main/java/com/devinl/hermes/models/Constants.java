package com.devinl.hermes.models;

/**
 * Created by Alcha on 1/28/2017.
 * Basic class for storing constants necessary for running the app. This makes it easier to keep
 * track of the various constants and strings used throughout the app.
 */
public class Constants {
    /**
     * Broadcast String for receiving text messages
     */
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * String Array containing the various permissions necessary to run the app.
     */
    public static final String[] PERMISSIONS = {
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.INTERNET"
    };

    /**
     * Server IP where Tron is hosted.
     */
    public static final String SERVER_IP = "198.199.117.243";

    public Constants() {

    }
}

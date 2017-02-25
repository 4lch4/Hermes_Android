package com.devinl.hermes.utils;

/**
 * Created by Alcha on 2/20/2017.
 */

public class Constants {
    public static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01233456789/.";

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

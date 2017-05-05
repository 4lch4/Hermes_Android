package com.devinl.hermes.utils;

/**
 * Created by Alcha on 2/20/2017.
 */

public class Constants {
    /**
     * List of available characters for a HermesDirect user token
     * Firebase Database paths must not contain '.', '#', '$', '[', or ']'
     */
    static final String CHAR_LIST = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789%@!=+";

    /** String Array containing the various permissions necessary to run the app. */
    public static final String[] PERMISSIONS = {
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.INTERNET"
    };

    /** Server IP where Hermes is hosted. */
    public static final String SERVER_IP = "198.199.117.243";

    /** Link to invite Hermes to your Discord server. */
    public static final String INVITE_LINK = "https://discordapp.com/api/oauth2/authorize?client_id=283454148257644545&scope=bot";

    public Constants() {

    }
}

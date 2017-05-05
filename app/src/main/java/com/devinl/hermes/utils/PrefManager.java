package com.devinl.hermes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.devinl.hermes.models.User;

import static com.devinl.hermes.utils.Constants.PERMISSIONS;

/**
 * Created by Alcha on 2/19/2017.
 */

public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "hermes";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_TOKEN = "UserToken";
    private static final String USER_CHANNEL_ID = "ChannelId";
    private static final String USER_PHONE_NUM = "PhoneNum";
    private static final String USER_ID = "UserId";
    private static final String USERNAME = "Username";
    private static final String FIREBASE_TOKEN = "FirebaseToken";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public PrefManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, 0);
        mEditor = mPref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return mPref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static boolean isFirstTimeLaunch(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        mEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        mEditor.commit();
    }

    public boolean isConfigNeeded() {
        return getUser() == null;
    }

    public User getUser() {
        User user = new User();

        user.setUserId(mPref.getLong(USER_ID, 0));
        user.setUsername(mPref.getString(USERNAME, ""));
        user.setUserToken(mPref.getString(USER_TOKEN, ""));
        user.setPhoneNum(mPref.getString(USER_PHONE_NUM, ""));
        user.setChannelId(mPref.getLong(USER_CHANNEL_ID, 0));

        return user;
    }

    public void setUser(User user) {
        mEditor.putString(USER_TOKEN, user.getUserToken());
        mEditor.putString(USERNAME, user.getUsername());
        mEditor.putString(USER_PHONE_NUM, user.getPhoneNum());
        mEditor.putLong(USER_CHANNEL_ID, user.getChannelId());
        mEditor.putLong(USER_ID, user.getUserId());

        mEditor.commit();
    }

    public void setUserToken(String token) {
        mEditor.putString(USER_TOKEN, token);
        mEditor.commit();
    }

    public String getUserToken() {
        return mPref.getString(USER_TOKEN, "");
    }

    void setFirebaseToken(String token) {
        mEditor.putString(FIREBASE_TOKEN, token);
        mEditor.commit();
    }

    public String getDeviceToken() {
        return mPref.getString(FIREBASE_TOKEN, "");
    }

    /**
     * If the device is using Android >= 23 and the app doesn't have the necessary permissions
     * granted, this method will query the device and ask the user for permission.
     * <p>
     * TODO: Account for a user not wanting to give permission for contacts
     */
    public static void checkPermissions(Activity activity) {
        boolean check = false;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : PERMISSIONS) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    check = true;
            }

            if (check)
                activity.requestPermissions(PERMISSIONS, 0);
        }
    }
}
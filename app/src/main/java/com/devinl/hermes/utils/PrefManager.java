package com.devinl.hermes.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alcha on 2/19/2017.
 */

public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "hermes-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_TOKEN = "UserToken";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    // shared mPref mode
    private int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return mPref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        mEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        mEditor.commit();
    }

    public boolean isConfigNeeded() {
        return getUserToken() == null;
    }

    public String getUserToken() {
        return mPref.getString(USER_TOKEN, null);
    }

    public void setUserToken(String userToken) {
        mEditor.putString(USER_TOKEN, userToken);
        mEditor.commit();
    }
}
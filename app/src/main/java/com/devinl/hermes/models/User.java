package com.devinl.hermes.models;

/**
 * Created by Alcha on 4/1/2017.
 */

public class User {
    private String mDeviceToken;
    private String mUserToken;
    private String mUsername;
    private String mPhoneNum;
    private long mChannelId;
    private long mUserId;

    public User() {
    }

    public User(String userToken){
        setUserToken(userToken);
    }

    public boolean setUserToken(String userToken) {
        if (userToken.length() == 10) {
            mUserToken = userToken;
            return true;
        } else {
            mUserToken = "";
            return false;
        }
    }

    public String getUserToken() {
        return mUserToken;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPhoneNum(String phoneNum) {
        mPhoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public boolean setChannelId(long channelId) {
        if (String.valueOf(channelId).length() == 18) {
            mChannelId = channelId;
            return true;
        } else {
            mChannelId = 0;
            return false;
        }
    }

    public long getChannelId() {
        return mChannelId;
    }

    public boolean setUserId(long userId) {
        if (String.valueOf(userId).length() == 18) {
            mUserId = userId;
            return true;
        } else
            return false;
    }

    public long getUserId() {
        return mUserId;
    }

    public String getDeviceToken() {
        return mDeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        mDeviceToken = deviceToken;
    }
}

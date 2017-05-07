package com.devinl.hermes.models;

import android.content.Context;

import com.devinl.hermes.utils.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Created by Alcha on 4/1/2017.
 */

public class User implements Serializable {
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

    public static User buildUserObject(DataSnapshot dataSnapshot, Context context) {
        PrefManager manager = new PrefManager(context);
        User user = new User();

        user.setUserToken(dataSnapshot.getKey());
        user.setUsername(dataSnapshot.child("username").getValue().toString());
        user.setPhoneNum(dataSnapshot.child("phoneNum").getValue().toString());
        user.setUserId(Long.parseLong(dataSnapshot.child("userId").getValue().toString()));
        user.setChannelId(Long.parseLong(dataSnapshot.child("channelId").getValue().toString()));

        if (dataSnapshot.child("deviceToken").getValue() == null) {
            if (manager.getDeviceToken().length() > 0)
                user.setDeviceToken(manager.getDeviceToken());
        } else
            user.setDeviceToken(dataSnapshot.child("deviceToken").getValue().toString());

        return user;
    }
}

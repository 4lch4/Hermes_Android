package com.devinl.hermes.utils;

import android.content.Context;

import com.devinl.hermes.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alcha on 4/6/2017.
 * Provides methods to access data stored in the Firebase Realtime Database that stores the various
 * users information.
 */

class DBManager {
    /** Basic log tag **/
    private static final String LOG_TAG = "DBManager";

    /** {@link DatabaseReference} for converting a Firebase device token into a HermesDirect token **/
    private DatabaseReference mUserIdToDevice = FirebaseDatabase.getInstance().getReference("userIdToDevice");

    /** {@link DatabaseReference} for HermesDirect users **/
    private DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference("users");

    /** {@link PrefManager} reference for pulling local user info **/
    private PrefManager mPrefs;

    DBManager(Context context) {
        mPrefs = new PrefManager(context);
    }

    void updateDeviceToken(String tokenIn) {
        // Get user from preferences
        User user = mPrefs.getUser();

        // Update userIdToDevice/userId
        if (user.getUserId() != 0)
            mUserIdToDevice.child(String.valueOf(user.getUserId())).setValue(tokenIn);

        // Update user/userToken/deviceToken
        if (user.getUserToken() != null)
            mUsers.child(user.getUserToken()).child("deviceToken").setValue(tokenIn);
    }

    void updateUser(User user) {
        if (user.getUserToken() != null)
            mUsers.child(user.getUserToken()).setValue(user);

        if (user.getUserId() != 0)
            mUserIdToDevice.child(String.valueOf(user.getUserId())).setValue(user.getDeviceToken());
    }
}

package com.devinl.hermes.services;

import com.devinl.hermes.utils.PrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.devinl.hermes.utils.KeyUtility.updateDeviceToken;

/**
 * Created by Alcha on 4/6/2017.
 * Responsible for intercepting any refreshes to the Firebase device token so it can be updated.
 */

public class FCMIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        updateDeviceToken(token, this);
    }
}

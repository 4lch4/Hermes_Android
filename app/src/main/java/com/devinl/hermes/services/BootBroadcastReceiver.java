package com.devinl.hermes.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Alcha on 1/29/2017.
 * Receives a broadcast when the device is booted and ensures the {@link HermesService} is running in
 * the background.
 */

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent hermesServiceIntent = new Intent(context, HermesService.class);
        startWakefulService(context, hermesServiceIntent);
    }
}

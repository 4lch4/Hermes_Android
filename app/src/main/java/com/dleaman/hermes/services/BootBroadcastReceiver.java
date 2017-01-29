package com.dleaman.hermes.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by dleam on 1/29/2017.
 * Receives a broadcast when the device is booted and ensures the {@link TronService} is running in
 * the background.
 */

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent tronServiceIntent = new Intent(context, TronService.class);
        startWakefulService(context, tronServiceIntent);
    }
}

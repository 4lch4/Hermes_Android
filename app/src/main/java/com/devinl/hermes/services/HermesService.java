package com.devinl.hermes.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.devinl.hermes.models.Message;
import com.devinl.hermes.models.ObservableObject;
import com.devinl.hermes.models.User;
import com.devinl.hermes.receivers.SmsReceiver;
import com.devinl.hermes.utils.PrefManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by Alcha on 1/29/2017.
 * <p>
 * {@link Service} responsible for handling the network communication that is performed by Discord
 * Direct. For example, if a user receives a text message on their device, the {@link SmsReceiver}
 * updates an {@link ObservableObject} with the message received so that this service can forward
 * the message to the Hermes server. If the user sends a message to a contact, it is sent to this
 * class and forwarded to the correct number.
 * </p>
 */

public class HermesService extends Service implements Observer {
    private static final String LOG_TAG = "HermesService";
    private User mUser;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("messages");

    /**
     * {@link SmsReceiver} that detects received text messages and forwards them to the
     * {@link HermesService}.
     */
    SmsReceiver mSmsReceiver;

    public HermesService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObservableObject.getInstance().addObserver(this);

        mUser = new PrefManager(this).getUser();

        // Initiate SmsReceiver using application context
        mSmsReceiver = new SmsReceiver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Disable BroadcastReceiver
        mSmsReceiver.disableBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Enable BroadcastReceiver
        mSmsReceiver.enableBroadcastReceiver();

        // Indicate WakefulIntent has been completed
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        // Return START_STICKY to ensure service stays running
        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        // Convert newly received SMSMessage to Message
        Message message = (Message) (o);

        message.setUserToken(mUser.getUserToken());

        mDatabase.child(mUser.getUserToken())
                .child(UUID.randomUUID().toString())
                .setValue(message);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
package com.devinl.hermes.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.util.Log;

import com.devinl.hermes.models.Message;
import com.devinl.hermes.models.ObservableObject;
import com.devinl.hermes.utils.PrefManager;
import com.devinl.hermes.utils.SmsReceiver;
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
 * the message to the Tron server. If the user sends a message to a contact, it is sent to this
 * class and forwarded to the correct number.
 * </p>
 */

public class TronService extends Service implements Observer {
    private static final String LOG_TAG = "TronService";

    /**
     * {@link SmsReceiver} that detects received text messages and forwards them to the
     * {@link TronService}.
     */
    SmsReceiver mSmsReceiver;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("messages");
    private String mUserToken;

    public TronService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObservableObject.getInstance().addObserver(this);

        mUserToken = new PrefManager(this).getUserToken();

        /** Initiate SmsReceiver using application context **/
        mSmsReceiver = new SmsReceiver(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /** Disable BroadcastReceiver **/
        mSmsReceiver.disableBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /** Enable BroadcastReceiver **/
        mSmsReceiver.enableBroadcastReceiver();

        /** Indicate WakefulIntent has been completed **/
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        /** Return START_STICKY to ensure service stays running **/
        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        /** Convert newly received SMSMessage to Message **/
        Message message = (Message) (o);
        message.setUserToken(mUserToken);

        mDatabase.child(mUserToken)
                .child(UUID.randomUUID().toString())
                .setValue(message);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

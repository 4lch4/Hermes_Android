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
        Message message = convertToMessage(o);

        mDatabase.child(mUserToken)
                .child(UUID.randomUUID().toString())
                .setValue(message);
    }

    private Message convertToMessage(Object o) {
        SmsMessage smsMessage = (SmsMessage) o;
        Message message = new Message();

        message.setUserToken(mUserToken);
        message.setFromNum(smsMessage.getOriginatingAddress());
        message.setFromName(getContactName(this, message.getFromNum()));
        message.setContent(smsMessage.getMessageBody());

        return message;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Using the given number, query the device to see if the number is saved as a contact and if so
     * return the contact name. If not, return the original number provided.
     *
     * @param context {@link Context}
     * @param number  {@link String}
     * @return {@link String}
     */
    private String getContactName(Context context, String number) {
        /** Execute the query **/
        Cursor cursor = executeContactProviderQuery(context, number);

        /** Verify Cursor aren't null **/
        if (cursor != null) {
            /** Get first matches name **/
            if (cursor.moveToFirst()) {
                /** Get column index containing contact name **/
                int columnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);

                /** Get name from cursor object **/
                String name = cursor.getString(columnIndex);

                /** Close cursor to avoid memory leak **/
                cursor.close();

                /** Return contact name **/
                return name;
            } else {
                cursor.close();
            }
        }

        /** Return contact number as no name was located **/
        return number;
    }

    /**
     * Using the provided number parameter, queries the {@link ContactsContract} object to see if
     * the number is saved as a contact. If so, returns the {@link Cursor} that contains the query
     * results.
     *
     * @param context {@link Context} Application context
     * @param number  {@link String} Phone number to query
     * @return {@link Cursor} containing query results
     */
    private Cursor executeContactProviderQuery(Context context, String number) {
        /** Define the columns for the query to return **/
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        /** Encode the phone number and build the filter URI **/
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        /** Perform query and return results **/
        return context.getContentResolver().query(contactUri, projection, null, null, null);
    }
}

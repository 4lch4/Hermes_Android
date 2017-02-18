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
import android.telephony.SmsManager;

import com.devinl.hermes.models.Client;
import com.devinl.hermes.models.ObservableObject;
import com.devinl.hermes.utils.SmsReceiver;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import static com.devinl.hermes.models.Constants.SERVER_IP;

/**
 * Created by dleam on 1/29/2017.
 * {@link Service} responsible for handling the network communication that is performed by Discord
 * Direct. For example, if a user receives a text message on their device, the {@link SmsReceiver}
 * updates an {@link ObservableObject} with the message received so that this service can forward
 * the message to the Tron server. If the user sends a message to a contact, it is sent to this
 * class and forwarded to the correct number.
 */

public class TronService extends Service implements Observer {
    /**
     * {@link SmsReceiver} that detects received text messages and forwards them to the
     * {@link TronService}.
     */
    SmsReceiver mSmsReceiver;
    /**
     * Temporarily set to always text my tablet.
     */
    private String mPrevNumber = "+17075604247";
    /**
     * {@link com.devinl.hermes.models.Client} object for network connections
     */
    private Client mPrimarySocket;

    public TronService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObservableObject.getInstance().addObserver(this);

        /** Initiate primary socket **/
        mPrimarySocket = new Client(SERVER_IP, 6969);

        /** Set ClientCallback **/
        mPrimarySocket.setClientCallback(buildClientCallback());

        /** Initiate SmsReceiver using application context **/
        mSmsReceiver = new SmsReceiver(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /** Close primary socket **/
        mPrimarySocket.disconnect();

        /** Disable BroadcastReceiver **/
        mSmsReceiver.disableBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /** Initiate connection with the primary socket **/
        mPrimarySocket.connect();

        /** Enable BroadcastReceiver **/
        mSmsReceiver.enableBroadcastReceiver();

        /** Indicate WakefulIntent has been completed **/
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        /** Return START_STICKY to ensure service stays running **/
        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        /** Convert received object to String **/
        String message = o.toString();

        /** If message contains the receiving number, if so, query for contact name **/
        if (message.startsWith("msgFrom = ")) {

            /** Set String to actual number without + sign **/
            String number = message.substring(message.indexOf("+"));

            /** Send contact name or number through the primary socket **/
            mPrimarySocket.send("Message From: " + getContactName(getApplicationContext(), number));
        } else if (message.startsWith("msgBody")) {

            /** Set message to the actual message content **/
            message = message.substring(9);

            /** Send message to user through the primary socket **/
            mPrimarySocket.send(message);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Build the {@link com.devinl.hermes.models.Client.ClientCallback} that when a text message is
     * received from the remote node server, will forward it to the correct contact.
     *
     * @return {@link com.devinl.hermes.models.Client.ClientCallback}
     */
    private Client.ClientCallback buildClientCallback() {
        return new Client.ClientCallback() {
            @Override
            public void onMessage(final String message) {
                if (message.startsWith("smsMsg")) {
                    String smsBody = message.substring(7);
                    System.out.println("smsBody = " + smsBody);
                    SmsManager manager = SmsManager.getDefault();
                    manager.sendTextMessage(mPrevNumber, "+19517078144", smsBody, null, null);
                }
                System.out.println("message = " + message);
            }

            @Override
            public void onConnect(Socket socket) {
                System.out.println("Connection successful.");
            }

            @Override
            public void onDisconnect(Socket socket, final String message) {
                System.out.println("message = " + message);
            }

            @Override
            public void onConnectError(Socket socket, final String message) {
                System.out.println("message = " + message);
            }
        };
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

package com.dleaman.hermes.services;

import android.app.IntentService;
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

import com.dleaman.hermes.models.Client;
import com.dleaman.hermes.models.Constants;
import com.dleaman.hermes.models.ObservableObject;
import com.dleaman.hermes.utils.SmsReceiver;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import static com.dleaman.hermes.models.Constants.SERVER_IP;

/**
 * Created by dleam on 1/29/2017.
 */

public class TronService extends Service implements Observer {
    private Client mPrimarySocket;
    private String mPrevNumber = "+17075604247";
    SmsReceiver mSmsReceiver;

    public TronService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObservableObject.getInstance().addObserver(this);

        mPrimarySocket = new Client(SERVER_IP, 6969);
        mPrimarySocket.setClientCallback(buildClientCallback());

        mSmsReceiver = new SmsReceiver(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrimarySocket.disconnect();
        mSmsReceiver.disableBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPrimarySocket.connect();
        mSmsReceiver.enableBroadcastReceiver();

        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        return START_STICKY;
    }

    @Override
    public void update(Observable observable, Object o) {
        String message = o.toString();

        if (message.startsWith("msgFrom = ")) {
            String number = message.substring(message.indexOf("+"));
            mPrimarySocket.send("Message From: " + getContactName(getApplicationContext(), number));
        } else if (message.startsWith("msgBody")) {
            message = message.substring(9);
            mPrimarySocket.send(message);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Client.ClientCallback buildClientCallback() {
        return new Client.ClientCallback() {
            @Override
            public void onMessage(final String message) {
                if(message.startsWith("smsMsg")) {
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

    private String getContactName(Context context, String number) {

        String name = number;

        // define the columns I want the query to return
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                System.out.println("name = " + name);
                System.out.println("number = " + number);
            } else {
                System.out.println("no contact found");
            }
            cursor.close();
        }
        return name;
    }
}

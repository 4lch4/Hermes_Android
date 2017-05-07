package com.devinl.hermes.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsManager;

import com.devinl.hermes.models.Message;
import com.devinl.hermes.models.ObservableObject;
import com.devinl.hermes.models.User;
import com.devinl.hermes.receivers.SmsReceiver;
import com.devinl.hermes.utils.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Observable;
import java.util.Observer;

import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
import static com.devinl.hermes.utils.KeyUtility.generateToken;

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
    private final IBinder mBinder = new LocalBinder();
    private SmsManager mSmsManager;
    private PrefManager mPrefs;
    private User mUser;

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

        mPrefs = new PrefManager(this);

        mUser = mPrefs.getUser();

        // Initiate SmsReceiver using application context
        mSmsReceiver = new SmsReceiver(this);

        // Initialize the SmsManager for sending text messages
        mSmsManager = SmsManager.getDefault();
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

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("157475292197@gcm.googleapis.com")
                .setMessageId(generateToken(25))
                .addData("messageFrom", message.getFromName())
                .addData("messageContent", message.getContent())
                .addData("userToken", mPrefs.getUserToken())
                .build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public HermesService getService() {
            // Return this instance of LocalService so clients can call public methods
            return HermesService.this;
        }
    }

    public User getUser() {
        if (mUser.getDeviceToken() == null)
            mUser.setDeviceToken(mPrefs.getDeviceToken());

        return mUser;
    }

    /**
     * Convert the toField in the provided {@link DataSnapshot} and return it as a {@link String}.
     * This is primarily for when a user provides a contact name instead of a direct number. This
     * method will find the contact and the mobile number associated with it if it exists.
     *
     * @param msgTo {@link DataSnapshot} containing msgTo info
     *
     * @return
     */
    private String convertToField(String msgTo) {
        if (Character.isDigit(msgTo.charAt(0)))
            return msgTo;
        else
            return getCNumber(msgTo);
    }

    /**
     * Largely constructed with the help of
     * <a href="http://www.geeks.gallery/how-to-get-contact-number-from-contactlist-in-android/">
     * this article</a>.<br/<br/>
     *
     * Using the name provided, searches the users contacts for a mobile phone number associated
     * with it and returns that. If none are found, it returns a blank String.
     *
     * @param nameIn Name of contact to search for
     *
     * @return contact mobile number or blank String if none found
     */
    public String getCNumber(String nameIn) {
        ContentResolver cr = this.getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String phoneNumber, name;
        int numberType;

        if (phones != null) {
            while (phones.moveToNext()) {
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                numberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                if (name.equals(nameIn) && numberType == TYPE_MOBILE)
                    return phoneNumber;
            }
            phones.close();
        }

        return "";
    }

    /**
     * Check to see if {@link HermesService} is currently running and return a boolean indicating the
     * answer.
     *
     * @return {@link Boolean}
     */
    public static boolean isHermesServiceOn(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (HermesService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}

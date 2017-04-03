package com.devinl.hermes.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

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
    private SmsManager mSmsManager;
    DatabaseReference mMessagesOut;
    DatabaseReference mMessagesIn;
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

        mUser = new PrefManager(this).getUser();

        // Initiate SmsReceiver using application context
        mSmsReceiver = new SmsReceiver(this);

        // Initialize the SmsManager for sending text messages
        mSmsManager = SmsManager.getDefault();

        // Initialize Firebase references
        mMessagesOut = FirebaseDatabase.getInstance().getReference("messagesOut/" + mUser.getUserToken());
        mMessagesIn = FirebaseDatabase.getInstance().getReference("messagesIn/" + mUser.getUserToken());

        // Add event listener for MessagesOut
        mMessagesOut.addChildEventListener(getMessageOutListener());
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

        mMessagesIn.child(UUID.randomUUID().toString()).setValue(message);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ChildEventListener getMessageOutListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = convertSnapshotToMessage(dataSnapshot);

                // Send the message to intended person
                sendTextMessage(message);

                // Delete it so it doesn't get sent again on reboot
                mMessagesOut.child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Send the provided {@link Message} object to the intended recipient over SMS. The only two
     * fields required in the {@link Message} are the toNum and content.
     *
     * @param message Contains the content you wish to send
     */
    private void sendTextMessage(Message message) {
        // Temporary until I can convert contact names into phone numbers
        if (message.getToNum().length() > 0) {
            mSmsManager.sendTextMessage(
                    message.getToNum(),
                    message.getFromNum(),
                    message.getContent(),
                    null,
                    null);
        }
    }

    /**
     * Convert the provided {@link DataSnapshot} into a {@link Message} and returns it. This is done
     * by pulling the msgTo and content children from the snapshot and storing them in the new
     * object.
     *
     * @param dataSnapshot Contains to field and message content
     *
     * @return Message object
     */
    private Message convertSnapshotToMessage(DataSnapshot dataSnapshot) {
        String msgTo = convertToField(dataSnapshot.child("msgTo"));
        String content = dataSnapshot.child("content").getValue().toString();

        Message message = new Message();
        message.setContent(content);
        message.setToNum(msgTo);
        message.setFromName("");
        message.setFromNum("");

        return message;
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
    private String convertToField(DataSnapshot msgTo) {
        String toField = msgTo.getValue().toString();
        if (Character.isDigit(toField.charAt(0)))
            return toField;
        else
            return getCNumber(toField);
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
}

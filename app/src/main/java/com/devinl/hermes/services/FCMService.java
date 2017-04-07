package com.devinl.hermes.services;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import com.devinl.hermes.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

/**
 * Created by Alcha on 4/6/2017.
 */

public class FCMService extends FirebaseMessagingService {
    private static final String LOG_TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Verify RemoteMessage contains data payload
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Message message = new Message();

            String msgTo = convertToField(data.get("toField"));
            String content = data.get("content");

            message.setToNum(msgTo);
            message.setContent(content);

            sendTextMessage(message);
        }
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
     * Send the provided {@link Message} object to the intended recipient over SMS. The only two
     * fields required in the {@link Message} are the toNum and content.
     *
     * @param message Contains the content you wish to send
     */
    private void sendTextMessage(Message message) {
        // Temporary until I can convert contact names into phone numbers
        if (message.getToNum().length() > 0) {
            SmsManager.getDefault().sendTextMessage(
                    message.getToNum(),
                    message.getFromNum(),
                    message.getContent(),
                    null,
                    null);
        }
    }
}

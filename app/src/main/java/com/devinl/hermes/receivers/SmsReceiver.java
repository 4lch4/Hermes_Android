package com.devinl.hermes.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.devinl.hermes.models.Message;
import com.devinl.hermes.models.ObservableObject;


/**
 * Created by Alcha on 1/28/2017.
 * The {@link BroadcastReceiver} responsible for forwarding sent and received text messages. If the
 * device receives a text message, it's forwarded to the appropriate instance of Hermes and then sent
 * to the correct user.
 */
public class SmsReceiver extends BroadcastReceiver {
    static final int NOTIFICATION = 69;
    private static final String LOG_TAG = "SmsReceiver";
    private PackageManager mPackageManager;
    private ComponentName mReceiver;
    private Context mContext;

    /**
     * Required to avoid random "Unable to instantiate receiver com.devinl.hermes.receivers.SmsReceiver"
     * errors that occur when the receiver is moved to the background by the service.
     */
    public SmsReceiver() {
    }

    public SmsReceiver(Context context) {
        mContext = context;
        mReceiver = new ComponentName(context, SmsReceiver.class);
        mPackageManager = context.getPackageManager();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    StringBuilder builder = new StringBuilder();

                    if (pdus != null) {
                        Message message = new Message();
                        msgs = new SmsMessage[pdus.length];
                        msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
                        message.setFromNum(msgs[0].getOriginatingAddress());
                        message.setFromName(getContactName(context, message.getFromNum()));

                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            builder.append(msgs[i].getMessageBody());
                        }

                        message.setContent(builder.toString());
                        ObservableObject.getInstance().updateValue(message);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error when retrieving text message.");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Enables the {@link BroadcastReceiver} so that it's able to receive text messages that are
     * sent to the device. Also displays a {@link Notification} in the notification bar that is not
     * dismissible until the receiver is disabled.
     */
    public void enableBroadcastReceiver() {
        mPackageManager.setComponentEnabledSetting(mReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Toast.makeText(mContext, "Forwarding enabled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Disables the {@link BroadcastReceiver} and removes the {@link Notification} from the
     * notification bar.
     */
    public void disableBroadcastReceiver() {
        mPackageManager.setComponentEnabledSetting(mReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Toast.makeText(mContext, "Forwarding disabled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Using the given number, query the device to see if the number is saved as a contact and if so
     * return the contact name. If not, return the original number provided.
     *
     * @param context {@link Context}
     * @param number  {@link String}
     *
     * @return {@link String}
     */
    private String getContactName(Context context, String number) {
        // Execute the query
        Cursor cursor = executeContactProviderQuery(context, number);

        // Verify Cursor aren't null
        if (cursor != null) {
            // Get first matches name
            if (cursor.moveToFirst()) {
                // Get column index containing contact name
                int columnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);

                // Get name from cursor object
                String name = cursor.getString(columnIndex);

                // Close cursor to avoid memory leak
                cursor.close();

                // Return contact name
                return name;
            } else {
                cursor.close();
            }
        }

        // Return contact number as no name was located
        return number;
    }

    /**
     * Using the provided number parameter, queries the {@link ContactsContract} object to see if
     * the number is saved as a contact. If so, returns the {@link Cursor} that contains the query
     * results.
     *
     * @param context {@link Context} Application context
     * @param number  {@link String} Phone number to query
     *
     * @return {@link Cursor} containing query results
     */
    private Cursor executeContactProviderQuery(Context context, String number) {
        // Define the columns for the query to return
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // Encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // Perform query and return results
        return context.getContentResolver().query(contactUri, projection, null, null, null);
    }
}

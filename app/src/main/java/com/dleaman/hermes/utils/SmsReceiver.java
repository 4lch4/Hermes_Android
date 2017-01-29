package com.dleaman.hermes.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.dleaman.hermes.R;
import com.dleaman.hermes.models.ObservableObject;

import static com.dleaman.hermes.models.Constants.SMS_RECEIVED;

/**
 * Created by dleam on 1/28/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private NotificationManager mNotificationManager;
    private PackageManager mPackageManager;
    static final int NOTIFICATION = 81237;
    private ComponentName mReceiver;
    private Context mContext;

    public SmsReceiver(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mReceiver = new ComponentName(context, SmsReceiver.class);
        mPackageManager = context.getPackageManager();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called.");

        if (intent.getAction().equalsIgnoreCase(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (pdus != null) {
                    smsMessages = new SmsMessage[pdus.length];
                    for (int i = 0; i < smsMessages.length; i++) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            String format = bundle.getString("format");
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }

                        String msgFrom = smsMessages[i].getOriginatingAddress();
                        ObservableObject.getInstance().updateValue("msgFrom = " + msgFrom);

                        String msgBody = smsMessages[i].getMessageBody();
                        ObservableObject.getInstance().updateValue("msgBody = " + msgBody);
                    }
                }
            }
        }
    }

    public void enableBroadcastReceiver() {
        mPackageManager.setComponentEnabledSetting(mReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Toast.makeText(mContext, "Enabled logging", Toast.LENGTH_SHORT).show();

        //Let us also show a notification
        Notification notification = buildNotification();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(NOTIFICATION, notification);
    }

    public void disableBroadcastReceiver() {
        mPackageManager.setComponentEnabledSetting(mReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Toast.makeText(mContext, "Disabled logging", Toast.LENGTH_SHORT).show();

        mNotificationManager.cancel(NOTIFICATION);
    }

    public Notification buildNotification() {
        return new Notification.Builder(mContext.getApplicationContext())
                .setContentTitle("SMS Logger Running")
                .setContentText("Status: Logging..")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }
}

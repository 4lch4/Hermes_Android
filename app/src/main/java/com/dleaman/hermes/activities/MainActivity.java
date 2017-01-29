package com.dleaman.hermes.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dleaman.hermes.R;
import com.dleaman.hermes.models.Client;
import com.dleaman.hermes.models.ObservableObject;
import com.dleaman.hermes.utils.SmsReceiver;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dleaman.hermes.models.Constants.PERMISSIONS;

public class MainActivity extends AppCompatActivity implements Observer {
    private Client mPrimarySocket;

    @BindView(R.id.startButton)
    ImageView mStartButton;
    @BindView(R.id.pauseButton)
    ImageView mPauseButton;
    SmsReceiver mSmsReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        mPrimarySocket.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrimarySocket = new Client("198.199.117.243", 6969);
        mPrimarySocket.setClientCallback(buildClientCallback());
        mPrimarySocket.connect();

        mSmsReceiver = new SmsReceiver(getApplicationContext());

        ObservableObject.getInstance().addObserver(this);

        initializeControls();
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

    private void initializeControls() {
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                requestPermissions(PERMISSIONS, 0);
            }
        }

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmsReceiver.enableBroadcastReceiver();
                mStartButton.setVisibility(GONE);
                mPauseButton.setVisibility(VISIBLE);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmsReceiver.disableBroadcastReceiver();
                mStartButton.setVisibility(VISIBLE);
                mPauseButton.setVisibility(GONE);
            }
        });
    }

    // TODO: Account for a user not wanting to give permission for contacts
    private boolean checkPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            for (String permission : PERMISSIONS) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }
        return false;
    }

    private Client.ClientCallback buildClientCallback() {
        return new Client.ClientCallback() {
            @Override
            public void onMessage(final String message) {
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
}

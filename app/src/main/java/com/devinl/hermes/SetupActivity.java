package com.devinl.hermes;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devinl.hermes.utils.PrefManager;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.devinl.hermes.models.Constants.PERMISSIONS;
import static com.devinl.hermes.utils.Constants.CHAR_LIST;

public class SetupActivity extends AppCompatActivity {
    @BindView(R.id.user_token_label) TextView mTokenLabel;
    //@BindView(R.id.btn_generate_token) Button mGenerate;
    private PrefManager mPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        mPrefManager = new PrefManager(this);

        /** Verify app has permissions to view contacts, etc **/
        checkPermissions();

        mTokenLabel.setText(generateUserToken());

        /*mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTokenLabel.setText(generateUserToken());
            }
        });*/
    }

    private String generateUserToken() {
        StringBuilder builder = new StringBuilder();
        final int stringLen = 10;

        for (int i = 0; i < stringLen; i++) {
            int num = getRandomNum();
            char ch = CHAR_LIST.charAt(num);
            builder.append(ch);
        }

        return builder.toString();
    }

    public int getRandomNum() {
        int randomInt;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    /**
     * If the device is using Android >= 23 and the app doesn't have the necessary permissions
     * granted, this method will query the device and ask the user for permission.
     * <p>
     * TODO: Account for a user not wanting to give permission for contacts
     */
    private void checkPermissions() {
        boolean check = false;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : PERMISSIONS) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    check = true;
            }

            if (check)
                requestPermissions(PERMISSIONS, 0);
        }
    }
}

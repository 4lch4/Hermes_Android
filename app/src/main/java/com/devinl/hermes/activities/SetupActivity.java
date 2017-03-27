package com.devinl.hermes.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devinl.hermes.R;
import com.devinl.hermes.activities.MainActivity;
import com.devinl.hermes.utils.PrefManager;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.devinl.hermes.utils.Constants.CHAR_LIST;
import static com.devinl.hermes.utils.Constants.PERMISSIONS;

public class SetupActivity extends AppCompatActivity {
    @BindView(R.id.user_token_label) TextView mTokenLabel;
    @BindView(R.id.btn_generate_token) Button mGenerate;
    @BindView(R.id.btn_complete_setup) Button mComplete;
    private PrefManager mPrefManager;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        mPrefManager = new PrefManager(this);

        /** Verify app has permissions to view contacts, etc **/
        checkPermissions();

        mToken = generateUserToken();

        mTokenLabel.setText(mToken);

        mGenerate.setOnClickListener(getGenerateClickListener());

        mComplete.setOnClickListener(getCompleteClickListener());
    }

    /**
     * Generate a {@link String} that represents a user token and return it. The token is 10
     * characters long and consists of the characters <code>a-z, A-Z, 0-9, /, and .</code>
     *
     * @return String
     */
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

    /**
     * Generate a random number and return a character based on that number using the Alphabet  in
     * {@link com.devinl.hermes.utils.Constants}.
     *
     * @return int value for <code>a-z, A-Z, 0-9, /, and .</code>
     */
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

    /**
     * Build and return the {@link android.view.View.OnClickListener} for the Generate Token Button.
     * Generates a new token for the user and sets the TokenLabelTextView text to the token.
     *
     * @return OnClickListener
     */
    public View.OnClickListener getGenerateClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToken = generateUserToken();
                mTokenLabel.setText(mToken);
            }
        };
    }

    /**
     * Build and return the {@link android.view.View.OnClickListener} for the Complete Setup Button.
     * Sets the USER_TOKEN in the SharedPreferences and then starts the {@link MainActivity}.
     *
     * @return OnClickListener
     */
    public View.OnClickListener getCompleteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrefManager.setUserToken(mToken);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        };
    }
}

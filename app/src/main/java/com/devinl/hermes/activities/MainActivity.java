package com.devinl.hermes.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.devinl.hermes.R;
import com.devinl.hermes.services.TronService;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.fonts.MaterialModule;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.devinl.hermes.models.Constants.PERMISSIONS;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.primaryBtn)
    Button mPrimaryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Bind ButterKnife **/
        ButterKnife.bind(this);

        /** Initialize Iconify **/
        Iconify.with(new MaterialModule());

        /** Verify app has permissions to view contacts, etc **/
        checkPermissions();

        /** Initialize view controls and service **/
        initializeControls();
    }

    /**
     * Initializes the controls used by the Activity, such as the mStartButton and mPauseButton.
     * Also detects if the {@link TronService} is currently running and hides the mStartButton if
     * so.
     */
    private void initializeControls() {
        if (isTronServiceOn()) {
            mPrimaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_pause_circle_outline));
            mPrimaryBtn.setOnClickListener(getPauseButtonListener());
        } else {
            mPrimaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_play_circle_outline));
            mPrimaryBtn.setOnClickListener(getStartButtonListener());
        }
    }

    // TODO: Account for a user not wanting to give permission for contacts

    /**
     * If the device is using Android >= 23 and the app doesn't have the necessary permissions
     * granted, this method will query the device and ask the user for permission.
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
     * Check to see if {@link TronService} is currently running and return a boolean indicating the
     * answer.
     *
     * @return {@link Boolean}
     */
    private boolean isTronServiceOn() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TronService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Build and return the {@link android.view.View.OnClickListener} for the mStartButton that
     * starts the service if it isn't currently running.
     *
     * @return {@link android.view.View.OnClickListener}
     */
    public View.OnClickListener getStartButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTronServiceOn())
                    startService(new Intent(MainActivity.this, TronService.class));

                initializeControls();
            }
        };
    }


    /**
     * Build and return the {@link android.view.View.OnClickListener} for the mPauseButton that
     * stops the service if it is running.
     *
     * @return {@link android.view.View.OnClickListener}
     */
    public View.OnClickListener getPauseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTronServiceOn())
                    stopService(new Intent(MainActivity.this, TronService.class));

                initializeControls();
            }
        };
    }
}

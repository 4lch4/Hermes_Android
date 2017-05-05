package com.devinl.hermes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.devinl.hermes.R;
import com.devinl.hermes.services.HermesService;
import com.devinl.hermes.utils.PrefManager;
import com.digits.sdk.android.Digits;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

import static com.devinl.hermes.utils.KeyUtility.getTwitterKey;
import static com.devinl.hermes.utils.KeyUtility.getTwitterSecret;
import static com.devinl.hermes.utils.PrefManager.checkPermissions;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getTwitterKey(this), getTwitterSecret(this));
        Fabric.with(this, new Crashlytics(), new Digits.Builder().build(), new TwitterCore(authConfig));

        if (new PrefManager(this).isFirstTimeLaunch()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        // Initialize view controls and service
        initializeControls();

        checkPermissions(this);
    }

    /**
     * Initializes the controls used by the Activity, such as the mStartButton and mPauseButton.
     * Also detects if the {@link HermesService} is currently running and hides the mStartButton if
     * so.
     */
    private void initializeControls() {
        Button primaryBtn = (Button) findViewById(R.id.primaryBtn);

        if (isHermesServiceOn()) {
            primaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_pause_circle_outline).color(Color.WHITE));
            primaryBtn.setOnClickListener(getPauseButtonListener());
        } else {
            primaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_play_circle_outline).color(Color.WHITE));
            primaryBtn.setOnClickListener(getStartButtonListener());
        }
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
                if (!isHermesServiceOn())
                    startService(new Intent(MainActivity.this, HermesService.class));

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
                if (isHermesServiceOn())
                    stopService(new Intent(MainActivity.this, HermesService.class));

                initializeControls();
            }
        };
    }
}

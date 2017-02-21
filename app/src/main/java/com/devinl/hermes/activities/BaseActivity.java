package com.devinl.hermes.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devinl.hermes.services.TronService;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

/**
 * Created by Alcha on 2/19/2017.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Initialize Iconify **/
        Iconify.with(new MaterialModule());
    }

    /**
     * Check to see if {@link TronService} is currently running and return a boolean indicating the
     * answer.
     *
     * @return {@link Boolean}
     */
    protected boolean isTronServiceOn() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TronService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}

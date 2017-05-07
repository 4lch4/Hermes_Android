package com.devinl.hermes.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.devinl.hermes.R;
import com.devinl.hermes.services.HermesService;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

/**
 * Created by Alcha on 2/19/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Iconify
        Iconify.with(new MaterialModule());
    }

    /**
     * Build and activates the Toolbar/Actionbar without a back button in the left corner.
     *
     * @param title        Title that you wish to be displayed in the toolbar.
     * @param backDisabled If you don't want a back button in the left corner, this should be true.
     *
     * @return Returns the Toolbar for use within the activity.
     */
    protected Toolbar activateToolbar(String title, boolean backDisabled) {
        if (backDisabled) {
            if (mToolbar == null) {
                mToolbar = (Toolbar) findViewById(R.id.app_bar);
                if (mToolbar != null) {
                    mToolbar.setTitle(title);
                    setSupportActionBar(mToolbar);
                }
            }
        }
        return mToolbar;
    }

    /**
     * Returns the currently set toolbar title if there is one.
     *
     * @return String
     */
    public String getToolbarTitle() {
        if (mToolbar != null && mToolbar.getTitle() != null)
            return mToolbar.getTitle().toString();
        else return "";
    }

    /**
     * Sets the title of the currently active Toolbar.
     *
     * @param title Title that you wish to be displayed in the toolbar.
     */
    public void setToolbarTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }
}

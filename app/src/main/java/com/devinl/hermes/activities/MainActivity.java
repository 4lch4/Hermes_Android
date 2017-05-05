package com.devinl.hermes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavView;
    private DrawerLayout mDrawer;
    private Button mPrimaryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getTwitterKey(this), getTwitterSecret(this));
        Fabric.with(this, new Crashlytics(), new Digits.Builder().withTheme(R.style.DigitsTheme).build(), new TwitterCore(authConfig));

        if (new PrefManager(this).isFirstTimeLaunch()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);
        initializeControls();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_data) {

        } else if (id == R.id.nav_settings) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeControls() {
        mPrimaryBtn = (Button) findViewById(R.id.primaryBtn);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view);

        Toolbar toolbar = activateToolbar("Hermes Direct", true);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        mNavView.getMenu().getItem(0).setChecked(true);

        if (isHermesServiceOn()) {
            mPrimaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_pause_circle_outline).color(Color.WHITE));
            mPrimaryBtn.setOnClickListener(getPauseButtonListener());
        } else {
            mPrimaryBtn.setBackground(new IconDrawable(this, MaterialIcons.md_play_circle_outline).color(Color.WHITE));
            mPrimaryBtn.setOnClickListener(getStartButtonListener());
        }

        MenuItem configTitle = mNavView.getMenu().findItem(R.id.nav_config_title);
        SpannableString s = new SpannableString(configTitle.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.WhiteTextStyle), 0, s.length(), 0);
        configTitle.setTitle(s);
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

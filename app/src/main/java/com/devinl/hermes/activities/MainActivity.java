package com.devinl.hermes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.devinl.hermes.R;
import com.devinl.hermes.fragments.HomeFragment;
import com.devinl.hermes.fragments.StoredDataFragment;
import com.devinl.hermes.models.User;
import com.devinl.hermes.utils.PrefManager;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

import static com.devinl.hermes.utils.KeyUtility.getTwitterKey;
import static com.devinl.hermes.utils.KeyUtility.getTwitterSecret;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavView;
    private DrawerLayout mDrawer;

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

        replacePlaceholder(new HomeFragment());
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replacePlaceholder(new HomeFragment());
        } else if (id == R.id.nav_data) {
            User user = new PrefManager(this).getUser();
                if (user != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_placeholder, StoredDataFragment.newInstance(user)).addToBackStack("StoredData");
                    ft.commit();
                } else
                    Toast.makeText(this, "Please create an account first.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replacePlaceholder(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();
    }

    private void initializeControls() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view);

        Toolbar toolbar = activateToolbar("Hermes Direct", true);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        mNavView.getMenu().getItem(0).setChecked(true);

        MenuItem configTitle = mNavView.getMenu().findItem(R.id.nav_config_title);
        SpannableString s = new SpannableString(configTitle.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.WhiteTextStyle), 0, s.length(), 0);
        configTitle.setTitle(s);
    }
}

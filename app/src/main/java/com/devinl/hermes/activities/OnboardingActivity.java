package com.devinl.hermes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devinl.hermes.R;
import com.devinl.hermes.adapters.SectionsPagerAdapter;
import com.devinl.hermes.utils.PrefManager;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.devinl.hermes.utils.KeyUtility.generateUserToken;
import static com.devinl.hermes.utils.KeyUtility.getTwitterKey;
import static com.devinl.hermes.utils.KeyUtility.getTwitterSecret;

public class OnboardingActivity extends BaseActivity {
    private static final String LOG_TAG = "OnboardingActivity";
    @BindView(R.id.layoutDots) LinearLayout mDotsLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.btn_next) Button mBtnNext;
    @BindView(R.id.btn_back) Button mBtnBack;
    private static final int ANIMATION_DURATION = 1000;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DigitsAuthButton mAuthButton;
    private TextView mCommandTemplate;
    private TextView mCommandDescription;
    private TextView mAuthDescription;
    private String mUserToken;
    private TextView[] mDots;
    private int[] mLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // TODO: Lock vertical orientation until I can fix the rotation crash

        initializeControls();

        addBottomDots(0);

        changeStatusBarColor();
    }

    private void initializeControls() {
        FirebaseApp.initializeApp(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getTwitterKey(this), getTwitterSecret(this));
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());

        // Initialize ButterKnife
        ButterKnife.bind(this);

        mLayouts = new int[]{
                R.layout.onboarding_slide_1,
                R.layout.onboarding_slide_2,
                R.layout.onboarding_slide_3
        };

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr = getItem(-1);
                mViewPager.setCurrentItem(curr);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr = getItem(+1);
                if (curr < mLayouts.length)
                    mViewPager.setCurrentItem(curr);
                else {
                    launchMainActivity();
                }
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, mLayouts);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(getOnPageChangeListener());
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void launchMainActivity() {
        new PrefManager(this).setFirstTimeLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void addBottomDots(int currentPage) {
        mDots = new TextView[mLayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive[currentPage]);
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                if (position > 0)
                    mBtnBack.setVisibility(View.VISIBLE);
                else
                    mBtnBack.setVisibility(View.GONE);

                if (position == 1) {
                    mUserToken = generateUserToken();

                    mAuthDescription = (TextView) mViewPager.findViewById(R.id.auth_description);
                    mAuthButton = (DigitsAuthButton) mViewPager.findViewById(R.id.auth_button);
                    mAuthButton.setBackground(getResources().getDrawable(R.drawable.btn_use_phone_number, null));
                    mAuthButton.setCallback(getAuthButtonCallback());

                    mCommandDescription = (TextView) mViewPager.findViewById(R.id.command_description);
                    mCommandTemplate = (TextView) mViewPager.findViewById(R.id.command_template);
                    mCommandTemplate.setText("h!initiate " + mUserToken);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };
    }

    /**
     * Begins the second step of the user authorization process.
     */
    private void initializeSecondAuthStep() {
        mAuthButton.setEnabled(false);
        mAuthButton.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        mAuthDescription.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        mCommandTemplate.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        mCommandDescription.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();

        mViewPager.findViewById(R.id.slide_2_title).animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        mViewPager.findViewById(R.id.command_initiate_success).animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        mViewPager.findViewById(R.id.command_failure_description).animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        mViewPager.findViewById(R.id.command_initiate_failure).animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
    }

    /**
     * Build and return the {@link AuthCallback} necessary for retrieving the authentication status
     * of the users phone number. If it's successful, begin the next step, if not, alert user.
     *
     * @return AuthCallback
     */
    private AuthCallback getAuthButtonCallback() {
        return new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference("users");
                myDatabase.child(mUserToken).child("phoneNum").setValue(phoneNumber);
                myDatabase.child(mUserToken).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("username").exists())
                            mBtnNext.performClick();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                initializeSecondAuthStep();
            }

            @Override
            public void failure(DigitsException exception) {
                Toast.makeText(OnboardingActivity.this, "Unfortunately, authentication was unsuccessful. Please contact the developer.", Toast.LENGTH_SHORT).show();
                Log.e("Digits", "Sign in with Digits failure", exception);
            }
        };
    }
}

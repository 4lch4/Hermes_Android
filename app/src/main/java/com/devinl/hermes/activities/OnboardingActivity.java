package com.devinl.hermes.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
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
import com.devinl.hermes.models.User;
import com.devinl.hermes.utils.PrefManager;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.devinl.hermes.models.User.buildUserObject;
import static com.devinl.hermes.utils.Constants.INVITE_LINK;
import static com.devinl.hermes.utils.KeyUtility.generateToken;
import static com.devinl.hermes.utils.KeyUtility.updateUser;
import static com.devinl.hermes.utils.PrefManager.checkPermissions;

public class OnboardingActivity extends BaseActivity {
    private static final String LOG_TAG = "OnboardingActivity";
    private static final int ANIMATION_DURATION = 1000;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private boolean mAuthenticated = false;
    private boolean mSynchronized = false;
    private DigitsAuthButton mAuthButton;
    private TextView mCommandDescription;
    private TextView mAuthDescription;
    private TextView mCommandTemplate;
    private LinearLayout mDotsLayout;
    private ViewPager mViewPager;
    private PrefManager mPref;
    private String mUserToken;
    private TextView[] mDots;
    private Button mBtnNext;
    private Button mBtnBack;
    private int[] mLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        FirebaseApp.initializeApp(this);

        // Lock screen in portrait until I can fix the rotation crash
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeControls();
        setControlListeners();

        addBottomDots(0);

        changeStatusBarColor();
    }

    private void initializeControls() {
        mDotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnBack = (Button) findViewById(R.id.btn_back);

        mPref = new PrefManager(this);
        mUserToken = mPref.getUserToken();

        if (mUserToken.length() == 0) {
            mUserToken = generateToken(10);
            mPref.setUserToken(mUserToken);
        }

        mLayouts = new int[]{
                R.layout.onboarding_slide_1,
                R.layout.onboarding_slide_2,
                R.layout.onboarding_slide_3
        };

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, mLayouts);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * Add the listeners to the various controls in the Activity.<br/><br/>
     *
     * At time of writing this, for example, I set the next and back {@link Button}'s
     * {@link android.view.View.OnClickListener OnClickListener} as well as the {@link ViewPager}'s
     * {@link android.support.v4.view.ViewPager.OnPageChangeListener OnPageChangeListener}.
     */
    private void setControlListeners() {
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

                if (curr < mLayouts.length) {
                    if (curr == 2 && !mAuthenticated) {
                        Toast.makeText(OnboardingActivity.this, "Please activate with your cell phone before continuing.", Toast.LENGTH_SHORT).show();
                    } else {
                        mViewPager.setCurrentItem(curr);
                    }
                } else if (mSynchronized) {
                    launchMainActivity();
                } else {
                    Toast.makeText(OnboardingActivity.this, "Please make sure to synchronize your account before continuing on.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewPager.addOnPageChangeListener(getOnPageChangeListener());
    }

    /** Makes the notification bar transparent if on API 19+. */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Launches the {@link MainActivity} as well as sets the FirstTimeLaunch flag to false and
     * returns the screen orientation to the device sensor.
     */
    private void launchMainActivity() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        new PrefManager(this).setFirstTimeLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Add the bottom dots to the bottom of the current page based on the page loaded. This ensures
     * the correct dot is highlighted for each page loaded.
     *
     * @param currentPage
     */
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

    /**
     * Build the {@link android.support.v4.view.ViewPager.OnPageChangeListener} responsible for
     * determining which page is selected. For example, verifies the user has synchronized their
     * account before allowing them to continue or set the authentication token in the command
     * sample on page 2.
     *
     * @return {@link android.support.v4.view.ViewPager.OnPageChangeListener}
     */
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
                    mAuthDescription = (TextView) mViewPager.findViewById(R.id.auth_description);
                    mAuthButton = (DigitsAuthButton) mViewPager.findViewById(R.id.auth_button);
                    mAuthButton.setBackground(getResources().getDrawable(R.drawable.btn_use_phone_number, null));
                    mAuthButton.setCallback(getAuthButtonCallback());

                    Button inviteButton = (Button) mViewPager.findViewById(R.id.btn_invite_hermes);
                    inviteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(INVITE_LINK));
                            startActivity(browserIntent);
                        }
                    });
                } else if (position == 2) {
                    mCommandDescription = (TextView) mViewPager.findViewById(R.id.command_description);
                    mCommandTemplate = (TextView) mViewPager.findViewById(R.id.command_template);
                    mCommandTemplate.setText("h!sync " + mUserToken);
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
        mAuthenticated = true;
        mAuthButton.setEnabled(false);

        // Fade out step 1
        mAuthButton.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        mAuthDescription.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();

        // Fade in step 2
        mViewPager.findViewById(R.id.btn_invite_hermes).setEnabled(true);
        mViewPager.findViewById(R.id.btn_invite_hermes).animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        mViewPager.findViewById(R.id.slide_2_invite_hermes).animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
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
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("users");
                userDb.child(mUserToken).child("phoneNum").setValue(phoneNumber);
                userDb.child(mUserToken).addValueEventListener(getSyncListener());

                initializeSecondAuthStep();
            }

            @Override
            public void failure(DigitsException exception) {
                Toast.makeText(OnboardingActivity.this, "Unfortunately, authentication was unsuccessful. Please contact the developer.", Toast.LENGTH_SHORT).show();
                Log.e("Digits", "Sign in with Digits failure", exception);
            }
        };
    }

    /**
     * Build the {@link ValueEventListener} responsible for flipping the page to the next slide when
     * the user successfully synchronizes their Discord account with the database.
     *
     * @return {@link ValueEventListener}
     */
    public ValueEventListener getSyncListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("username").exists()) {
                    checkPermissions(OnboardingActivity.this);

                    // Indicate user is synchronized
                    mSynchronized = true;

                    User user = buildUserObject(dataSnapshot, OnboardingActivity.this);

                    updateUser(user, OnboardingActivity.this);

                    // Alert user they can go on
                    Toast.makeText(OnboardingActivity.this, "Synchronize successful, you may move on to the next step!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}

package com.devinl.hermes.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devinl.hermes.R;
import com.devinl.hermes.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingActivity extends AppCompatActivity {
    private static final String LOG_TAG = "OnboardingActivity";
    @BindView(R.id.layoutDots) LinearLayout mDotsLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.btn_next) Button mBtnNext;
    @BindView(R.id.btn_skip) Button mBtnSkip;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private PrefManager mPrefManager;
    private TextView[] mDots;
    private int[] mLayouts;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == mLayouts.length - 1) {
                // last page. make button text to GOT IT
                mBtnNext.setText(getString(R.string.onboarding_finish));
                mBtnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                mBtnNext.setText(getString(R.string.onboarding_next_slide));
                mBtnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        mPrefManager = new PrefManager(this);

        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter();

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(viewPagerPageChangeListener);

        mLayouts = new int[]{
                R.layout.onboarding_slide_1,
                R.layout.onboarding_slide_2,
                R.layout.onboarding_slide_3
        };

        addBottomDots(0);

        changeStatusBarColor();

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMainActivity();
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
        mPrefManager.setFirstTimeLaunch(false);
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

    public class SectionsPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        SectionsPagerAdapter() {
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(mLayouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.onboarding_slide_1_title);
                case 1:
                    return getResources().getString(R.string.onboarding_slide_2_title);
                case 2:
                    return getResources().getString(R.string.onboarding_slide_3_title);
            }
            return null;
        }
    }
}

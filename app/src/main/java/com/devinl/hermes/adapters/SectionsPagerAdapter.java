package com.devinl.hermes.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devinl.hermes.R;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

/**
 * Created by Alcha on 3/25/2017.
 */

public class SectionsPagerAdapter extends PagerAdapter {
    private Context mContext;
    private int[] mLayouts;

    public SectionsPagerAdapter(Context context, int[] layouts) {
        mLayouts = layouts;
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(mLayouts[position], container, false);

        if (position == 1) {
            DigitsAuthButton digitsButton = (DigitsAuthButton) view.findViewById(R.id.auth_button);
            digitsButton.setCallback(new AuthCallback() {
                @Override
                public void success(DigitsSession session, String phoneNumber) {
                    // TODO: associate the session userID with your user model
                    hideFirstStep();
                    Toast.makeText(mContext.getApplicationContext(), "Authentication successful for "
                            + phoneNumber, Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(DigitsException exception) {
                    Log.d("Digits", "Sign in with Digits failure", exception);
                }
            });
        }
        container.addView(view);

        return view;
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
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.onboarding_slide_1_title);
            case 1:
                return mContext.getResources().getString(R.string.onboarding_slide_2_title);
            case 2:
                return mContext.getResources().getString(R.string.onboarding_slide_3_title);
        }
        return null;
    }

    private void hideFirstStep() {

    }

}

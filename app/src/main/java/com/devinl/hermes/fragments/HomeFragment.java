package com.devinl.hermes.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devinl.hermes.R;
import com.devinl.hermes.services.HermesService;

/**
 * A simple {@link Fragment} subclass for the Home fragment.
 */
public class HomeFragment extends Fragment {
    private Button mPrimaryBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        mPrimaryBtn = (Button) rootView.findViewById(R.id.primaryBtn);
        updateButton();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Update the {@link Button} that starts/stops the {@link HermesService}. If the service is
     * running, set the background to the pause icon and use the {@link #getPauseButtonListener()}.
     * If the service isn't running, sets the background to the play icon and use the
     * {@link #getStartButtonListener()}.
     */
    private void updateButton() {
        if (isHermesServiceOn(getContext())) {
            mPrimaryBtn.setText(getText(R.string.pause_button_desc));
            mPrimaryBtn.setOnClickListener(getPauseButtonListener());
        } else {
            mPrimaryBtn.setText(getText(R.string.start_button_desc));
            mPrimaryBtn.setOnClickListener(getStartButtonListener());
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
                if (!isHermesServiceOn(getContext()))
                    getActivity().startService(new Intent(getContext(), HermesService.class));

                updateButton();
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
                if (isHermesServiceOn(getContext()))
                    getActivity().stopService(new Intent(getContext(), HermesService.class));

                updateButton();
            }
        };
    }

    /**
     * Check to see if {@link HermesService} is currently running and return a boolean indicating the
     * answer.
     *
     * @return {@link Boolean}
     */
    public static boolean isHermesServiceOn(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (HermesService.class.getName().equals(service.service.getClassName()))
                return true;
        }

        return false;
    }
}

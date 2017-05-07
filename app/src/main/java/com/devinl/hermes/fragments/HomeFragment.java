package com.devinl.hermes.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devinl.hermes.R;
import com.devinl.hermes.activities.MainActivity;
import com.devinl.hermes.services.HermesService;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import static com.devinl.hermes.services.HermesService.isHermesServiceOn;

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
            mPrimaryBtn.setBackground(new IconDrawable(getContext(), MaterialIcons.md_pause_circle_outline).color(Color.WHITE));
            mPrimaryBtn.setOnClickListener(getPauseButtonListener());
        } else {
            mPrimaryBtn.setBackground(new IconDrawable(getContext(), MaterialIcons.md_play_circle_outline).color(Color.WHITE));
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
}

package com.devinl.hermes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devinl.hermes.R;
import com.devinl.hermes.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoredDataFragment extends Fragment {
    private User mUser;

    public StoredDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoredDataFragment newInstance(User user) {
        StoredDataFragment fragment = new StoredDataFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stored_data, container, false);

        TextView username = (TextView) rootView.findViewById(R.id.text_view_username);
        username.setText(mUser.getUsername());

        TextView userId = (TextView) rootView.findViewById(R.id.text_view_user_id);
        userId.setText(String.valueOf(mUser.getUserId()));

        TextView userToken = (TextView) rootView.findViewById(R.id.text_view_user_token);
        userToken.setText(mUser.getUserToken());

        TextView channelId = (TextView) rootView.findViewById(R.id.text_view_channel_id);
        channelId.setText(String.valueOf(mUser.getChannelId()));

        TextView phoneNumb = (TextView) rootView.findViewById(R.id.text_view_phone_num);
        phoneNumb.setText(mUser.getPhoneNum());

        TextView deviceToken = (TextView) rootView.findViewById(R.id.text_view_device_token);
        deviceToken.setText(mUser.getDeviceToken());

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
}

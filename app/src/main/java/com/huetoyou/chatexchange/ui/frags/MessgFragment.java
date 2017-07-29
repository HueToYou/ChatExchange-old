package com.huetoyou.chatexchange.ui.frags;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.huetoyou.chatexchange.R;

public class MessgFragment extends Fragment
{
    private static final String MESSG_BODY_KEY = "param1";
    private static final String MESSG_USERNAME_KEY = "param2";

    private String usernameText;
    private String bodyText;

    public MessgFragment()
    {
        // Required empty public constructor
    }

    public static MessgFragment newInstance(String param1, String param2)
    {
        MessgFragment fragment = new MessgFragment();
        Bundle args = new Bundle();
        args.putString(MESSG_USERNAME_KEY, param1);
        args.putString(MESSG_BODY_KEY, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            usernameText = getArguments().getString(MESSG_USERNAME_KEY);
            bodyText = getArguments().getString(MESSG_BODY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.message_view, container, false);
        TextView messgBody = mView.findViewById(R.id.message_body);
        TextView username = mView.findViewById(R.id.messg_username);
        messgBody.setText(bodyText);
        username.setText(usernameText);
        return mView;

    }
}
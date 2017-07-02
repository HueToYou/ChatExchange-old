package com.huetoyou.chatexchange.ui.frags;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

public class UsernameTilePingFragment extends Fragment
{
    private View mView;
    private Bundle mArgs;
    private TextView usernameTextView;
    private ImageView userAvatar;
    private UserTileFragment mUserTileFragment;
    private String mUsername;
    private ChatFragment.SetTabCompleteName mSetTabCompleteName;

//    public UsernameTilePingFragment(UserTileFragment fragment, ChatFragment.SetTabCompleteName setTabCompleteName) {
//        mUserTileFragment = fragment;
//        mSetTabCompleteName = setTabCompleteName;
//    }

    public static UsernameTilePingFragment newInstance(UserTileFragment fragment, ChatFragment.SetTabCompleteName setTabCompleteName)
    {
        UsernameTilePingFragment pingFragment = new UsernameTilePingFragment();
        pingFragment.mUserTileFragment = fragment;
        pingFragment.mSetTabCompleteName = setTabCompleteName;
        return pingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.username_tile_for_ping_suggestions, container, false);
        usernameTextView = mView.findViewById(R.id.username_tile_ping_suggestion_name);
        userAvatar = mView.findViewById(R.id.username_tile_ping_suggestion_avatar);
        mArgs = getArguments();

        mUsername = mArgs.getString(ChatFragment.USER_NAME_KEY, "HueToYou");

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());

        Bitmap bmp = mUserTileFragment.getmIconBitmap();
        if (bmp != null)
            userAvatar.setImageDrawable(new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true)));

        if (usernameTextView != null)
        {
            usernameTextView.setText(mUsername);
        }

        mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSetTabCompleteName.setName(UsernameTilePingFragment.this);
            }
        });

        return mView;
    }

    public String getmUsername()
    {
        return mUsername;
    }

}

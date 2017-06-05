package com.huetoyou.chatexchange.ui.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    public static final String USER_NAME_KEY = "userName";
    public static final String USER_AVATAR_URL_KEY = "userAvatarUrl";
    public static final String USER_URL_KEY = "chatUrl";
    public static final String USER_ID_KEY = "id";
    public static final String USER_LAST_POST_KEY = "lastPost";
    public static final String USER_REP_KEY = "rep";
    public static final String USER_IS_MOD_KEY = "isMod";
    public static final String USER_IS_OWNER_KEY = "isOwner";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.username_tile_for_ping_suggestions, container, false);
        usernameTextView = (TextView) mView.findViewById(R.id.username_tile_ping_suggestion_name);
        userAvatar = (ImageView)  mView.findViewById(R.id.username_tile_ping_suggestion_avatar);
        mArgs = getArguments();

        final String username = mArgs.getString(USER_NAME_KEY, "HueToYou");
        final String avatar_url = mArgs.getString(USER_AVATAR_URL_KEY, "");

        if(usernameTextView != null)
        {
            usernameTextView.setText(username);
        }


        return mView;
    }

}

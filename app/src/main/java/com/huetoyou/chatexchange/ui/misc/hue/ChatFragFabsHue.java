package com.huetoyou.chatexchange.ui.misc.hue;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.huetoyou.chatexchange.R;

public class ChatFragFabsHue
{
    /*
     * This class is for setting the color of the FABs in a chat fragment {??except the show chats fab}
     */

    private SharedPreferences mSharedPreferences = null;

    public void setChatFragmentFabColor(AppCompatActivity activity, @ColorInt int appBarColor)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);
        tints(colorStateList, activity);
    }

    public void setChatFragmentFabColorToSharedPrefsValue(AppCompatActivity activity)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            int initialColor = mSharedPreferences.getInt("fab_color", activity.getResources().getColor(R.color.colorAccent));
            ColorStateList colorStateList = ColorStateList.valueOf(initialColor);
            tints(colorStateList, activity);
        }
    }

    private void tints(ColorStateList colorStateList, AppCompatActivity activity)
    {
        FloatingActionButton closeChat = activity.findViewById(R.id.close_chat_frag);
        FloatingActionButton openBrowser = activity.findViewById(R.id.open_in_browser_fab);
        FloatingActionButton showUsers = activity.findViewById(R.id.show_users_fab);
        FloatingActionButton roomInfo = activity.findViewById(R.id.room_info_fab);
        FloatingActionButton stars = activity.findViewById(R.id.star_fab);
        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);

        if (closeChat != null)
        {
            closeChat.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            closeChat.setBackgroundTintList(colorStateList);
        }

        if (openBrowser != null)
        {
            openBrowser.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            openBrowser.setBackgroundTintList(colorStateList);
        }

        if (showUsers != null)
        {
            showUsers.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            showUsers.setBackgroundTintList(colorStateList);
        }

        if (roomInfo != null)
        {
            roomInfo.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            roomInfo.setBackgroundTintList(colorStateList);
        }

        if (stars != null)
        {
            stars.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            stars.setBackgroundTintList(colorStateList);
        }

        showChatsTint(colorStateList, activity);
    }

    public void showChatsTint(ColorStateList colorStateList, AppCompatActivity activity)
    {
        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);
        if (showChats != null)
        {
            showChats.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            showChats.setBackgroundTintList(colorStateList);
        }
    }
}

package com.huetoyou.chatexchange.ui.misc.hue;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;

/*
 * This class is for setting the color of the FABs in a chat fragment {??except the show chats fab}
 */

public class ChatFragFabsHue
{
    private SharedPreferences mSharedPreferences = null;

    public void setChatFragmentFabColor(AppCompatActivity activity, @ColorInt int appBarColor)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);
        tints(colorStateList, activity);
    }

    public void setChatFragmentFabColorToSharedPrefsValue(AppCompatActivity activity)
    {
        //Grab an instance of SharedPrefs if we haven't already
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
//        FloatingActionButton closeChat = activity.findViewById(R.id.close_chat_frag);
//        FloatingActionButton openBrowser = activity.findViewById(R.id.open_in_browser_fab);
        com.github.clans.fab.FloatingActionButton showUsers = activity.findViewById(R.id.show_users_fab);
        com.github.clans.fab.FloatingActionButton roomInfo = activity.findViewById(R.id.room_info_fab);
        com.github.clans.fab.FloatingActionButton stars = activity.findViewById(R.id.star_fab);
        FloatingActionMenu menu = activity.findViewById(R.id.chat_menu);
//        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);

//        if (closeChat != null)
//        {
//            closeChat.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            closeChat.setBackgroundTintList(colorStateList);
//        }
//
//        if (openBrowser != null)
//        {
//            openBrowser.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            openBrowser.setBackgroundTintList(colorStateList);
//        }

        if (menu != null)
        {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_unfold_menu, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            menu.getMenuIconView().setImageDrawable(vectorDrawableCompat);
        }

        if (showUsers != null)
        {
//            showUsers.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            showUsers.setBackgroundTintList(colorStateList);
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_supervisor_account_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            showUsers.setColorNormal(colorStateList.getDefaultColor());
            showUsers.setColorPressed(colorStateList.getDefaultColor());
            showUsers.setImageDrawable(vectorDrawableCompat);
        }

        if (roomInfo != null)
        {
//            roomInfo.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            roomInfo.setBackgroundTintList(colorStateList);

            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_info_outline_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            roomInfo.setColorNormal(colorStateList.getDefaultColor());
            roomInfo.setColorPressed(colorStateList.getDefaultColor());
            roomInfo.setImageDrawable(vectorDrawableCompat);
        }

        if (stars != null)
        {
//            stars.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            stars.setBackgroundTintList(colorStateList);

            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_star_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            stars.setColorNormal(colorStateList.getDefaultColor());
            stars.setColorPressed(colorStateList.getDefaultColor());
            stars.setImageDrawable(vectorDrawableCompat);
        }

        if (menu != null)
        {
            menu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
            menu.setMenuButtonColorPressed(colorStateList.getDefaultColor());
        }

//        showChatsTint(colorStateList, activity);
    }

    private void showChatsTint(ColorStateList colorStateList, AppCompatActivity activity)
    {
//        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);
//        if (showChats != null)
//        {
//            showChats.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
//            showChats.setBackgroundTintList(colorStateList);
//        }
    }
}

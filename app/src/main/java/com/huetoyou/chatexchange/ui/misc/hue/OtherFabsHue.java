package com.huetoyou.chatexchange.ui.misc.hue;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;

public class OtherFabsHue
{
    /*
     * This class is for setting the color of the show chats FAB and the add new chat FAB
     */

    private SharedPreferences mSharedPreferences = null;

    public void setAddChatFabColor(AppCompatActivity activity, @ColorInt int appBarColor)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);

        FloatingActionMenu chatListMenu = activity.findViewById(R.id.chat_slide_menu);
        com.github.clans.fab.FloatingActionButton home = activity.findViewById(R.id.home_fab);
        com.github.clans.fab.FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);

        if (chatListMenu != null) {
            chatListMenu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
            chatListMenu.setMenuButtonColorPressed(colorStateList.getDefaultColor());
        }

        if (home != null) {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_home_white_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            home.setColorNormal(colorStateList.getDefaultColor());
            home.setColorPressed(colorStateList.getDefaultColor());
            home.setImageDrawable(vectorDrawableCompat);
        }

        if (addChat != null) {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_add_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            addChat.setColorNormal(colorStateList.getDefaultColor());
            addChat.setColorPressed(colorStateList.getDefaultColor());
            addChat.setImageDrawable(vectorDrawableCompat);
        }
    }

    public void setAddChatFabColorToSharedPrefsValue(AppCompatActivity activity)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            int hue = mSharedPreferences.getInt("fab_color", activity.getResources().getColor(R.color.colorAccent));

            ColorStateList colorStateList = ColorStateList.valueOf(hue);

            FloatingActionMenu chatListMenu = activity.findViewById(R.id.chat_slide_menu);
            com.github.clans.fab.FloatingActionButton home = activity.findViewById(R.id.home_fab);
            com.github.clans.fab.FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);

            if (chatListMenu != null) {
                chatListMenu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
                chatListMenu.setMenuButtonColorPressed(colorStateList.getDefaultColor());
            }

            if (home != null) {
                VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_home_white_24dp, null);
                vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

                home.setColorNormal(colorStateList.getDefaultColor());
                home.setColorPressed(colorStateList.getDefaultColor());
                home.setImageDrawable(vectorDrawableCompat);
            }

            if (addChat != null) {
                VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_add_black_24dp, null);
                vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

                addChat.setColorNormal(colorStateList.getDefaultColor());
                addChat.setColorPressed(colorStateList.getDefaultColor());
                addChat.setImageDrawable(vectorDrawableCompat);
            }

            //showChatsTint(colorStateList, activity);
        }
    }
}

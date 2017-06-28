package com.huetoyou.chatexchange.ui.misc.hue;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

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

        FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);
        FloatingActionButton homeFab = activity.findViewById(R.id.home_fab);

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);

        if (addChat != null) {
            addChat.setBackgroundTintList(colorStateList);
        }

        if (homeFab != null) {
            homeFab.setBackgroundTintList(colorStateList);
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
            int hue = 0;
            FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);
            FloatingActionButton homeFab = activity.findViewById(R.id.home_fab);

            hue = mSharedPreferences.getInt("fab_color",activity.getResources().getColor(R.color.colorAccent));

            ColorStateList colorStateList = ColorStateList.valueOf(hue);

            if (addChat != null) {
                addChat.setBackgroundTintList(colorStateList);
            }

            if (homeFab != null) {
                homeFab.setBackgroundTintList(colorStateList);
            }

            //showChatsTint(colorStateList, activity);
        }
    }
}

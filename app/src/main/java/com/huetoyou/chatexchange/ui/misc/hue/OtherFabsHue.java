package com.huetoyou.chatexchange.ui.misc.hue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;

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
        setTints(colorStateList, activity);
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
            setTints(colorStateList, activity);

            //showChatsTint(colorStateList, activity);
        }
    }

    private void setTints(ColorStateList colorStateList, Activity activity) {
        FloatingActionMenu chatListMenu = activity.findViewById(R.id.chat_slide_menu);
        com.github.clans.fab.FloatingActionButton home = activity.findViewById(R.id.home_fab);
        com.github.clans.fab.FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);
        com.github.clans.fab.FloatingActionButton removeChats = activity.findViewById(R.id.remove_all_chats_fab);

        if (chatListMenu != null)
        {
            chatListMenu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
            chatListMenu.setMenuButtonColorPressed(colorStateList.getDefaultColor());

            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_more_vert_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            chatListMenu.getMenuIconView().setImageDrawable(vectorDrawableCompat);
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

        if (removeChats != null) {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_clear_all_black_24dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            removeChats.setColorNormal(colorStateList.getDefaultColor());
            removeChats.setColorPressed(colorStateList.getDefaultColor());
            removeChats.setImageDrawable(vectorDrawableCompat);
        }
    }
}

package com.huetoyou.chatexchange.ui.misc.hue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;

import java.net.HttpURLConnection;

/*
 * This class is for setting the color of the FABs in a chat fragment {??except the show chats fab}
 */

public class ChatFragFabsHue
{
    private static SharedPreferences mSharedPreferences = null;

    public static void setChatFragmentFabColor(AppCompatActivity activity, @ColorInt int appBarColor)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);
        tints(colorStateList, activity);
    }

    public static void setChatFragmentFabColorToSharedPrefsValue(AppCompatActivity activity)
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

    private static void tints(ColorStateList colorStateList, AppCompatActivity activity)
    {
        com.github.clans.fab.FloatingActionButton showUsers = activity.findViewById(R.id.show_users_fab);
        com.github.clans.fab.FloatingActionButton roomInfo = activity.findViewById(R.id.room_info_fab);
        com.github.clans.fab.FloatingActionButton stars = activity.findViewById(R.id.star_fab);
        FloatingActionMenu menu = activity.findViewById(R.id.chat_menu);

        if (menu != null)
        {
            menu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
            menu.setMenuButtonColorPressed(colorStateList.getDefaultColor());

            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_expand_more_black_32dp, null);
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            menu.getMenuIconView().setImageDrawable(vectorDrawableCompat);
        }

        if (showUsers != null)
        {
            setColorsOnFabs(showUsers, colorStateList, activity, R.drawable.ic_supervisor_account_black_24dp);
        }

        if (roomInfo != null)
        {
            setColorsOnFabs(roomInfo, colorStateList, activity, R.drawable.ic_info_outline_black_24dp);
        }

        if (stars != null)
        {
            setColorsOnFabs(stars, colorStateList, activity, R.drawable.ic_star_black_24dp);
        }

    }

    private static void setColorsOnFabs(FloatingActionButton fab, ColorStateList colorStateList, Activity activity, @DrawableRes int drawable) {
        boolean desiredThemeIsDark = mSharedPreferences.getBoolean("darkTheme", false);

        @ColorInt int colorNormal;
        @ColorInt int colorPressed;
        @ColorInt int textColor;
//        TypedArray a;

        if (desiredThemeIsDark) {
//            a = activity.getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.colorBackgroundFloating});
            textColor = HueUtils.darkenColor(activity.getResources().getColor(R.color.white), .9f);
            colorNormal = HueUtils.darkenColor(activity.getResources().getColor(android.R.color.darker_gray), .6f);
            colorPressed = colorNormal;
        } else {
//            a = activity.getTheme().obtainStyledAttributes(R.style.DarkTheme, new int[] {R.attr.colorBackgroundFloating});
            textColor = activity.getResources().getColor(android.R.color.primary_text_dark);
            colorNormal = 0xFF333333;
            colorPressed = 0xFF444444;
        }

//        color = a.getColor(0, 0);
//        a.recycle();

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), drawable, null);
        vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

        fab.setColorNormal(colorStateList.getDefaultColor());
        fab.setColorPressed(colorStateList.getDefaultColor());
        fab.setImageDrawable(vectorDrawableCompat);
        fab.setLabelColors(colorNormal, colorPressed, fab.getColorRipple());
        fab.setLabelTextColor(textColor);
    }
}

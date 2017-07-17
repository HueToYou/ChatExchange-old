package com.huetoyou.chatexchange.ui.misc.hue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;

public class OtherFabsHue
{
    /*
     * This class is for setting the color of the show chats FAB and the add new chat FAB
     */

    private static SharedPreferences mSharedPreferences = null;

    public static void setAddChatFabColor(AppCompatActivity activity, @ColorInt int appBarColor)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);
        setTints(colorStateList, activity);
    }

    public static void setAddChatFabColorToSharedPrefsValue(AppCompatActivity activity)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            int initialColor = -1;
            if(mSharedPreferences.getBoolean("same_fab_color", false))
            {
                initialColor = mSharedPreferences.getInt("default_color", activity.getResources().getColor(R.color.colorPrimary));
            }
            else
            {
                initialColor = mSharedPreferences.getInt("fab_color", activity.getResources().getColor(R.color.colorAccent));
            }
            ColorStateList colorStateList = ColorStateList.valueOf(initialColor);
            setTints(colorStateList, activity);
        }
    }

    public static int getPrefsFabColor(AppCompatActivity activity) {
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            return mSharedPreferences.getInt("fab_color", activity.getResources().getColor(R.color.colorAccent));
        }

        return 0x00000000;
    }

    private static void setTints(ColorStateList colorStateList, Activity activity)
    {
        FloatingActionMenu chatListMenu = activity.findViewById(R.id.chat_slide_menu);
        com.github.clans.fab.FloatingActionButton home = activity.findViewById(R.id.home_fab);
        com.github.clans.fab.FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);
        com.github.clans.fab.FloatingActionButton removeChats = activity.findViewById(R.id.remove_all_chats_fab);

        if (chatListMenu != null)
        {
            chatListMenu.setMenuButtonColorNormal(colorStateList.getDefaultColor());
            chatListMenu.setMenuButtonColorPressed(colorStateList.getDefaultColor());

            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_expand_less_black_32dp, null);
            assert vectorDrawableCompat != null;
            vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

            chatListMenu.getMenuIconView().setImageDrawable(vectorDrawableCompat);
        }

        if (home != null)
        {
            setColorsOnFabs(home, colorStateList, activity, R.drawable.ic_home_white_24dp);
        }

        if (addChat != null)
        {
            setColorsOnFabs(addChat, colorStateList, activity, R.drawable.ic_add_black_24dp);
        }

        if (removeChats != null)
        {
            setColorsOnFabs(removeChats, colorStateList, activity, R.drawable.ic_clear_all_black_24dp);
        }
    }

    private static void setColorsOnFabs(FloatingActionButton fab, ColorStateList colorStateList, Activity activity, @DrawableRes int drawable)
    {
        boolean desiredThemeIsDark = mSharedPreferences.getBoolean("darkTheme", false);

        @ColorInt int colorNormal;
        @ColorInt int colorPressed;
        @ColorInt int textColor;
//        TypedArray a;

        if (desiredThemeIsDark)
        {
//            a = activity.getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.colorBackgroundFloating});
            textColor = HueUtils.darkenColor(activity.getResources().getColor(R.color.white), .9f);
            colorNormal = HueUtils.darkenColor(activity.getResources().getColor(android.R.color.darker_gray), .6f);
            colorPressed = colorNormal;
        }
        else
        {
//            a = activity.getTheme().obtainStyledAttributes(R.style.DarkTheme, new int[] {R.attr.colorBackgroundFloating});
            textColor = activity.getResources().getColor(android.R.color.primary_text_dark);
            colorNormal = 0xFF333333;
            colorPressed = 0xFF444444;
        }

//        color = a.getColor(0, 0);
//        a.recycle();

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), drawable, null);
        assert vectorDrawableCompat != null;
        vectorDrawableCompat.setTint(Color.rgb(255, 255, 255));

        fab.setColorNormal(colorStateList.getDefaultColor());
        fab.setColorPressed(colorStateList.getDefaultColor());
        fab.setImageDrawable(vectorDrawableCompat);
        fab.setLabelColors(colorNormal, colorPressed, fab.getColorRipple());
        fab.setLabelTextColor(textColor);
    }
}

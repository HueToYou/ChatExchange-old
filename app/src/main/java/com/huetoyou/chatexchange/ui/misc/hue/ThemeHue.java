package com.huetoyou.chatexchange.ui.misc.hue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huetoyou.chatexchange.R;

public class ThemeHue
{
    /*
     * This class is for setting the theme of MainActivity
     */

    private SharedPreferences mSharedPreferences = null;

    public void setTheme(Activity activity)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        boolean desiredThemeIsDark = mSharedPreferences.getBoolean("darkTheme", false);

        if(desiredThemeIsDark)
        {
            activity.setTheme(R.style.DarkTheme);
        }
    }

    public void setThemeOnResume(Activity activity, boolean oncreatejustcalled)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if(mSharedPreferences.getBoolean("FLAG_restartMain", false) && !oncreatejustcalled)
        {
            mSharedPreferences.edit().putBoolean("FLAG_restartMain", false).apply();
            activity.recreate();
        }
    }
}
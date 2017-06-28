package com.huetoyou.chatexchange.ui.misc.hue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huetoyou.chatexchange.R;

/*
 * This class is for dynamically setting the theme of an Activity
 */

public class ThemeHue
{
    private SharedPreferences mSharedPreferences = null;

    /*
     * Only call this method if setContentView() has not been called yet
     */
    public void setTheme(Activity activity)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        //Grab the value of the darkTheme preference
        boolean desiredThemeIsDark = mSharedPreferences.getBoolean("darkTheme", false);

        //We only need to explicitly set the theme if the user wants the dark theme, as light is the default
        if(desiredThemeIsDark)
        {
            //NB: this *must* be called before setContentView(), else weird things happen
            activity.setTheme(R.style.DarkTheme);
        }
    }

    /*
     * Do *not* call this method from onCreate(); used for changing the theme on-the-fly; you should call this in onResume()
     */
    public void setThemeOnResume(Activity activity, boolean oncreatejustcalled)
    {
        //Grab an instance of SharedPrefs if we haven't already
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        //Check to see whether or not the activity must be restarted due to a requested theme change.
        //Requires the oncreatejustcalled parameter to prevent setting the theme twice, as onResume() is called after onCreate() finishes
        if(mSharedPreferences.getBoolean("FLAG_restartMain", false) && !oncreatejustcalled)
        {
            mSharedPreferences.edit().putBoolean("FLAG_restartMain", false).apply();
            activity.recreate();
        }
    }
}
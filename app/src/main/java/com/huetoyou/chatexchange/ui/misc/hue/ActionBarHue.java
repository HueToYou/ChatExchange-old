package com.huetoyou.chatexchange.ui.misc.hue;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.AppCompatPreferenceActivity;

public class ActionBarHue
{
    /*
     * This class is for setting the color of the action bar
     */

    private SharedPreferences mSharedPreferences = null;

    public void setActionBarColor(AppCompatActivity activity, int mAppBarColor)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();

            if (actionBar != null)
            {
                actionBar.setBackgroundDrawable(new ColorDrawable(mAppBarColor));

                //Change status bar color too
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(HueUtils.darkenColor(mAppBarColor, 0.7f));
                }
            }
        }
    }

    public void setActionBarColorToSharedPrefsValue(AppCompatActivity activity)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();

            if (actionBar != null)
            {
                int initialColor = mSharedPreferences.getInt("default_color", activity.getResources().getColor(R.color.colorPrimary));
                actionBar.setBackgroundDrawable(new ColorDrawable(initialColor));

                //Change status bar color too
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(HueUtils.darkenColor(initialColor, 0.7f));
                }

            }
        }
    }

    public void setActionBarColorToSharedPrefsValue(AppCompatPreferenceActivity activity)
    {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();

            if (actionBar != null)
            {
                int initialColor = mSharedPreferences.getInt("default_color", activity.getResources().getColor(R.color.colorPrimary));
                actionBar.setBackgroundDrawable(new ColorDrawable(initialColor));


                //Change status bar color too
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(HueUtils.darkenColor(initialColor, 0.7f));
                }
            }
        }
    }
}

package com.huetoyou.chatexchange.ui.misc;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.huetoyou.chatexchange.ui.activity.AppCompatPreferenceActivity;

public class HueUtils
{
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
                    Window window = ((AppCompatActivity) activity).getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(manipulateColor(mAppBarColor, 0.7f));
                }
            }
        }
    }

    public void setActionBarColorDefault(AppCompatActivity activity)
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
                int initialColor = mSharedPreferences.getInt("default_color", 0xFF000000);
                actionBar.setBackgroundDrawable(new ColorDrawable(initialColor));

                //Change status bar color too
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(manipulateColor(initialColor, 0.7f));
                }
            }
        }
    }

    public void setActionBarColorDefault(AppCompatPreferenceActivity activity)
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
                int initialColor = mSharedPreferences.getInt("default_color", 0xFF000000);
                actionBar.setBackgroundDrawable(new ColorDrawable(initialColor));

                //Change status bar color too
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(manipulateColor(initialColor, 0.7f));
                }
            }
        }
    }

    private static int manipulateColor(int color, float factor)
    {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }
}

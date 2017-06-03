package com.huetoyou.chatexchange.ui.misc;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.internal.ForegroundLinearLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.huetoyou.chatexchange.R;
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

    public void setAddChatFabColor(AppCompatActivity activity, @ColorInt int appBarColor) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        FloatingActionButton addChat = (FloatingActionButton) activity.findViewById(R.id.add_chat_fab);

        if (addChat != null) {
            ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {appBarColor});

            addChat.setBackgroundTintList(colorStateList);
        }
    }

    public void setAddChatFabColorDefault(AppCompatActivity activity) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            FloatingActionButton addChat = (FloatingActionButton) activity.findViewById(R.id.add_chat_fab);

            if (addChat != null)
            {
                int initialColor = mSharedPreferences.getInt("default_color", 0xFF000000);

                ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {initialColor});
                addChat.setBackgroundTintList(colorStateList);
            }
        }
    }

    public void setChatFragmentFabColor(AppCompatActivity activity, @ColorInt int appBarColor) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        FloatingActionButton closeChat = (FloatingActionButton) activity.findViewById(R.id.close_chat_frag);
        FloatingActionButton openBrowser = (FloatingActionButton) activity.findViewById(R.id.open_in_browser_fab);
        FloatingActionButton showUsers = (FloatingActionButton) activity.findViewById(R.id.show_users_fab);

        ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {appBarColor});

        closeChat.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
        closeChat.setBackgroundTintList(colorStateList);
        openBrowser.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
        openBrowser.setBackgroundTintList(colorStateList);
        showUsers.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
        showUsers.setBackgroundTintList(colorStateList);
    }

    public void setChatFragmentFabColorDefault(AppCompatActivity activity) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            FloatingActionButton closeChat = (FloatingActionButton) activity.findViewById(R.id.close_chat_frag);
            FloatingActionButton openBrowser = (FloatingActionButton) activity.findViewById(R.id.open_in_browser_fab);
            FloatingActionButton showUsers = (FloatingActionButton) activity.findViewById(R.id.show_users_fab);

            int initialColor = mSharedPreferences.getInt("default_color", 0xFF000000);
            ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {initialColor});

            closeChat.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            closeChat.setBackgroundTintList(colorStateList);
            openBrowser.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            openBrowser.setBackgroundTintList(colorStateList);
            showUsers.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            showUsers.setBackgroundTintList(colorStateList);
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

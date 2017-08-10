package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    public static ViewGroup getActionBar(View windowDecorView)
    {
        try
        {
            if (windowDecorView instanceof ViewGroup)
            {
                ViewGroup viewGroup = (ViewGroup) windowDecorView;

                if (viewGroup instanceof android.support.v7.widget.Toolbar)
                {
                    return viewGroup;
                }

                for (int i = 0; i < viewGroup.getChildCount(); i++)
                {
                    ViewGroup actionBar = getActionBar(viewGroup.getChildAt(i));

                    if (actionBar != null)
                    {
                        return actionBar;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int spToPixels(int sp, Activity activity)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, activity.getResources().getDisplayMetrics());
    }

    public static float getAnimDuration(float origDuration, Context context)
    {
        float systemAnimScale = 1.0f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            systemAnimScale = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            systemAnimScale = Settings.System.getFloat(context.getContentResolver(), Settings.System.ANIMATOR_DURATION_SCALE, 1.0f);
        }

        return origDuration / systemAnimScale;
    }
}
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
    private static SharedPreferences mSharedPreferences = null;

    public static int getColorInt(Activity activity, String url)
    {

        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        try
        {
            Document doc = Jsoup.connect(url).get();

            Elements styles = doc.select("link");
            Element element = new Element("hue");

            for (int i = 0; i < styles.size(); i++)
            {
                Element current = styles.get(i);

                if (current.hasAttr("href") && current.attr("rel").equals("stylesheet"))
                {
                    element = current;
                    break;
                }
            }

            String link = "";
            if (element.hasAttr("href"))
            {
                link = element.attr("href");
                if (!(link.contains("http://") || link.contains("https://")))
                {
                    link = "https:".concat(link);
                }
            }


//                Log.e("UR", link);
            URL url1 = new URL(link);

            InputStream inStr = url1.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStr));
            String line;
            String css = "";

            while ((line = bufferedReader.readLine()) != null)
            {
                css = css.concat(line);
            }


            Pattern p = Pattern.compile("\\.msparea\\{(.+?)\\}");
            Matcher m = p.matcher(css);
            String a = "";

            if (m.find())
            {
                a = m.group();
            }

            p = Pattern.compile("color:(.*?);");
            m = p.matcher(a);

            String colorHex = "#000000";

            if (m.find())
            {
                colorHex = m.group().replace("color", "").replace(":", "").replace(";", "").replaceAll(" ", "");
            }

            try
            {
                mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("UNKNOWN COLOR", url);
                String threeChar = colorHex.replace("#", "");
                String one = threeChar.substring(0, 1);
                String two = threeChar.substring(1, 2);
                String three = threeChar.substring(2);

                colorHex = "#".concat(one).concat(one).concat(two).concat(two).concat(three).concat(three);

                mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Color.parseColor("#000000");
        }
    }

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

    public static int spToPixels(int sp, Activity activity) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, activity.getResources().getDisplayMetrics());
    }

    public static float getAnimDuration(float origDuration, Context context) {
        float systemAnimScale = 1.0f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            systemAnimScale = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            systemAnimScale = Settings.System.getFloat(context.getContentResolver(), Settings.System.ANIMATOR_DURATION_SCALE, 1.0f);
        }

        return origDuration/systemAnimScale;
    }
}
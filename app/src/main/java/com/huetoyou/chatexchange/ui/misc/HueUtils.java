package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huetoyou.chatexchange.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    Window window = activity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(manipulateColor(mAppBarColor, 0.7f));
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
                    window.setStatusBarColor(manipulateColor(initialColor, 0.7f));
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

        FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);

        if (addChat != null) {
            addChat.setBackgroundTintList(colorStateList);
        }
    }

    public void setAddChatFabColorToSharedPrefsValue(AppCompatActivity activity) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null) {
            FloatingActionButton addChat = activity.findViewById(R.id.add_chat_fab);

            int initialColor = mSharedPreferences.getInt("default_color",activity.getResources().getColor(R.color.colorPrimary));
            ColorStateList colorStateList = ColorStateList.valueOf(initialColor);

            if (addChat != null) {
                addChat.setBackgroundTintList(colorStateList);
            }

            showChatsTint(colorStateList, activity);

        }
    }

    public void setChatFragmentFabColor(AppCompatActivity activity, @ColorInt int appBarColor) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(appBarColor);
        tints(colorStateList, activity);
    }

    public void setChatFragmentFabColorToSharedPrefsValue(AppCompatActivity activity) {
        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (activity != null)
        {
            //int initialColor = mSharedPreferences.getInt("default_color", activity.getResources().getColor(R.color.colorPrimary));.
            int initialColor = activity.getResources().getColor(R.color.colorAccent);
            ColorStateList colorStateList = ColorStateList.valueOf(initialColor);
            tints(colorStateList, activity);
        }
    }

    private void tints(ColorStateList colorStateList, AppCompatActivity activity) {
        FloatingActionButton closeChat = activity.findViewById(R.id.close_chat_frag);
        FloatingActionButton openBrowser = activity.findViewById(R.id.open_in_browser_fab);
        FloatingActionButton showUsers = activity.findViewById(R.id.show_users_fab);
        FloatingActionButton roomInfo = activity.findViewById(R.id.room_info_fab);
        FloatingActionButton stars = activity.findViewById(R.id.star_fab);
        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);

        if (closeChat != null) {
            closeChat.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            closeChat.setBackgroundTintList(colorStateList);
        }
        if (openBrowser != null) {
            openBrowser.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            openBrowser.setBackgroundTintList(colorStateList);
        }
        if (showUsers != null) {
            showUsers.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            showUsers.setBackgroundTintList(colorStateList);
        }
        if (roomInfo != null) {
            roomInfo.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            roomInfo.setBackgroundTintList(colorStateList);
        }
        if (stars != null) {
            stars.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            stars.setBackgroundTintList(colorStateList);
        }

        showChatsTint(colorStateList, activity);
    }

    public void showChatsTint(ColorStateList colorStateList, AppCompatActivity activity) {
        FloatingActionButton showChats = activity.findViewById(R.id.show_chats_fab);
        if (showChats != null) {
            showChats.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            showChats.setBackgroundTintList(colorStateList);
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
            mSharedPreferences.edit().putBoolean("FLAG_restartMain", false);
            activity.recreate();
        }
    }

    public int getColorInt(Activity activity, String url) {

        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        try {
            Document doc = Jsoup.connect(url).get();

            Elements styles = doc.select("link");
            Element element = new Element("hue");

            for (int i = 0; i < styles.size(); i++) {
                Element current = styles.get(i);

                if (current.hasAttr("href") && current.attr("rel").equals("stylesheet")) {
                    element = current;
                    break;
                }
            }

            String link = "";
            if (element.hasAttr("href")) {
                link = element.attr("href");
                if (!(link.contains("http://") || link.contains("https://")))
                    link = "https:".concat(link);
            }


//                Log.e("UR", link);
            URL url1 = new URL(link);

            InputStream inStr = url1.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStr));
            String line;
            String css = "";

            while ((line = bufferedReader.readLine()) != null) {
                css = css.concat(line);
            }


            Pattern p = Pattern.compile("\\.msparea\\{(.+?)\\}");
            Matcher m = p.matcher(css);
            String a = "";

            if (m.find()) {
                a = m.group();
            }

            p = Pattern.compile("color:(.*?);");
            m = p.matcher(a);

            String colorHex = "#000000";

            if (m.find()) {
                colorHex = m.group().replace("color", "").replace(":", "").replace(";", "").replaceAll(" ", "");
            }

            mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex));
            mSharedPreferences.edit().apply();
            return Color.parseColor(colorHex);
        } catch (Exception e) {
            e.printStackTrace();
            return Color.parseColor("#000000");
        }
    }
}

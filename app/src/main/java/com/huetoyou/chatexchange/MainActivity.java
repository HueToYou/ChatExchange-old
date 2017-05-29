package com.huetoyou.chatexchange;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Html;
import android.text.Spanned;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private TabLayout mTabLayout;
    private FragmentManager mFragmentManager;

    private Set<String> mChatUrls = new HashSet<>();

    private boolean mUseDark;
    private boolean mTabAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getSharedPreferences(getResources().getText(R.string.app_name).toString(), MODE_PRIVATE);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();

        mEditor.putInt("tabIndex", 0).apply();

        mUseDark = mSharedPrefs.getBoolean("isDarkMode", false);

        mFragmentManager = getFragmentManager();

        setup();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.activity_main);
//
//        mChatUrls = new HashSet<>();
//        setup();
//    }

    private void setup() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        try {
            mTabLayout.addTab(mTabLayout.newTab().setText("Accounts").setIcon(R.drawable.ic_stackexchange));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        for (String s : mSharedPrefs.getStringSet("chatURLs", new HashSet<String>())) {
            addTab(s);
//            while (!mTabAdded);
        }

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
            int tabIndex = mSharedPrefs.getInt("tabIndex", 0);
            setFragmentByTab(mTabLayout.getTabAt(tabIndex));
        } else {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        tabListener();
    }

    private void setFragmentByTab(TabLayout.Tab tab) {
        Fragment fragment;

        switch (tab.getPosition()) {
            case 0:
                fragment = new AccountsFragment();
                break;
            default:
                fragment = new ChatFragment();
                break;
        }

        mEditor.putInt("tabIndex", tab.getPosition()).apply();

        if (fragment instanceof ChatFragment) {
            if (tab.getText() != null) mEditor.putString("chatTitle", tab.getText().toString());
            mEditor.apply();
        }

        mFragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
    }

    private void tabListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFragmentByTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {
                if (tab.getPosition() != 0) {
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    vib.vibrate(100);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, mUseDark ? R.style.Theme_AppCompat : R.style.Theme_AppCompat_Light))
                                    .setTitle(getResources().getText(R.string.activity_main_delete_chat_title))
                                    .setMessage(getResources().getText(R.string.activity_main_delete_chat_message))
                                    .setPositiveButton(getResources().getText(R.string.generic_yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (tab.getTag() != null) {
                                                mChatUrls.remove(tab.getTag().toString());
                                                mEditor.putStringSet("chatURLs", mChatUrls).apply();
                                                if (mTabLayout.getTabAt(tab.getPosition() - 1) != null) mTabLayout.getTabAt(tab.getPosition() - 1).select();
                                                mTabLayout.removeTab(tab);
                                            }
                                        }
                                    })
                                    .setNegativeButton(getResources().getText(R.string.generic_no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
        });
    }

    public void addTab(final String chatUrl) {
        mTabAdded = false;
        if (!mChatUrls.contains(chatUrl)) {
            mChatUrls.add(chatUrl);
            mEditor.putStringSet("chatURLs", mChatUrls);
            mEditor.apply();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    TabLayout.Tab tab = null;

                    try {
                        String chatName = new GetName().execute(chatUrl).get();
                        Spanned name;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            name = Html.fromHtml(chatName, Html.FROM_HTML_MODE_LEGACY);
                        } else {
                            //noinspection deprecation
                            name = Html.fromHtml(chatName);
                        }
                        tab = mTabLayout.newTab().setText(name).setIcon(new GetIcon().execute(chatUrl).get()).setTag(chatUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (tab != null && !chatUrl.isEmpty()) {
                        final TabLayout.Tab tab1 = tab;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTabLayout.addTab(tab1);
                            }
                        });
                    }
                    mTabAdded = true;
                }
            }).start();
        } else {
            Toast.makeText(this, getResources().getText(R.string.activity_main_chat_already_added).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class GetName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url;
            InputStream is = null;
            BufferedReader br;
            String line;
            String html = "";

            try {
                url = new URL(params[0]);
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null) {
                    html += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException ioe) {
                    // nothing to see here
                }
            }
            Pattern p = Pattern.compile("<title>(.+?)</title>");
            Matcher m = p.matcher(html);
            String title = null;
            try {
                boolean idk = m.find();
                title = m.group().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return title;
        }
    }

    private class GetIcon extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            Bitmap bmp;
            try {
                if (!params[0].contains("http")) params[0] = "https://".concat(params[0]);
                Connection con2= Jsoup.connect(params[0]);
                Document doc = con2.get();
                Element e = doc.head().select("link[href~=.*\\.ico]").first();
                String ico = e.attr("href");
                ico = ico.replace("?v=da", "");
                if (!ico.contains("http")) ico = "https:".concat(ico);

                URL url = new URL(ico);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, 144, 144, true));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

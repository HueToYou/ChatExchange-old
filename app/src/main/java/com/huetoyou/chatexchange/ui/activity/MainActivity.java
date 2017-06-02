package com.huetoyou.chatexchange.ui.activity;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.huetoyou.chatexchange.ui.frags.AccountsFragment;
import com.huetoyou.chatexchange.ui.frags.ChatFragment;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

import io.fabric.sdk.android.Fabric;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
    private ArrayList<TabLayout.Tab> mTabs = new ArrayList<>();
    private SparseArray<Fragment> mCurrentFragments = new SparseArray<>();

    private FragmentManager mFragmentManager;

    private Intent mIntent;

    private Set<String> mChatUrls = new HashSet<>();

    private boolean mUseDark;
    private boolean mDoneAddingChats = false;
    private Thread mAddTab;

    private final int HOME_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();

//        mEditor.putInt("tabIndex", 0).apply();

        mUseDark = mSharedPrefs.getBoolean("isDarkMode", false);

        mFragmentManager = getSupportFragmentManager();

        mIntent = getIntent();

        setup();
        setActionBarColor();

        //ColorPickerDialog.newBuilder().setColor(color).show(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent prefIntent = new Intent(this, PreferencesActivity.class);
                int requestCode = 1; // Or some number you choose
                startActivityForResult(prefIntent, requestCode);
                break;
            case R.id.action_about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_help:
                Intent startHelpActivity = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(startHelpActivity);
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setup() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        try {
            TabLayout.Tab home = mTabLayout.newTab()
                    .setText(getResources().getText(R.string.generic_accounts))
                    .setIcon(new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 144, 144, true)))
                    .setTag("home");

//            TabLayout.Tab add = mTabLayout.newTab()
//                    .setText(getResources()
//                            .getText(R.string.activity_main_add_chat))
//                    .setIcon(new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 144, 144, true)))
//                    .setTag("add");


//            mTabLayout.addTab(add);
            mTabLayout.addTab(home);

//            home.select();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        Button addChatButton = (Button) findViewById(R.id.add_chat_button);
        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTabDialog();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String s : mSharedPrefs.getStringSet("chatURLs", new HashSet<String>())) {
                    addTab(s);
                    while (mAddTab.isAlive());
                }
                mDoneAddingChats = true;
            }
        }).start();

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
//            int tabIndex = mSharedPrefs.getInt("tabIndex", 0);
            addFragmentByTab(mTabLayout.getTabAt(HOME_INDEX));
        } else {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        tabListener();

        if (mAddTab != null && mIntent != null && mIntent.getAction() != null) {
            final String action = mIntent.getAction();
            if (action.equals(Intent.ACTION_OPEN_DOCUMENT) || action.equals(Intent.ACTION_MAIN)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        while (!mDoneAddingChats);
                        Bundle extras = mIntent.getExtras();
                        Object o = null;
                        if (extras != null) o = extras.get("chatURL");
                        String url = "";
                        if (o != null) url = o.toString();
                        TabLayout.Tab tab = getTabByURL(url);

                        if (tab != null) addTab(url);
                        while (tab == null) tab = getTabByURL(url);

                        final TabLayout.Tab t2 = tab;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addFragmentByTab(t2);
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private TabLayout.Tab getTabByURL(String url) {
        for (int i = 0; i < mTabs.size(); i++) {
            TabLayout.Tab tab = mTabs.get(i);
            String tabTag = tab.getTag() != null ? tab.getTag().toString().replace("http://", "").replace("https://", "").replace("/", "").replace("#", "") : "";
            url = url.replace("http://", "").replace("https://", "").replace("/", "").replace("#", "");

            if ((url.contains(tabTag) || tabTag.contains(url)) && url.length() > 0) return tab;
        }

        return null;
    }

    private void setFragmentByTab(TabLayout.Tab tab) {
        String tag = "";
        if (tab.getTag() != null) tag = tab.getTag().toString();

        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        for (Fragment fragment1 : mFragmentManager.getFragments()) {
            if (!fragment1.isDetached()) mFragmentManager.beginTransaction().hide(fragment1).commit();
        }

        if (fragment != null) {
            mFragmentManager.beginTransaction().show(fragment).commit();
        }
    }

    private void addFragmentByTab(TabLayout.Tab tab) {
        if (tab != null) {
            Fragment fragment;

            if (mCurrentFragments.get(tab.getPosition()) != null) {
                fragment = mCurrentFragments.get(tab.getPosition());
            } else {
                switch (tab.getTag().toString()) {
                    case "home":
                        fragment = new AccountsFragment();
                        break;
                    default:
                        fragment = new ChatFragment();
                        break;
                }
                mCurrentFragments.put(tab.getPosition(), fragment);

                if (fragment instanceof ChatFragment) {
                    Bundle args = new Bundle();
                    if (tab.getText() != null) args.putString("chatTitle", tab.getText().toString());
                    if (tab.getTag() != null) args.putString("chatUrl", tab.getTag().toString());
                    if (tab.getContentDescription() != null) args.putInt("AppBarColor", Integer.decode(tab.getContentDescription().toString()));
                    fragment.setArguments(args);
                }

            }

            String tag = "";
            if (tab.getTag() != null) tag = tab.getTag().toString();

            if (mFragmentManager.findFragmentByTag(fragment.getTag()) == null) {
                mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).hide(fragment).commit();
            }

            if (tab.getPosition() == HOME_INDEX) {
                mFragmentManager.beginTransaction().show(fragment).commit();
            }

            mFragmentManager.executePendingTransactions();
        }
    }

    private void tabListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                addFragmentByTab(tab);
                setFragmentByTab(tab);
                if (tab.getPosition() == HOME_INDEX) setActionBarColor();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {

            }
        });
    }

    public void showAddTabDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.activity_main_chat_url));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);
        builder.setPositiveButton(getResources().getText(R.string.generic_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTab(input.getText().toString());
            }
        });
        builder.setNegativeButton(getResources().getText(R.string.generic_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void addTab(final String chatUrl) {
        if (!mChatUrls.contains(chatUrl)) {
            mChatUrls.add(chatUrl);
            mEditor.putStringSet("chatURLs", mChatUrls);
            mEditor.apply();

            //noinspection deprecation
            mAddTab = new Thread(new Runnable() {
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
                        tab = mTabLayout.newTab().setText(name).setIcon(new GetIcon().execute(chatUrl).get()).setTag(chatUrl).setContentDescription(String.valueOf(new GetColorInt().execute(chatUrl).get()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (tab != null && !chatUrl.isEmpty()) {
                        final TabLayout.Tab tab1 = tab;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTabLayout.addTab(tab1);
                                mTabs.add(tab1);
                            }
                        });
                    }
                }
            });
            mAddTab.start();
        } else {
            Toast.makeText(this, getResources().getText(R.string.activity_main_chat_already_added).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class GetName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return Jsoup.connect(params[0]).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class GetColorInt extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];

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

                mSharedPrefs.edit().putInt(params[0] + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            } catch (Exception e) {
                e.printStackTrace();
                return Color.parseColor("#000000");
            }
        }
    }

    private class GetIcon extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                Element head = document.head();
                Element link = head.select("link").first();

                String fav = link.attr("href");
                if (!fav.contains("http")) fav = "https:".concat(fav);
                URL url = new URL(fav);

                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, 144, 144, true));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void setActionBarColor()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("default_color", 0xFF000000);
        System.out.println(initialColor);


        android.support.v7.app.ActionBar bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(initialColor);
        if (bar != null) bar.setBackgroundDrawable(cd);
    }

    public void confirmClose(View v) {
        if (mTabLayout.getSelectedTabPosition() != 0) {
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
                                    if (mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getTag() != null) {
                                        mChatUrls.remove(mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getTag().toString());
                                        mEditor.putStringSet("chatURLs", mChatUrls).apply();
                                        TabLayout.Tab prev = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition() - 1);
                                        mTabLayout.removeTab(mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()));
                                        if (prev != null) prev.select();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences mSharedPreferences;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mTabLayout.getSelectedTabPosition() == 0) {
            setActionBarColor();
        }
        else if (!mSharedPreferences.getBoolean("dynamicallyColorBar", false))
        {
            setActionBarColor();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

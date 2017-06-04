package com.huetoyou.chatexchange.ui.activity;

import android.accounts.AccountManager;
import android.app.Activity;
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
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.huetoyou.chatexchange.ui.frags.AccountsFragment;
import com.huetoyou.chatexchange.ui.frags.ChatFragment;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.huetoyou.chatexchange.ui.misc.ImgTextArrayAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

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

    private SlidingMenu mChatroomSlidingMenu;
    private ListView chatroomsList;
    private ArrayList<String> chatroomArrayList;
    private ArrayAdapter<String> chatroomArrayAdapter;

    private ArrayList<String> chatroomNames = new ArrayList<>();
    private ArrayList<String> chatroomDescs = new ArrayList<>();
    private ArrayList<Drawable> chatroomIcons = new ArrayList<>();
    private int chatroomArrayIndex = 0;

    private FragmentManager mFragmentManager;

    private Intent mIntent;

    private Set<String> mChatUrls = new HashSet<>();

    private boolean mUseDark;
    private boolean mDoneAddingChats = false;

    private final int HOME_INDEX = 0;

    private Handler mHandler;

    private HueUtils hueUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        hueUtils = new HueUtils();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();
        mHandler = new Handler();

//        mEditor.putInt("tabIndex", 0).apply();

        mUseDark = mSharedPrefs.getBoolean("isDarkMode", false);

        mFragmentManager = getSupportFragmentManager();

        mIntent = getIntent();

        setup();
        hueUtils.setActionBarColorDefault(this);
        hueUtils.setAddChatFabColorDefault(this);

        //ColorPickerDialog.newBuilder().setColor(color).show(activity);
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

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_chat_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTabDialog();
            }
        });

        HandleAdds handleAdds = new HandleAdds();
        CancelTask cancel = new CancelTask(handleAdds);
        mHandler.postDelayed(cancel, 10000);
        handleAdds.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
//            int tabIndex = mSharedPrefs.getInt("tabIndex", 0);
            addFragmentByTab(mTabLayout.getTabAt(HOME_INDEX));
        } else {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        tabListener();

        if (mIntent != null && mIntent.getAction() != null)
        {
            final String action = mIntent.getAction();

            if (action.equals(Intent.ACTION_MAIN))
            {
                ReAddTabs reAddTabs = new ReAddTabs();
                CancelTask cancelTask = new CancelTask(reAddTabs);
                mHandler.postDelayed(cancelTask, 10000);
                reAddTabs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
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
            if (!fragment1.isDetached()) mFragmentManager.beginTransaction().detach(fragment1).commit();
        }

        if (fragment != null) {
            mFragmentManager.beginTransaction().attach(fragment).commit();
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

                    if (mSharedPrefs.getBoolean("dynamicallyColorBar", false)) {
                        hueUtils.setAddChatFabColor(this, Integer.decode(tab.getContentDescription().toString()));
                    }
                }

            }

            String tag = "";
            if (tab.getTag() != null) tag = tab.getTag().toString();

            if (mFragmentManager.findFragmentByTag(fragment.getTag()) == null) {
                mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).detach(fragment).commit();
            }

            if (tab.getPosition() == HOME_INDEX) {
                mFragmentManager.beginTransaction().attach(fragment).commit();
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
                if (tab.getPosition() == HOME_INDEX)
                {
                    hueUtils.setActionBarColorDefault(MainActivity.this);
                    hueUtils.setAddChatFabColorDefault(MainActivity.this);
                }
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
            AddTab addTab = new AddTab();
            CancelTask cancelTask = new CancelTask(addTab);
            mHandler.postDelayed(cancelTask, 10000);
            addTab.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chatUrl);

        } else {
            Toast.makeText(this, getResources().getText(R.string.activity_main_chat_already_added).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setActionBarColor()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("default_color", 0xFF000000);
        System.out.println(initialColor);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(initialColor);
        bar.setBackgroundDrawable(cd);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(manipulateColor(initialColor, 0.7f));
        }
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
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

    private class AddTab extends AsyncTask<String, Void, Void> {
        private Spanned name;
        private Drawable chatIcon;
        private int colorInt;
        private String chatUrl;
        private String chatName;

        @Override
        protected Void doInBackground(String... params) {
            chatUrl = params[0];

            try
            {
                chatName = getName(chatUrl);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    name = Html.fromHtml(chatName, Html.FROM_HTML_MODE_LEGACY);
                }

                else
                {
                    //noinspection deprecation
                    name = Html.fromHtml(chatName);
                }
                chatIcon = getIcon(chatUrl);
                colorInt = getColorInt(chatUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            chatroomNames.set(chatroomArrayIndex, chatName);
            chatroomIcons.set(chatroomArrayIndex, chatIcon);
            chatroomDescs.set(chatroomArrayIndex, "HUE");

            TabLayout.Tab tab = mTabLayout.newTab().setText(name).setIcon(chatIcon).setTag(chatUrl).setContentDescription(String.valueOf(colorInt));
            if (!chatUrl.isEmpty()) {
                mTabLayout.addTab(tab);
                mTabs.add(tab);
            }
            super.onPostExecute(aVoid);
        }

        private String getName(String url) {
            try {
                Elements spans = Jsoup.connect(url).get().select("span");

                for (Element e : spans) {
                    if (e.hasAttr("id") && e.attr("id").equals("roomname")) return e.ownText();
                }
                return Jsoup.connect(url).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Drawable getIcon(String chatUrl) {
            try {
                Document document = Jsoup.connect(chatUrl).get();
                Element head = document.head();
                Element link = head.select("link").first();

                String fav = link.attr("href");
                if (!fav.contains("http")) fav = "https:".concat(fav);
                URL url = new URL(fav);

                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, 144, 144, true));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private int getColorInt(String url) {
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

                mSharedPrefs.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            } catch (Exception e) {
                e.printStackTrace();
                return Color.parseColor("#000000");
            }
        }
    }

    private class CancelTask implements Runnable {
        private AsyncTask task;

        public CancelTask(AsyncTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (task.getStatus() == AsyncTask.Status.RUNNING )
                task.cancel(true);
        }
    }

    private class HandleAdds extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            chatroomArrayIndex = 0;
            boolean firstTab = true;

            for (String s : mSharedPrefs.getStringSet("chatURLs", new HashSet<String>())) {
                addTab(s);
                if(!firstTab)
                {
                    chatroomArrayIndex++;
                }
                firstTab = false;
//                while (mAddTab.isAlive());
            }

            /*try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    setupChatRoomList(chatroomNames, chatroomDescs, chatroomIcons);
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDoneAddingChats = true;
            super.onPostExecute(aVoid);
        }
    }

    private class ReAddTabs extends AsyncTask<Void, Void, Void> {
        private TabLayout.Tab tab;

        @Override
        protected Void doInBackground(Void... params) {
            while (!mDoneAddingChats);
            Bundle extras = mIntent.getExtras();
            Object o = null;
            if (extras != null) o = extras.get("chatURL");
            String url = "";
            if (o != null) url = o.toString();
            tab = getTabByURL(url);

            if (tab != null) addTab(url);
            while (tab == null) tab = getTabByURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            addFragmentByTab(tab);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences mSharedPreferences;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mTabLayout.getSelectedTabPosition() == 0) {
            hueUtils.setActionBarColorDefault(this);
        }
        else if (!mSharedPreferences.getBoolean("dynamicallyColorBar", false))
        {
            hueUtils.setActionBarColorDefault(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupChatRoomList(ArrayList<String> chatroomNames, ArrayList<String> chatroomDescs, ArrayList<Drawable> icons)
    {
        // configure the SlidingMenu
        mChatroomSlidingMenu = new SlidingMenu(this);
        mChatroomSlidingMenu.setMode(SlidingMenu.LEFT);
        mChatroomSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mChatroomSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mChatroomSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mChatroomSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mChatroomSlidingMenu.setFadeDegree(0.35f);
        mChatroomSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mChatroomSlidingMenu.setMenu(R.layout.chatroom_slideout);

        chatroomsList = (ListView) findViewById(R.id.chatroomsListView);
        chatroomArrayList = new ArrayList<String>();

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        //chatroomArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.chatroom_list_item, R.id.chatroomName, chatroomArrayList);

        if(chatroomNames != null && chatroomDescs != null && icons != null)
        {
            System.out.println("Chatroom names: " + chatroomNames);


            chatroomArrayAdapter = new ImgTextArrayAdapter(this, chatroomNames, chatroomDescs, icons);

            // Here, you set the data in your ListView
            chatroomsList.setAdapter(chatroomArrayAdapter);

            // this line adds the data of your EditText and puts in your array
            //chatroomArrayList.add("Text");
            //chatroomArrayList.add("Text 2");
            // next thing you have to do is check if your adapter has changed
            //chatroomArrayAdapter.notifyDataSetChanged();
        }
        else
        {
            System.out.println("Hue :(");
        }
    }
}

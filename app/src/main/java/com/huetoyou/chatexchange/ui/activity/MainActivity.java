package com.huetoyou.chatexchange.ui.activity;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.frags.HomeFragment;
import com.huetoyou.chatexchange.ui.frags.ChatFragment;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.ImgTextArrayAdapter;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends SlidingActivity {

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private SlidingMenu mChatroomSlidingMenu;
    private ListView chatroomsList;
    private ImgTextArrayAdapter chatroomArrayAdapter;
    private SlidingMenu mCurrentUsers_SlidingMenu;
    private FragmentManager mFragmentManager;

    private BroadcastReceiver mAddChatReceiver;

    private Intent mIntent;

    private boolean oncreatejustcalled = false;

    private Handler mHandler;

    private ThemeHue themeHue = null;
    private String mCurrentFragment;

    private boolean mCanAddChat = true;
    private RequestFactory mRequestFactory;

    private SparseArray<Fragment> mSOChats = new SparseArray<>();
    private SparseArray<Fragment> mSEChats = new SparseArray<>();

    private SparseIntArray mSOChatColors = new SparseIntArray();
    private SparseIntArray mSEChatColors = new SparseIntArray();

    private SparseArray<String> mSOChatNames = new SparseArray<>();
    private SparseArray<String> mSEChatNames = new SparseArray<>();

    private SparseArray<String> mSOChatUrls = new SparseArray<>();
    private SparseArray<String> mSEChatUrls = new SparseArray<>();

    private SparseArray<Drawable> mSOChatIcons = new SparseArray<>();
    private SparseArray<Drawable> mSEChatIcons = new SparseArray<>();

    private Set<String> mSOChatIDs = new HashSet<>();
    private Set<String> mSEChatIDs = new HashSet<>();

    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;
    private AddList mAddList;

    /*
     * Activity Lifecycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mainActivity = this;
        themeHue = new ThemeHue();
        themeHue.setTheme(MainActivity.this);

        mRequestFactory = new RequestFactory();

        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        preSetup();
        createUsersSlidingMenu();
        setup();

        oncreatejustcalled = true;
    }

    @Override
    protected void onResume()
    {
        themeHue.setThemeOnResume(MainActivity.this, oncreatejustcalled);

        if(oncreatejustcalled)
        {
            oncreatejustcalled = false;
        }

        doFragmentStuff();
        super.onResume();

        System.out.println("Hellu!");

        if (mFragmentManager.findFragmentByTag("home").isDetached())
        {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(VectorDrawableCompat.create(getResources(), R.drawable.ic_home_white_24dp, null));
        }

        else
        {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        mIntent = getIntent();
        respondToNotificationClick();

        /*if (!mFragmentManager.findFragmentByTag("home").isDetached())
        {
            actionBarHue.setActionBarColorToSharedPrefsValue(this);
        }

        else if (!mSharedPrefs.getBoolean("dynamicallyColorBar", false))
        {
            actionBarHue.setActionBarColorToSharedPrefsValue(this);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.findFragmentByTag("home").isDetached()) {
            setFragmentByTag("home");
            for (Fragment fragment : mFragmentManager.getFragments()) {
                if (fragment != null && !fragment.isDetached() && fragment instanceof ChatFragment) if (((ChatFragment) fragment).getmSlidingMenu().isMenuShowing()) ((ChatFragment) fragment).getmSlidingMenu().showContent(true);
            }
            if (mChatroomSlidingMenu.isMenuShowing()) mChatroomSlidingMenu.showContent(true);
        } else if (mChatroomSlidingMenu.isMenuShowing()) {
            mChatroomSlidingMenu.showContent(true);
        } else {
            super.onBackPressed();
        }
    }

    /*
     * Setup procedure
     */

    private void setup() {
        FloatingActionButton floatingActionButton = findViewById(R.id.add_chat_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTabDialog();
            }
        });

        mAccountManager = AccountManager.get(this);

        mSOChatIDs = mSharedPrefs.getStringSet("SOChatIDs", new HashSet<String>());
        mSEChatIDs = mSharedPrefs.getStringSet("SEChatIDs", new HashSet<String>());

        if(mSharedPrefs.getBoolean("isFirstRun", true))
        {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);

            finish();
        }

        else if (mAccountManager.getAccounts().length < 1)
        {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        else
        {
            if (mFragmentManager.findFragmentByTag("home") == null) mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            doFragmentStuff();
        }

        respondToNotificationClick();
        setupACBR();
    }

    @SuppressWarnings("StaticFieldLeak")
    private void setupACBR() {
        mAddChatReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final Bundle extras = intent.getExtras();
                if (extras != null) {
                    if (extras.containsKey("idSE")) {
                        mSEChatIDs.add(extras.getString("idSE"));
                        mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();
                        doFragmentStuff();
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                while (mSEChatUrls.get(Integer.decode(extras.getString("idSE"))) == null);
                                while (mFragmentManager.findFragmentByTag(mSEChatUrls.get(Integer.decode(extras.getString("idSE")))) == null);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                setFragmentByChatId(extras.getString("idSE"), "exchange");
                                super.onPostExecute(aVoid);
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else if (extras.containsKey("idSO")) {
                        mSOChatIDs.add(extras.getString("idSO"));
                        mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
                        doFragmentStuff();
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                while (mSOChatUrls.get(Integer.decode(extras.getString("idSO"))) == null);
                                while (mFragmentManager.findFragmentByTag(mSOChatUrls.get(Integer.decode(extras.getString("idSO")))) == null);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                setFragmentByChatId(extras.getString("idSO"), "overflow");
                                super.onPostExecute(aVoid);
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("idAdd");

        LocalBroadcastManager.getInstance(this).registerReceiver(mAddChatReceiver, intentFilter);
    }

    @SuppressLint("StaticFieldLeak")
    private void respondToNotificationClick() {
        if (getIntent().getExtras() != null) {
            Log.e("NOTIF", "NOTIF");
            final String chatId = mIntent.getExtras().getString("chatId");
            final String chatDomain = mIntent.getExtras().getString("chatDomain");

            if (chatId != null && chatDomain != null) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (chatDomain.contains("exchange")) //noinspection StatementWithEmptyBody
                            while (mSEChatUrls.get(Integer.decode(chatId)) == null);
                        else if (chatDomain.contains("overflow")) //noinspection StatementWithEmptyBody
                            while (mSOChatUrls.get(Integer.decode(chatId)) == null);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        setFragmentByChatId(chatId, chatDomain);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    private static List<Integer> sparseIntArrayAsList(SparseIntArray sparseIntArray) {
        if (sparseIntArray == null) return null;
        List<Integer> arrayList = new ArrayList<>(sparseIntArray.size());
        for (int i = 0; i < sparseIntArray.size(); i++)
            arrayList.add(sparseIntArray.valueAt(i));
        return arrayList;
    }

    private void doFragmentStuff() {
        mSEChatUrls = new SparseArray<>();
        mSOChatUrls = new SparseArray<>();
        mSEChatColors = new SparseIntArray();
        mSOChatColors = new SparseIntArray();
        mSEChatIcons = new SparseArray<>();
        mSOChatIcons = new SparseArray<>();
        mSEChatNames = new SparseArray<>();
        mSOChatNames = new SparseArray<>();
        mSEChats = new SparseArray<>();
        mSOChats = new SparseArray<>();
//        Looper.prepare();
        for (String s : mSEChatIDs) {
            Log.e("ID", s);
            final String chatUrl = "https://chat.stackexchange.com/rooms/".concat(s);
            final String id = s;
            mRequestFactory.get(chatUrl, true, new RequestFactory.Listener() {
                @Override
                public void onSucceeded(final URL url, String data) {
                    mSEChatUrls.put(Integer.decode(id), chatUrl);
                    mAddList = AddList.newInstance(mSharedPrefs, data, id, chatUrl, new AddListListener() {
                        @Override
                        public void onStart() {
                            mCanAddChat = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color) {
                            Fragment fragment = addFragment(chatUrl, name, color);
                            Log.e("RRR", fragment.getArguments().getString("chatUrl", "").concat("HUE"));
                            mSEChats.put(Integer.decode(id), fragment);
                            mSEChatColors.put(Integer.decode(id), color);
                            mSEChatIcons.put(Integer.decode(id), icon);
                            mSEChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish() {
                            mCanAddChat = true;
                            findViewById(R.id.loading_progress).setVisibility(View.GONE);
                            ArrayList<String> names = new ArrayList<>();
                            names.addAll(asList(mSEChatNames));
                            names.addAll(asList(mSOChatNames));

                            ArrayList<String> urls = new ArrayList<>();
                            urls.addAll(asList(mSEChatUrls));
                            urls.addAll(asList(mSOChatUrls));

                            ArrayList<Drawable> icons = new ArrayList<>();
                            icons.addAll(asList(mSEChatIcons));
                            icons.addAll(asList(mSOChatIcons));

                            ArrayList<Integer> colors = new ArrayList<>();
                            colors.addAll(sparseIntArrayAsList(mSEChatColors));
                            colors.addAll(sparseIntArrayAsList(mSOChatColors));

                            ArrayList<Fragment> fragments = new ArrayList<>();
                            fragments.addAll(asList(mSEChats));
                            fragments.addAll(asList(mSOChats));

                            addFragmentsToList(names, urls, icons, colors, fragments);
                            initiateCurrentFragments(fragments);
                        }
                    });

                    mAddList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message) {
                    Toast.makeText(MainActivity.this, "Failed to load chat ".concat(id).concat(": ").concat(message), Toast.LENGTH_LONG).show();
                    mSEChatIDs.remove(id);
                    mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();
                    Log.e("FAIL", message.concat(id));
                }
            });
        }

        for (String s : mSOChatIDs) {
            final String chatUrl = "https://chat.stackoverflow.com/rooms/".concat(s);
            final String id = s;
            mRequestFactory.get(chatUrl, true, new RequestFactory.Listener() {
                @Override
                public void onSucceeded(final URL url, String data) {
                    mSOChatUrls.put(Integer.decode(id), chatUrl);
                    AddList addList = AddList.newInstance(mSharedPrefs, data, id, chatUrl, new AddListListener() {
                        @Override
                        public void onStart() {
                            mCanAddChat = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color) {
                            Fragment fragment = addFragment(chatUrl, name, color);
                            mSOChats.put(Integer.decode(id), fragment);
                            mSOChatColors.put(Integer.decode(id), color);
                            mSOChatIcons.put(Integer.decode(id), icon);
                            mSOChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish() {
                            mCanAddChat = true;
                            findViewById(R.id.loading_progress).setVisibility(View.GONE);
                            ArrayList<String> names = new ArrayList<>();
                            names.addAll(asList(mSEChatNames));
                            names.addAll(asList(mSOChatNames));

                            ArrayList<String> urls = new ArrayList<>();
                            urls.addAll(asList(mSEChatUrls));
                            urls.addAll(asList(mSOChatUrls));

                            ArrayList<Drawable> icons = new ArrayList<>();
                            icons.addAll(asList(mSEChatIcons));
                            icons.addAll(asList(mSOChatIcons));

                            ArrayList<Integer> colors = new ArrayList<>();
                            colors.addAll(sparseIntArrayAsList(mSEChatColors));
                            colors.addAll(sparseIntArrayAsList(mSOChatColors));

                            ArrayList<Fragment> fragments = new ArrayList<>();
                            fragments.addAll(asList(mSEChats));
                            fragments.addAll(asList(mSOChats));

                            addFragmentsToList(names, urls, icons, colors, fragments);
                            initiateCurrentFragments(fragments);
                        }
                    });

                    addList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message) {
                    Toast.makeText(MainActivity.this, "Failed to load chat ".concat(id), Toast.LENGTH_SHORT).show();
                    mSOChatIDs.remove(id);
                    mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
                    Log.e("FAIL", message.concat(id));
                }
            });
        }

        if (mSEChatIDs.size() == 0 && mSOChatIDs.size() == 0) removeAllFragmentsFromList();

//        mAddListItemsFromURLList = AddListItemsFromURLList.newInstance(new AddItemsListener() {
//            @Override
//            public void onStart() {
//                mCanAddChat = false;
//            }
//
//            @Override
//            public void onProgressMade(String url, ArrayList<String> names, ArrayList<String> urls, ArrayList<Drawable> icons, ArrayList<Integer> colors, ArrayList<Fragment> fragments) {
//                fragments = addTab(url, names, urls, icons, colors, fragments);
//                if (fragments.size() > 0) {
//                    initiateCurrentFragments(fragments);
//                    addFragmentsToList(names, urls, icons, colors, fragments);
//                } else {
//                    removeAllFragmentsFromList();
//                }
//            }
//
//            @Override
//            public void onFinished() {
//                mCanAddChat = true;
//                findViewById(R.id.loading_progress).setVisibility(View.GONE);
//            }
//        });
//        mAddListItemsFromURLList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
    }

    private static class AddList extends AsyncTask<String, Void, Void> {
        private String mHtmlData;
        private String mChatId;
        private String mChatUrl;
        private AddListListener mAddListListener;
        private SharedPreferences mSharedPreferences;
        private String mName;
        private Drawable mIcon;
        private Integer mColor;

        static AddList newInstance(SharedPreferences sharedPreferences, String data, String id, String url, AddListListener addListListener) {
            return new AddList(sharedPreferences, data, id, url, addListListener);
        }

        AddList(SharedPreferences sharedPreferences, String data, String id, String url, AddListListener addListListener) {
            mSharedPreferences = sharedPreferences;
            mHtmlData = data;
            mChatId = id;
            mChatUrl = url;
            mAddListListener = addListListener;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAddListListener.onStart();
            mName = getName(mHtmlData, mChatUrl);
            mIcon = getIcon(mHtmlData, mChatUrl);
            mColor = new Utils().getColorInt(mainActivity, mChatUrl);

            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAddListListener.onFinish();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mAddListListener.onProgress(mName, mIcon, mColor);
            super.onProgressUpdate(values);
        }

        @Nullable
        private String getName(String html, String url) {
            try {
                Elements spans = Jsoup.parse(html).select("span");

                for (Element e : spans) {
                    if (e.hasAttr("id") && e.attr("id").equals("roomname")) {
                        mSharedPreferences.edit().putString(url + "Name", e.ownText()).apply();
                        return e.ownText();
                    }
                }
                String ret = Jsoup.connect(url).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
                mSharedPreferences.edit().putString(url + "Name", ret).apply();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Nullable
        private Drawable getIcon(String html, String chatUrl) {
            try {
                Document document = Jsoup.parse(html);
                Element head = document.head();
                Element link = head.select("link").first();

                String fav = link.attr("href");
                if (!fav.contains("http")) fav = "https:".concat(fav);
                URL url = new URL(fav);

                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                String FILENAME = "FAVICON_" + chatUrl.replace("/", "");
                FileOutputStream fos = mainActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Resources r = mainActivity.getResources();
                int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true));

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private interface AddListListener {
        void onStart();
        void onProgress(String name, Drawable icon, Integer color);
        void onFinish();
    }

    private void createUsersSlidingMenu()
    {
        // configure the SlidingMenu
        mCurrentUsers_SlidingMenu = new SlidingMenu(MainActivity.this);
        mCurrentUsers_SlidingMenu.setMode(SlidingMenu.RIGHT);
        //mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mCurrentUsers_SlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mCurrentUsers_SlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mCurrentUsers_SlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mCurrentUsers_SlidingMenu.setFadeDegree(0.35f);
        mCurrentUsers_SlidingMenu.attachToActivity(MainActivity.this, SlidingMenu.SLIDING_CONTENT);
        mCurrentUsers_SlidingMenu.setMenu(R.layout.users_slideout);
        mCurrentUsers_SlidingMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen()
            {
                if (getmChatroomSlidingMenu().isMenuShowing())
                {
                    getmChatroomSlidingMenu().showContent(true);
                }
            }
        });
        mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    public SlidingMenu getCurrentUsers_SlidingMenu()
    {
        return mCurrentUsers_SlidingMenu;
    }

    private void preSetup()
    {
        setBehindContentView(R.layout.chatroom_slideout);
        mChatroomSlidingMenu = getSlidingMenu();

        mChatroomSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mChatroomSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mChatroomSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mChatroomSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_chats_width);
        mChatroomSlidingMenu.setFadeDegree(0.35f);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();


        mHandler = new Handler();

//        Log.e("URLS", mChatUrls.toString());

        //mEditor.putInt("tabIndex", 0).apply();
        mFragmentManager = getSupportFragmentManager();

        mIntent = getIntent();
    }

    /*
     * Menu
     */

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
            default:
                setFragmentByTag("home");
                for (Fragment fragment : mFragmentManager.getFragments()) {
                    if (fragment != null && !fragment.isDetached() && fragment instanceof ChatFragment) if (((ChatFragment) fragment).getmSlidingMenu().isMenuShowing()) ((ChatFragment) fragment).getmSlidingMenu().showContent(true);
                }
                if (mChatroomSlidingMenu.isMenuShowing()) mChatroomSlidingMenu.showContent(true);
                break;
        }

        return true;
    }

    /*
     * Fragment Stuffs
     */

    private void initiateCurrentFragments(ArrayList<Fragment> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            try {
                Fragment fragment = fragments.get(i);
                String tag = fragment.getArguments().getString("chatUrl");
                if (mFragmentManager.findFragmentByTag(tag) == null) {
                    mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).detach(fragment).commit();
                }

                if ((mCurrentFragment == null || mCurrentFragment.equals("home")) && mFragmentManager.findFragmentByTag("home") == null) {
                    mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
                    //hueUtils.setActionBarColorToSharedPrefsValue(this);
//                    hueUtils.setAddChatFabColorDefault(this);
                }

                mFragmentManager.executePendingTransactions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addFragmentsToList(ArrayList<String> chatroomNames,
                                    ArrayList<String> chatUrls,
                                    ArrayList<Drawable> chatIcons,
                                    ArrayList<Integer> chatColors,
                                    ArrayList<Fragment> chatFragments) {

        String[] names = new String[chatroomNames.size()];
        names = chatroomNames.toArray(names);

        String[] urls = new String[chatUrls.size()];
        urls = chatUrls.toArray(urls);

        Drawable[] ico = new Drawable[chatIcons.size()];
        ico = chatIcons.toArray(ico);

        Integer[] colors = new Integer[chatColors.size()];
        colors = chatColors.toArray(colors);

        chatroomArrayAdapter = new ImgTextArrayAdapter(this, names, urls, ico, colors);
        if (names.length < 1) chatroomArrayAdapter.clear();
//        Log.e("LE", names.length + "");

        chatroomsList = findViewById(R.id.chatroomsListView);
        chatroomsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Here, you set the data in your ListView
        chatroomsList.setAdapter(chatroomArrayAdapter);

        chatroomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                chatroomsList.requestFocusFromTouch();
                chatroomsList.setSelection(position);
                chatroomsList.requestFocus();

                mCurrentFragment = chatroomArrayAdapter.getUrls()[position];

                if (mFragmentManager.findFragmentByTag("home").isDetached()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setFragmentByTag(chatroomArrayAdapter.getUrls()[position]);
                        }
                    }, 400);
                } else {
                    setFragmentByTag(chatroomArrayAdapter.getUrls()[position]);
                }


                getmChatroomSlidingMenu().toggle();
            }
        });
    }

    private void removeAllFragmentsFromList() {
        if (chatroomsList != null) chatroomsList.setAdapter(null);
    }


    private void setFragmentByChatId(String id, String domain) {
//        for (String url : mChatUrls) {
//            if (url.contains(domain) && url.contains(id)) {
//                setFragmentByTag(url);
//                break;
//            }
//        }

        Log.e("SETID", id.concat(domain));

        if (domain.contains("exchange")) {
            if (mSEChatUrls.get(Integer.decode(id)) != null) setFragmentByTag(mSEChatUrls.get(Integer.decode(id)));
            else Toast.makeText(this, "Chat not added", Toast.LENGTH_SHORT).show();
        } else if (domain.contains("overflow")) {
            if (mSOChatUrls.get(Integer.decode(id)) != null) setFragmentByTag(mSOChatUrls.get(Integer.decode(id)));
            else Toast.makeText(this, "Chat not added", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFragmentByTag(String tag)
    {
        Log.e("TAG", tag);
        if (mFragmentManager.getFragments() != null)
        {
            for (Fragment fragment : mFragmentManager.getFragments())
            {
                if (fragment != null && !fragment.isDetached())
                {
                    mFragmentManager.beginTransaction().detach(fragment).commit();
                }
            }
            Fragment fragToAttach = mFragmentManager.findFragmentByTag(tag);

            if(tag.equals("home"))
            {
                mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                //noinspection ConstantConditions
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                hueUtils.showAddChatFab(this, true);
                //hueUtils.setAddChatFabColorToSharedPrefsValue(this);
//                hueUtils.setActionBarColorDefault(this);
                mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                ((HomeFragment) fragToAttach).hueTest();
            }
            else
            {
                if (mFragmentManager.findFragmentByTag("home").isDetached()) {
                    mFragmentManager.beginTransaction().attach(fragToAttach).commit();
                } else {
                    mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                }
                mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                //noinspection ConstantConditions
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(VectorDrawableCompat.create(getResources(), R.drawable.ic_home_white_24dp, null));
//                hueUtils.showAddChatFab(this, false);
                ((ChatFragment) fragToAttach).hueTest();
            }
        }
    }

    /*
     * Other Stuffs
     */

    private void showAddTabDialog() {
        if (mCanAddChat) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getText(R.string.activity_main_add_chat));

            View view = View.inflate(this, R.layout.add_chat_dialog, null);
            final EditText input = view.findViewById(R.id.url_edittext);

            final Spinner domains = view.findViewById(R.id.domain_spinner);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.domain_spinner, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            domains.setAdapter(adapter);

            domains.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.enter_full_url))) {
                        input.setHint(getResources().getText(R.string.activity_main_chat_full_url_hint));
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                    } else {
                        input.setHint(getResources().getText(R.string.activity_main_chat_url_hint));
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            builder.setView(view);
            builder.setPositiveButton(getResources().getText(R.string.generic_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputText = input.getText().toString();
//                    String url;

                    if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackoverflow).toString())) {
//                        url = getResources().getText(R.string.stackoverflow).toString().concat("rooms/").concat(inputText);
                        mSOChatIDs.add(inputText);
                    } else //noinspection StatementWithEmptyBody
                        if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackexchange).toString())) {
//                        url = getResources().getText(R.string.stackexchange).toString().concat("rooms/").concat(inputText);
                        mSEChatIDs.add(inputText);
                    } else {
//                        url = inputText;
                    }

                    mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
                    mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();

//                    mChatUrls.add(url);
//                    mEditor.putStringSet(CHAT_URLS_KEY, mChatUrls);
//                    mEditor.apply();
//                    Log.e("URLSA", mChatUrls.toString());
                    doFragmentStuff();
                }
            });
            builder.setNegativeButton(getResources().getText(R.string.generic_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog al = builder.create();
            al.show();
        } else {
            Toast.makeText(this, getResources().getText(R.string.cant_add_chat), Toast.LENGTH_LONG).show();
        }
    }

    public void confirmClose(View v) {
        if (chatroomsList.getSelectedItemPosition() != 0) {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            vib.vibrate(100);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getText(R.string.activity_main_delete_chat_title))
                            .setMessage(getResources().getText(R.string.activity_main_delete_chat_message))
                            .setPositiveButton(getResources().getText(R.string.generic_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Fragment remFrag = mFragmentManager.findFragmentByTag(mCurrentFragment);
                                    String domain = "";
                                    String id = "";

                                    Pattern domP = Pattern.compile("//(.+?)\\.com");
                                    Matcher domM = domP.matcher(mCurrentFragment);

                                    while (!domM.hitEnd()) {
                                        if (domM.find()) {
                                            domain = domM.group();
                                        }
                                    }

                                    Pattern idP = Pattern.compile("rooms/(.+?)\\b");
                                    Matcher idM = idP.matcher(mCurrentFragment);

                                    while (!idM.hitEnd()) {
                                        if (idM.find()) {
                                            id = idM.group().replace("rooms/", "");
                                        }
                                    }

                                    Log.e("IDDDDD", id);
                                    Log.e("DOMAIN", domain);

                                    if (!domain.isEmpty() && !id.isEmpty()) {
                                        if (domain.contains("overflow")) {
                                            mSOChatIDs.remove(id);
                                        } else if (domain.contains("exchange")) {
                                            mSEChatIDs.remove(id);
                                        }

                                        mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
                                        mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();

                                        setFragmentByTag("home");
                                        doFragmentStuff();
                                    }

//                                    mChatUrls.remove(remFrag.getTag());
//                                    Log.e("TAG", remFrag.getTag());
//                                    mEditor.putStringSet(CHAT_URLS_KEY, mChatUrls);
//                                    mEditor.apply();
//                                    Log.e("URLSR", mChatUrls.toString());
                                }
                            })
                            .setNegativeButton(getResources().getText(R.string.generic_no), null)
                            .show();
                }
            });
        }
    }

    private SlidingMenu getmChatroomSlidingMenu() {
        return mChatroomSlidingMenu;
    }

    private Fragment addFragment(String url, String name, Integer color) {
        Fragment fragment;
        if (mFragmentManager.findFragmentByTag(url) != null) {
            fragment = mFragmentManager.findFragmentByTag(url);
        } else {
            fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("chatTitle", name);
            args.putString("chatUrl", url);
            args.putInt("chatColor", color);

            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig) ;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFragmentByTag("home");
            }
        }, 400);
        return true;
    }

    public void toggleChatsSlide(View v) {
        mChatroomSlidingMenu.toggle();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAddChatReceiver);
        super.onDestroy();
    }
}

package com.huetoyou.chatexchange.ui.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.auth.Authenticator;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.frags.HomeFragment;
import com.huetoyou.chatexchange.ui.frags.ChatFragment;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.ui.misc.ChatroomRecyclerObject;
import com.huetoyou.chatexchange.ui.misc.CustomWebView;
import com.huetoyou.chatexchange.ui.misc.RecyclerAdapter;
import com.huetoyou.chatexchange.ui.misc.TutorialStuff;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends SlidingActivity
{

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private SlidingMenu mChatroomSlidingMenu;
    private RecyclerView chatroomsList;
    private static SlidingMenu mCurrentUsers_SlidingMenu;
    private static FragmentManager mFragmentManager;

    private BroadcastReceiver mAddChatReceiver;

    private Intent mIntent;

    private boolean oncreatejustcalled = false;

    private Handler mHandler;

    private String mCurrentFragment;

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

    private Set<String> mSOChatIDs = new HashSet<>(0);
    private Set<String> mSEChatIDs = new HashSet<>(0);

    private String mCookieString = null;

    private MainActivityUtils.AddList mAddList;

    private VectorDrawableCompat drawable;
    private Toolbar mActionBar;
    private AppCompatImageButton mDrawerButton;

    private final AnimatorSet mOpenAnimSet = new AnimatorSet();
    private final AnimatorSet mCloseAnimSet = new AnimatorSet();
    private RecyclerView.Adapter mAdapter;
    private RecyclerAdapter.OnItemClicked mItemClickedListener;
    private ActionMenuView mActionMenuView;
    private RecyclerAdapter mWrappedAdapter;
    private RecyclerViewSwipeManager mSwipeManager;

    /*
     * Activity Lifecycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(this);
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics()); //TODO: Remember to uncomment this for production
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        preSetup();
        createUsersSlidingMenu();
        setup();

        mItemClickedListener = new RecyclerAdapter.OnItemClicked()
        {
            @Override
            public void onClick(View view, int position)
            {
                Log.e("CLICKED", position + "");

                mCurrentFragment = mWrappedAdapter.getUrlAt(position);
                doCloseAnimationForDrawerToggle(mDrawerButton);
                getmChatroomSlidingMenu().toggle();

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setFragmentByTag(mCurrentFragment);
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms));
            }

            @Override
            public void onCloseClick(View view, int position)
            {
                confirmClose(position);
            }
        };

        chatroomsList = findViewById(R.id.chatroomsListView);

        mSwipeManager = new RecyclerViewSwipeManager();

        mWrappedAdapter = new RecyclerAdapter(this, mItemClickedListener, mSwipeManager);
        mAdapter = mSwipeManager.createWrappedAdapter(mWrappedAdapter);

        chatroomsList.setAdapter(mAdapter);

        // disable change animations
        ((SimpleItemAnimator) chatroomsList.getItemAnimator()).setSupportsChangeAnimations(false);

        mSwipeManager.attachRecyclerView(chatroomsList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chatroomsList.getContext(),
                DividerItemDecoration.VERTICAL);
        chatroomsList.addItemDecoration(dividerItemDecoration);

//        ItemTouchHelper.Callback callback = new HueRecyclerViewSwipeHelperHue(mAdapter);
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(chatroomsList);

        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_black_24dp, null);
        drawable.setTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        final FloatingActionMenu fam = findViewById(R.id.chat_slide_menu);
        fam.hideMenuButton(false);

        mActionBar = (Toolbar) Utils.getActionBar(getWindow().getDecorView());
        assert mActionBar != null;

        Log.e("ACTIONBAR", mActionBar.getClass().toString());

//        mActionBar.removeViewAt(1);
//        mActionBar.addView(newDrawer, 1);

        mDrawerButton = (AppCompatImageButton) mActionBar.getChildAt(1);

        ObjectAnimator closeAnimator = ObjectAnimator.ofFloat(
                mDrawerButton,
                "rotation",
                180f,
                0f);

        mCloseAnimSet.play(closeAnimator);
        mCloseAnimSet.setInterpolator(new AnticipateInterpolator());
        mCloseAnimSet.setDuration(getResources().getInteger(R.integer.animation_duration_ms));

        ObjectAnimator openAnimator = ObjectAnimator.ofFloat(
                mDrawerButton,
                "rotation",
                -180f,
                0f);

        mOpenAnimSet.play(openAnimator);
        mOpenAnimSet.setInterpolator(new OvershootInterpolator());
        mOpenAnimSet.setDuration(getResources().getInteger(R.integer.animation_duration_ms));

        mDrawerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.e("CLICKED", "CLICKED");

                if (mChatroomSlidingMenu.isMenuShowing())
                {
                    doCloseAnimationForDrawerToggle(view);
                }
                else
                {
                    doOpenAnimationForDrawerToggle(view);
                }
                onSupportNavigateUp();
            }
        });

        mChatroomSlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener()
        {
            @Override
            public void onClose()
            {
                fam.close(false);
                fam.hideMenuButton(false);
            }
        });

        mChatroomSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener()
        {
            @Override
            public void onOpen()
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        fam.showMenuButton(true);
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms) - 400);
                TutorialStuff.showChatSliderTutorial_MainActivity(MainActivity.this);
            }
        });

        Log.e("FEATURE", String.valueOf(getWindow().hasFeature(Window.FEATURE_OPTIONS_PANEL)));
        mActionMenuView = (ActionMenuView) mActionBar.getChildAt(2);

        oncreatejustcalled = true;

        //forces options menu overflow icon to show on devices with physical menu keys
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            // presumably, not relevant
        }

    }

    public void openOptionsMenu(View v) {
        openOptionsMenu();
    }

    @Override
    public void openOptionsMenu()
    {
//        mActionMenuView.showOverflowMenu();
        mActionBar.showOverflowMenu();
        super.openOptionsMenu();
    }

    @Override
    protected void onResume()
    {

        ThemeHue.setThemeOnResume(MainActivity.this, oncreatejustcalled);

        if (oncreatejustcalled)
        {
            oncreatejustcalled = false;
        }

//        doFragmentStuff();
        super.onResume();

        System.out.println("Hellu!");

        mIntent = getIntent();
        respondToNotificationClick();
    }

    @Override
    public void onBackPressed()
    {
        if (mFragmentManager.findFragmentByTag("home").isDetached())
        {
            setFragmentByTag("home");
            for (Fragment fragment : mFragmentManager.getFragments())
            {
                if (fragment != null && !fragment.isDetached() && fragment instanceof ChatFragment)
                {
                    if (((ChatFragment) fragment).getmSlidingMenu().isMenuShowing())
                    {
                        ((ChatFragment) fragment).getmSlidingMenu().hideMenu(true);
                    }
                }
            }
            if (mChatroomSlidingMenu.isMenuShowing())
            {
                mChatroomSlidingMenu.hideMenu(true);
            }
        }
        else if (mChatroomSlidingMenu.isMenuShowing())
        {
            mChatroomSlidingMenu.hideMenu(true);
        }
        else
        {
            super.onBackPressed();
        }
    }

    /*
     * Setup procedure
     */

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
        mFragmentManager = getSupportFragmentManager();

        mIntent = getIntent();
    }

    private void setup()
    {
        final FloatingActionMenu fam = findViewById(R.id.chat_slide_menu);

        FloatingActionButton floatingActionButton = findViewById(R.id.add_chat_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAddTabDialog();
                fam.close(true);
            }
        });

        FloatingActionButton fab = findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("POS", "DEFL");
                        setFragmentByTag("home");
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms));

                doCloseAnimationForDrawerToggle(mDrawerButton);
                mChatroomSlidingMenu.toggle();
                fam.close(false);
            }
        });
        mRequestFactory = new RequestFactory();

        mAccountManager = AccountManager.get(this);

        AccountManagerCallback<Bundle> accountManagerCallback = new AccountManagerCallback<Bundle>()
        {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture)
            {
                Log.e("AUtH", "AAA");
                String authToken = "";
                try
                {
                    authToken = accountManagerFuture.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    Log.e("Auth", authToken);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("RI", "P");
                }
                mRequestFactory = new RequestFactory(authToken);
                mCookieString = authToken;
                doFragmentStuff();
            }
        };

        Set<String> seChatsTemp = mSharedPrefs.getStringSet("SEChatIDs", new HashSet<String>());
        Set<String> soChatsTemp = mSharedPrefs.getStringSet("SOChatIDs", new HashSet<String>());

        mSOChatIDs = new HashSet<>(soChatsTemp);
        mSEChatIDs = new HashSet<>(seChatsTemp);

        if (mSharedPrefs.getBoolean("isFirstRun", true))
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
            if (mFragmentManager.findFragmentByTag("home") == null)
            {
                mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            }
            mAccountManager.getAuthToken(mAccountManager.getAccounts()[0], Authenticator.ACCOUNT_TYPE, null, true, accountManagerCallback, null);
        }

        respondToNotificationClick();
        setupACBR();
    }

    private void doCloseAnimationForDrawerToggle(View view)
    {
        mOpenAnimSet.cancel();
        mCloseAnimSet.start();
    }

    private void doOpenAnimationForDrawerToggle(View view)
    {
        mCloseAnimSet.cancel();
        mOpenAnimSet.start();
    }

    /**
     * BroadcastReceiver listening for click on chat URL from WebViewActivity
     *
     * @see CustomWebView#client()
     */

    private void setupACBR()
    {
        mAddChatReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                final Bundle extras = intent.getExtras();
                if (extras != null)
                {
                    if (extras.containsKey("idSE"))
                    {
                        addIdToSEList(extras.getString("idSE"));
                        doFragmentStuff();

                        ReceiveACB.newInstance(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mSEChatUrls.get(Integer.decode(extras.getString("idSE"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mFragmentManager.findFragmentByTag(mSEChatUrls.get(Integer.decode(extras.getString("idSE")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    setFragmentByChatId(extras.getString("idSE"), "exchange");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    mCurrentUsers_SlidingMenu.toggle();
                                }
                            }
                        }, "idSE").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                    else if (extras.containsKey("idSO"))
                    {
                        addIdToSOList(extras.getString("idSO"));
                        doFragmentStuff();

                        ReceiveACB.newInstance(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mSOChatUrls.get(Integer.decode(extras.getString("idSO"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mFragmentManager.findFragmentByTag(mSOChatUrls.get(Integer.decode(extras.getString("idSO")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    setFragmentByChatId(extras.getString("idSO"), "overflow");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    mCurrentUsers_SlidingMenu.toggle();
                                }
                            }
                        }, "idSO").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("idAdd");

        LocalBroadcastManager.getInstance(this).registerReceiver(mAddChatReceiver, intentFilter);
    }

    static private class ReceiveACB extends AsyncTask<Void, Void, Void>
    {
        final ACBInterface mInterface;
        final String mKey;

        static ReceiveACB newInstance(ACBInterface acbInterface, String key)
        {
            return new ReceiveACB(acbInterface, key);
        }

        ReceiveACB(ACBInterface acbInterface, String key)
        {
            mInterface = acbInterface;
            mKey = key;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            while (true)
            {
                if (!mInterface.urlFound())
                {
                    continue;
                }
                if (!mInterface.fragmentFound())
                {
                    continue;
                }
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mInterface.onFinish();
            super.onPostExecute(aVoid);
        }
    }

    private interface ACBInterface
    {
        boolean urlFound();

        boolean fragmentFound();

        void onFinish();
    }

    /**
     * If Firebase notification comes with data, and that data is room info, open the room if added
     */

    private void respondToNotificationClick()
    {
        if (getIntent().getExtras() != null)
        {
            Log.e("NOTIF", "NOTIF");
            final String chatId = mIntent.getExtras().getString("chatId");
            final String chatDomain = mIntent.getExtras().getString("chatDomain");

            if (chatId != null && chatDomain != null)
            {
                NotificationHandler.newInstance(new NHInterface()
                {
                    @Override
                    public boolean seContainsId()
                    {
                        return mSEChatUrls.get(Integer.decode(chatId)) != null;
                    }

                    @Override
                    public boolean soContainsId()
                    {
                        return mSOChatUrls.get(Integer.decode(chatId)) != null;
                    }

                    @Override
                    public void onFinish()
                    {
                        setFragmentByChatId(chatId, chatDomain);
                    }
                }, chatDomain).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private static class NotificationHandler extends AsyncTask<Void, Void, Void>
    {
        final NHInterface mInterface;
        final String mKey;

        static NotificationHandler newInstance(NHInterface nhInterface, String key)
        {
            return new NotificationHandler(nhInterface, key);
        }

        NotificationHandler(NHInterface nhInterface, String key)
        {
            mInterface = nhInterface;
            mKey = key;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if (mKey.contains("overflow"))
            {
                while (true)
                {
                    if (!mInterface.soContainsId())
                    {
                        continue;
                    }
                    break;
                }
            }
            else if (mKey.contains("exchange"))
            {
                while (true)
                {
                    if (!mInterface.seContainsId())
                    {
                        continue;
                    }
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mInterface.onFinish();
            super.onPostExecute(aVoid);
        }
    }

    private interface NHInterface
    {
        boolean seContainsId();

        boolean soContainsId();

        void onFinish();
    }

    /**
     * Needed to convert some SparseArrays to ArrayLists
     *
     * @param sparseArray the SparseArray to be converted
     * @param <C>         dummy class for compatibility or something
     * @return returns the resulting ArrayList
     */

    private static <C> List<C> asList(SparseArray<C> sparseArray)
    {
        if (sparseArray == null)
        {
            return null;
        }
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    /**
     * Same as {@link MainActivity#asList(SparseArray)} but for SparseIntArray
     *
     * @param sparseIntArray Array to be converted
     * @return returns resulting ArrayList
     */

    private static List<Integer> sparseIntArrayAsList(SparseIntArray sparseIntArray)
    {
        if (sparseIntArray == null)
        {
            return null;
        }
        List<Integer> arrayList = new ArrayList<>(sparseIntArray.size());
        for (int i = 0; i < sparseIntArray.size(); i++)
            arrayList.add(sparseIntArray.valueAt(i));
        return arrayList;
    }

    /*
     * Setup fragments
     */

    /**
     * Instantiate fragments and add them to {@link MainActivity#mChatroomSlidingMenu}
     */

    private void doFragmentStuff()
    {
        resetArrays(false);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
            }
        });
//        Looper.prepare();
        Log.e("IDS", mSEChatIDs.toString().concat(mSOChatIDs.toString()));

        for (String s : mSEChatIDs)
        {
            Log.e("ID", s);
            final String chatUrl = "https://chat.stackexchange.com/rooms/".concat(s);
            final String id = s;
            mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
            {
                @Override
                public void onSucceeded(final URL url, String data)
                {
                    mSEChatUrls.put(Integer.decode(id), chatUrl);
                    mAddList = MainActivityUtils.AddList.newInstance(MainActivity.this, mSharedPrefs, data, id, chatUrl, new AddListListener()
                    {

                        private Fragment fragment;

                        @Override
                        public void onStart()
                        {

                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color)
                        {
                            fragment = addFragment(chatUrl, name, color, Integer.decode(id));
                            Log.e("RRR", fragment.getArguments().getString("chatUrl", "").concat("HUE"));
                            mSEChats.put(Integer.decode(id), fragment);
                            mSEChatColors.put(Integer.decode(id), color);
                            mSEChatIcons.put(Integer.decode(id), icon);
                            mSEChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish(String name, String url, Drawable icon, Integer color)
                        {
                            addFragmentToList(name, url, icon, color, id);
                            initiateFragment(fragment);
                        }
                    });

                    mAddList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message)
                {
                    Toast.makeText(MainActivity.this, "Failed to load chat ".concat(id).concat(": ").concat(message), Toast.LENGTH_LONG).show();

                    removeIdFromSEList(id);
                    Log.e("Couldn't load SE chat ".concat(id), message);
                }
            });
        }

        for (String s : mSOChatIDs)
        {
            final String chatUrl = "https://chat.stackoverflow.com/rooms/".concat(s);
            final String id = s;
            mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
            {
                @Override
                public void onSucceeded(final URL url, String data)
                {
                    mSOChatUrls.put(Integer.decode(id), chatUrl);
                    MainActivityUtils.AddList addList = MainActivityUtils.AddList.newInstance(MainActivity.this, mSharedPrefs, data, id, chatUrl, new AddListListener()
                    {

                        private Fragment fragment;

                        @Override
                        public void onStart()
                        {
                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color)
                        {
                            fragment = addFragment(chatUrl, name, color, Integer.decode(id));
                            mSOChats.put(Integer.decode(id), fragment);
                            mSOChatColors.put(Integer.decode(id), color);
                            mSOChatIcons.put(Integer.decode(id), icon);
                            mSOChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish(String name, String url, Drawable icon, Integer color)
                        {
                            addFragmentToList(name, url, icon, color, id);
                            initiateFragment(fragment);
                        }
                    });

                    addList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message)
                {
                    Toast.makeText(MainActivity.this, "Failed to load chat ".concat(id), Toast.LENGTH_SHORT).show();
                    removeIdFromSOList(id);
                    Log.e("Couldn't load SO chat ".concat(id), message);
                }
            });
        }

        if (mSEChatIDs.size() == 0 && mSOChatIDs.size() == 0)
        {
            removeAllFragmentsFromList();
            findViewById(R.id.loading_progress).setVisibility(View.GONE);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(350);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                while (true)
                {
                    if (chatroomsList == null)
                    {
                        continue;
                    }
                    if (mWrappedAdapter.getItemCount() < mSEChatIDs.size() + mSOChatIDs.size())
                    {
                        continue;
                    }
                    break;
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findViewById(R.id.loading_progress).setVisibility(View.GONE);
                        Log.e("VIS", "GONE");
                    }
                });
            }
        }).start();
    }

    /**
     * Handles the actual data parsing for chats
     * (in the background to avoid ANRs)
     */


    interface AddListListener
    {
        void onStart();

        void onProgress(String name, Drawable icon, Integer color);

        void onFinish(String name, String url, Drawable icon, Integer color);
    }

    /**
     * Setup current users {@link SlidingMenu}
     */

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
        mCurrentUsers_SlidingMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener()
        {
            @Override
            public void onOpen()
            {
                if (getmChatroomSlidingMenu().isMenuShowing())
                {
                    getmChatroomSlidingMenu().hideMenu(true);
                }
            }
        });
        mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    /**
     * Get the sliding menu from another class
     *
     * @return returns the current users {@link SlidingMenu}
     */

    public SlidingMenu getCurrentUsers_SlidingMenu()
    {
        return mCurrentUsers_SlidingMenu;
    }

    /*
     * Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        TutorialStuff.homeFragTutorial(this);

        return true;
    }

    /**
     * @see MainActivity#onCreate(Bundle) for drawer toggle!
     */

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
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
            case R.id.explore_chats:
                Intent exploreintent = new Intent(getApplicationContext(), ChatroomsExplorationActivity.class);
                startActivity(exploreintent);
            default:
                break;
        }

        return true;
    }

    /*
     * Fragment Stuffs
     */

    /**
     * Add specified fragment to the {@link FragmentManager}
     *
     * @param fragment Fragment to be added
     */

    private void initiateFragment(Fragment fragment) {
        try
        {
            String tag = fragment.getArguments().getString("chatUrl");
            if (mFragmentManager.findFragmentByTag(tag) == null)
            {
                mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).detach(fragment).commit();
            }

            if ((mCurrentFragment == null || mCurrentFragment.equals("home")) && mFragmentManager.findFragmentByTag("home") == null)
            {
                mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            }

            mFragmentManager.executePendingTransactions();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add fragment info to the RecyclerView list
     * @param name Chat name
     * @param url Chat URL
     * @param icon Chat favicon
     * @param color Chat color
     */

    private void addFragmentToList(String name, String url, Drawable icon, Integer color, String id) {
        Log.e("ADD", "ADD");
        int identifier;

        if (url.contains("overflow")) identifier = -Integer.decode(id);
        else identifier = Integer.decode(id);

        mWrappedAdapter.addItem(new ChatroomRecyclerObject(
                mWrappedAdapter.getItemCount(),
                name,
                url,
                icon,
                color,
                0,
                identifier
        ));
    }

    /**
     * Might be useful for a batch removal later, but right now, it just enables removal of the only chat added
     */

    private void removeAllFragmentsFromList()
    {
        if (chatroomsList != null)
        {
//            mAdapter = new RecyclerAdapter(this, mItemClickedListener);
//            chatroomsList.setAdapter(mAdapter);
            for (int i = 0; i < mWrappedAdapter.getItemCount(); i++) {
                mWrappedAdapter.removeItem(i);
            }
        }
        resetArrays(true);
    }

    /**
     * Open a chat by the specified ID
     *
     * @param id     the ID of the desired chat
     * @param domain the domain of the desired chat ("exchange" or "overflow")
     */

    private void setFragmentByChatId(String id, String domain)
    {
        Log.e("SETID", id.concat(domain));

        if (domain.contains("exchange"))
        {
            if (mSEChatUrls.get(Integer.decode(id)) != null)
            {
                setFragmentByTag(mSEChatUrls.get(Integer.decode(id)));
            }
            else
            {
                Toast.makeText(this, "Chat not added", Toast.LENGTH_SHORT).show();
            }
        }
        else if (domain.contains("overflow"))
        {
            if (mSOChatUrls.get(Integer.decode(id)) != null)
            {
                setFragmentByTag(mSOChatUrls.get(Integer.decode(id)));
            }
            else
            {
                Toast.makeText(this, "Chat not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Open a chat using its tag
     *
     * @param tag the chat's fragment tag (should be its URL)
     */

    public static void setFragmentByTag(String tag)
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

            if (fragToAttach != null)
            {

                if (tag.equals("home"))
                {
                    mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                    mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    ((HomeFragment) fragToAttach).hueTest();
                }
                else
                {
                    if (mFragmentManager.findFragmentByTag("home").isDetached())
                    {
                        mFragmentManager.beginTransaction().attach(fragToAttach).commit();
                    }
                    else
                    {
                        mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                    }
                    mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }
            }
            else
            {
                Log.e("TAG", tag);
            }

        }
    }

    /*
     * Other Stuffs
     */

    /**
     * Handle adding chats
     */

    private void showAddTabDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.activity_main_add_chat));

        View view = View.inflate(this, R.layout.add_chat_dialog, null);
        final EditText input = view.findViewById(R.id.url_edittext);

        final Spinner domains = view.findViewById(R.id.domain_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.domain_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        domains.setAdapter(adapter);

        domains.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                input.setHint(getResources().getText(R.string.activity_main_chat_url_hint));
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        builder.setView(view);
        builder.setPositiveButton(getResources().getText(R.string.generic_ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                String inputText = input.getText().toString();
                if (!inputText.isEmpty())
                {

                    if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackoverflow).toString()))
                    {
                        addIdToSOList(inputText);
                    }
                    else if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackexchange).toString()))
                    {
                        addIdToSEList(inputText);
                    }

                    doFragmentStuff();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please enter an ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(getResources().getText(R.string.generic_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }

    /**
     * Handle removing a chat
     *
     * @param position the position of the item in the chat list
     */

    public void confirmClose(final int position)
    {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        vib.vibrate(100);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getText(R.string.activity_main_delete_chat_title))
                        .setMessage(getResources().getText(R.string.activity_main_delete_chat_message))
                        .setPositiveButton(getResources().getText(R.string.generic_yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String domain = "";
                                String id = "";

                                Pattern domP = Pattern.compile("//(.+?)\\.com");
                                Matcher domM = domP.matcher(mWrappedAdapter.getUrlAt(position));

                                while (!domM.hitEnd())
                                {
                                    if (domM.find())
                                    {
                                        domain = domM.group();
                                    }
                                }

                                Pattern idP = Pattern.compile("rooms/(.+?)\\b");
                                Matcher idM = idP.matcher(mWrappedAdapter.getUrlAt(position));

                                while (!idM.hitEnd())
                                {
                                    if (idM.find())
                                    {
                                        id = idM.group().replace("rooms/", "");
                                    }
                                }

                                Log.e("IDDDDD", id);
                                Log.e("DOMAIN", domain);

                                if (!domain.isEmpty() && !id.isEmpty())
                                {
                                    if (domain.contains("overflow"))
                                    {
                                        removeIdFromSOList(id);
                                    }
                                    else if (domain.contains("exchange"))
                                    {
                                        removeIdFromSEList(id);
                                    }

                                    mFragmentManager.getFragments().remove(mFragmentManager.findFragmentByTag(mWrappedAdapter.getUrlAt(position)));

                                    if (mWrappedAdapter.getUrlAt(position).equals(mCurrentFragment)) setFragmentByTag("home");
                                    mWrappedAdapter.getSwipeManager().performFakeSwipe(mWrappedAdapter.getViewHolderAt(position), 1);
                                    mWrappedAdapter.removeItemWithSnackbar(MainActivity.this, position);
                                }
                            }
                        })
                        .setNegativeButton(getResources().getText(R.string.generic_no), null)
                        .show();
            }
        });

    }

    /**
     * Get the chatroom list {@link SlidingMenu} instance from other classes
     *
     * @return returns the chatroom SlidingMenu
     */

    public SlidingMenu getmChatroomSlidingMenu()
    {
        return mChatroomSlidingMenu;
    }

    /**
     * Instantiate/create the appropriate chat fragment, if necessary
     *
     * @param url   URL of chat
     * @param name  Name of chat
     * @param color Accent color of chat
     * @param id    ID of chat
     * @return the created Fragment
     */

    private Fragment addFragment(String url, String name, Integer color, Integer id)
    {
        Fragment fragment;
        if (mFragmentManager.findFragmentByTag(url) != null)
        {
            fragment = mFragmentManager.findFragmentByTag(url);
        }
        else
        {
            fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("chatTitle", name);
            args.putString("chatUrl", url);
            args.putInt("chatColor", color);
            args.putInt("chatId", id);

            fragment.setArguments(args);
        }

        return fragment;
    }

    /**
     * Handle user press of Home button in ActionBar
     *
     * @return true
     */

    @Override
    public boolean onSupportNavigateUp()
    {
        mChatroomSlidingMenu.toggle();
        return true;
    }

    /**
     * Open or close chatroom list
     *
     * @param v The View calling the method
     */

    public void toggleChatsSlide(View v)
    {
        mChatroomSlidingMenu.toggle();
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAddChatReceiver);
        super.onDestroy();
    }

    /**
     * Empty all specified arrays related to chats
     *
     * @param shouldEmptyIDs should the ID Set be emptied?
     */

    private void resetArrays(boolean shouldEmptyIDs)
    {
        if (shouldEmptyIDs)
        {
            mSEChatIDs = new HashSet<>(0);
            mSOChatIDs = new HashSet<>(0);
            setSOStringSet();
            setSEStringSet();
        }

        mSEChatUrls = new SparseArray<>();
        mSOChatUrls = new SparseArray<>();
        mSEChats = new SparseArray<>();
        mSOChats = new SparseArray<>();
        mSEChatNames = new SparseArray<>();
        mSOChatNames = new SparseArray<>();
        mSEChatIcons = new SparseArray<>();
        mSOChatIcons = new SparseArray<>();
        mSEChatColors = new SparseIntArray();
        mSOChatColors = new SparseIntArray();
    }

    /**
     * Removes all chats on confirmation
     *
     * @param v the view calling this function
     */

    public void removeAllChats(View v)
    {
        final FloatingActionMenu fam = findViewById(R.id.chat_slide_menu);
        fam.close(true);

        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to remove all chats?")
                .setPositiveButton(getResources().getText(R.string.generic_yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        removeAllFragmentsFromList();
                        setFragmentByTag("home");
                    }
                })
                .setNegativeButton(getResources().getText(R.string.generic_no), null)
                .show();
    }

    /**
     * Get cookies from other classes
     *
     * @return the authToken/Cookie string of the current account
     */

    public String getCookieString()
    {
        return mCookieString;
    }

    public android.support.v7.widget.ActionMenuView getActionMenu() {
        return mActionMenuView;
    }

    private void removeIdFromSEList(String id) {
        mSEChatIDs.remove(id);
        setSEStringSet();
    }

    private void addIdToSEList(String id) {
        mSEChatIDs.add(id);
        setSEStringSet();
    }

    private void setSEStringSet() {
        mEditor.remove("SEChatIDs").apply();
        mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();
    }

    private void removeIdFromSOList(String id) {
        mSOChatIDs.remove(id);
        setSOStringSet();
    }

    private void addIdToSOList(String id) {
        mSOChatIDs.add(id);
        setSOStringSet();
    }

    private void setSOStringSet() {
        mEditor.remove("SOChatIDs").apply();
        mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
    }
}

package com.huetoyou.chatexchange.ui.activity.main;

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
import android.os.Looper;
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
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.auth.Authenticator;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.AboutActivity;
import com.huetoyou.chatexchange.ui.activity.ChatroomsExplorationActivity;
import com.huetoyou.chatexchange.ui.activity.HelpActivity;
import com.huetoyou.chatexchange.ui.activity.IntroActivity;
import com.huetoyou.chatexchange.ui.activity.PreferencesActivity;
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

    SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private SlidingMenu mChatroomSlidingMenu;
    RecyclerView chatroomsList;
    static SlidingMenu mCurrentUsers_SlidingMenu;
    static FragmentManager mFragmentManager;

    BroadcastReceiver mAddChatReceiver;

    Intent mIntent;

    private boolean oncreatejustcalled = false;

    private Handler mHandler;

    String mCurrentFragment;

    RequestFactory mRequestFactory;

    SparseArray<Fragment> mSOChats = new SparseArray<>();
    SparseArray<Fragment> mSEChats = new SparseArray<>();

    SparseIntArray mSOChatColors = new SparseIntArray();
    SparseIntArray mSEChatColors = new SparseIntArray();

    SparseArray<String> mSOChatNames = new SparseArray<>();
    SparseArray<String> mSEChatNames = new SparseArray<>();

    SparseArray<String> mSOChatUrls = new SparseArray<>();
    SparseArray<String> mSEChatUrls = new SparseArray<>();

    SparseArray<Drawable> mSOChatIcons = new SparseArray<>();
    SparseArray<Drawable> mSEChatIcons = new SparseArray<>();

    Set<String> mSOChatIDs = new HashSet<>(0);
    Set<String> mSEChatIDs = new HashSet<>(0);

    private String mCookieString = null;

    MainActivityUtils.AddList mAddList;

    private VectorDrawableCompat drawable;
    private Toolbar mActionBar;
    private AppCompatImageButton mDrawerButton;

    private final AnimatorSet mOpenAnimSet = new AnimatorSet();
    private final AnimatorSet mCloseAnimSet = new AnimatorSet();
    private RecyclerView.Adapter mAdapter;
    private RecyclerAdapter.OnItemClicked mItemClickedListener;
    private ActionMenuView mActionMenuView;
    RecyclerAdapter mWrappedAdapter;
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

                mCurrentFragment = mWrappedAdapter.getItemAt(position).getUrl();
                //doCloseAnimationForDrawerToggle(mDrawerButton);
                getmChatroomSlidingMenu().toggle();

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FragStuff.setFragmentByTag(MainActivity.this, mCurrentFragment);
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms));
            }

            @Override
            public void onCloseClick(View view, int position)
            {
                MainActivityUtils.confirmClose(MainActivity.this, position);
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
                    //doCloseAnimationForDrawerToggle(view);
                }
                else
                {
                    //doOpenAnimationForDrawerToggle(view);
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
                doCloseAnimationForDrawerToggle(mDrawerButton);
            }
        });

        mChatroomSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener()
        {
            @Override
            public void onOpen()
            {
                doOpenAnimationForDrawerToggle(mDrawerButton);
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        fam.showMenuButton(true);
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms) - 400);
            }
        });

        mChatroomSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener()
        {
            @Override
            public void onOpened()
            {
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
        MainActivityUtils.respondToNotificationClick(MainActivity.this);
    }

    @Override
    public void onBackPressed()
    {
        if (mFragmentManager.findFragmentByTag("home").isDetached())
        {
            FragStuff.setFragmentByTag(MainActivity.this, "home");
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
                        FragStuff.setFragmentByTag(MainActivity.this,"home");
                    }
                }, getResources().getInteger(R.integer.animation_duration_ms));

                //doCloseAnimationForDrawerToggle(mDrawerButton);
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

                Log.e("AUTHTOKEN", authToken);

                CookieSyncManager.createInstance(MainActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                if (authToken != null) {
                    cookieManager.removeSessionCookie();
                    cookieManager.setCookie("https://stackexchange.com", authToken);
                    CookieSyncManager.getInstance().sync();
                }

                FragStuff.doFragmentStuff(MainActivity.this);
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
        MainActivityUtils.respondToNotificationClick(MainActivity.this);
        MainActivityUtils.setupACBR(this);
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

    interface NHInterface
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
        mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mCurrentUsers_SlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener()
        {
            @Override
            public void onOpened()
            {
                TutorialStuff.showUsersTutorial(MainActivity.this);
            }
        });
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
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                while (true) {
                    if (mFragmentManager.findFragmentByTag("home") == null ||
                            mFragmentManager.findFragmentByTag("home").isDetached())
                        continue;
                    break;
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TutorialStuff.homeFragTutorial(MainActivity.this);
                    }
                });
            }
        }).start();

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

                    FragStuff.doFragmentStuff(MainActivity.this);
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

    Fragment addFragment(String url, String name, Integer color, Integer id)
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

    void resetArrays(boolean shouldEmptyIDs)
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

    void removeIdFromSEList(String id) {
        mSEChatIDs.remove(id);
        setSEStringSet();
    }

    void addIdToSEList(String id) {
        mSEChatIDs.add(id);
        setSEStringSet();
    }

    private void setSEStringSet() {
        mEditor.remove("SEChatIDs").apply();
        mEditor.putStringSet("SEChatIDs", mSEChatIDs).apply();
    }

    void removeIdFromSOList(String id) {
        mSOChatIDs.remove(id);
        setSOStringSet();
    }

    void addIdToSOList(String id) {
        mSOChatIDs.add(id);
        setSOStringSet();
    }

    private void setSOStringSet() {
        mEditor.remove("SOChatIDs").apply();
        mEditor.putStringSet("SOChatIDs", mSOChatIDs).apply();
    }
}

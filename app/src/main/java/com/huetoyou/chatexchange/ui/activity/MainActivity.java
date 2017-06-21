package com.huetoyou.chatexchange.ui.activity;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends SlidingActivity {

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private SlidingMenu mChatroomSlidingMenu;
    private ListView chatroomsList;
    private ImgTextArrayAdapter chatroomArrayAdapter;
    private SlidingMenu mCurrentUsers_SlidingMenu;
    private FragmentManager mFragmentManager;

    private Intent mIntent;

    private Set<String> mChatUrls = new HashSet<>();

    private boolean mUseDark;
    private boolean oncreatejustcalled = false;

    private final String CHAT_URLS_KEY = "URLS";

    private Handler mHandler;

    private Utils hueUtils = null;
    private ThemeHue themeHue = null;
    private ActionBarHue actionBarHue = null;
    private String mCurrentFragment;

    private boolean mCanAddChat = true;
    private AddListItemsFromURLList mAddListItemsFromURLList;

    /*
     * Activity Lifecycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        hueUtils = new Utils();
        themeHue = new ThemeHue();
        actionBarHue = new ActionBarHue();
        themeHue.setTheme(MainActivity.this);

        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        preSetup();
        createUsersSlidingMenu();
        setup();

        oncreatejustcalled = true;
    }

    @Override
    protected void onPause() {
        if (mAddListItemsFromURLList != null && mAddListItemsFromURLList.getStatus() == AsyncTask.Status.RUNNING) {
            mAddListItemsFromURLList.cancel(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        themeHue.setThemeOnResume(MainActivity.this, oncreatejustcalled);

        if(oncreatejustcalled)
        {
            oncreatejustcalled = false;
        }

        mAddListItemsFromURLList = new AddListItemsFromURLList();
        mAddListItemsFromURLList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
        super.onResume();

        System.out.println("Hellu!");

        if (mFragmentManager.findFragmentByTag("home").isDetached())
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(VectorDrawableCompat.create(getResources(), R.drawable.ic_home_white_24dp, null));
        }

        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

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
    protected void onDestroy() {
        if (mAddListItemsFromURLList != null && mAddListItemsFromURLList.getStatus() == AsyncTask.Status.RUNNING) {
            mAddListItemsFromURLList.cancel(true);
        }
        super.onDestroy();
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

    @SuppressLint("StaticFieldLeak")
    private void setup() {
        FloatingActionButton floatingActionButton = findViewById(R.id.add_chat_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTabDialog();
            }
        });

        mAccountManager = AccountManager.get(this);

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
            mAddListItemsFromURLList = new AddListItemsFromURLList();
            mAddListItemsFromURLList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
        }

        if (mIntent.getExtras() != null) {
            final String chatId = mIntent.getExtras().getString("chatId");
            final String chatDomain = mIntent.getExtras().getString("chatDomain");

            if (chatId != null && chatDomain != null) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        while (!mAddListItemsFromURLList.getStatus().equals(Status.FINISHED));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        setFragmentByChatId(chatId, chatDomain);
                    }
                }.execute();
            }
        }
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

        mChatUrls = mSharedPrefs.getStringSet(CHAT_URLS_KEY, new HashSet<String>());
//        Log.e("URLS", mChatUrls.toString());

        //mEditor.putInt("tabIndex", 0).apply();

        mUseDark = mSharedPrefs.getBoolean("isDarkMode", false);

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

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setFragmentByTag(chatroomArrayAdapter.getUrls()[position]);
                    }
                }, 400);

                getmChatroomSlidingMenu().toggle();
            }
        });
    }

    private void removeAllFragmentsFromList() {
        if (chatroomsList != null) chatroomsList.setAdapter(null);
    }


    private void setFragmentByChatId(String id, String domain) {
        for (String url : mChatUrls) {
            if (url.contains(domain) && url.contains(id)) {
                setFragmentByTag(url);
                break;
            }
        }
    }

    private void setFragmentByTag(String tag)
    {
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
            mFragmentManager.beginTransaction().attach(fragToAttach).commit();


            if(tag.equals("home"))
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                hueUtils.showAddChatFab(this, true);
                //hueUtils.setAddChatFabColorToSharedPrefsValue(this);
//                hueUtils.setActionBarColorDefault(this);
                mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                ((HomeFragment) fragToAttach).hueTest();
            }
            else
            {
                mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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

    public void showAddTabDialog() {
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
                    String url;


                    if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackoverflow).toString())) {
                        url = getResources().getText(R.string.stackoverflow).toString().concat("rooms/").concat(inputText);
                    } else if (domains.getSelectedItem().toString().equals(getResources().getText(R.string.stackexchange).toString())) {
                        url = getResources().getText(R.string.stackexchange).toString().concat("rooms/").concat(inputText);
                    } else {
                        url = inputText;
                    }

                    mChatUrls.add(url);
                    mEditor.putStringSet(CHAT_URLS_KEY, mChatUrls);
                    mEditor.apply();
//                    Log.e("URLSA", mChatUrls.toString());
                    mAddListItemsFromURLList = new AddListItemsFromURLList();
                    mAddListItemsFromURLList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
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
                                    mChatUrls.remove(remFrag.getTag());
//                                    Log.e("TAG", remFrag.getTag());
                                    setFragmentByTag("home");
                                    mEditor.putStringSet(CHAT_URLS_KEY, mChatUrls);
                                    mEditor.apply();
//                                    Log.e("URLSR", mChatUrls.toString());
                                    mAddListItemsFromURLList = new AddListItemsFromURLList();
                                    mAddListItemsFromURLList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
                                }
                            })
                            .setNegativeButton(getResources().getText(R.string.generic_no), null)
                            .show();
                }
            });
        }
    }

    public SlidingMenu getmChatroomSlidingMenu() {
        return mChatroomSlidingMenu;
    }

    private class AddListItemsFromURLList extends AsyncTask<Set, Void, Void> {
        private ArrayList<String> chatNames = new ArrayList<>();
        private ArrayList<String> chatUrls = new ArrayList<>();
        private ArrayList<Drawable> chatIcons = new ArrayList<>();
        private ArrayList<Integer> chatColors = new ArrayList<>();
        private ArrayList<Fragment> chatFragments = new ArrayList<>();
        private ProgressBar loading;

        @Override
        protected final Void doInBackground(Set... params) {
            mCanAddChat = false;
            chatNames = new ArrayList<>();
            chatUrls = new ArrayList<>();
            chatIcons = new ArrayList<>();
            chatColors = new ArrayList<>();
            chatFragments = new ArrayList<>();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading = mChatroomSlidingMenu.findViewById(R.id.loading_progress);
                    loading.setVisibility(View.VISIBLE);
                }
            });

            final Set urls = params[0];

            if (urls.size() < 1) {
                return null;
            } else {
                for (Object o : urls) {
                    addTab(o.toString());
                    publishProgress();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if (chatFragments.size() > 0) {
                initiateCurrentFragments(chatFragments);
                addFragmentsToList(chatNames, chatUrls, chatIcons, chatColors, chatFragments);
            } else {
                removeAllFragmentsFromList();
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCanAddChat = true;
            loading.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }

        private void addTab(String url) {
            String faviconKey = "FAVICON_" + url.replace("/", "");
            String nameKey = url + "Name";
            String colorKey = url + "Color";

            String name;
            Drawable icon;
            Integer color;

            name = mSharedPrefs.getString(nameKey, null);
            color = mSharedPrefs.getInt(colorKey, -1);

            try {
                FileInputStream fis = openFileInput(faviconKey);
                Bitmap bmp = BitmapFactory.decodeStream(fis);
                Resources r = getResources();
                int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

                icon = new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true));
            } catch (Exception e) {
                icon = getIcon(url);
            }

            if (name == null) name = getName(url);
            if (color == -1) color = hueUtils.getColorInt(MainActivity.this, url);

            chatNames.add(name);
            chatUrls.add(url);
            chatIcons.add(icon);
            chatColors.add(color);
            chatFragments.add(addFragment(url, name, color));
        }

        @Nullable
        private String getName(String url) {
            try {
                Elements spans = Jsoup.connect(url).get().select("span");

                for (Element e : spans) {
                    if (e.hasAttr("id") && e.attr("id").equals("roomname")) {
                        mSharedPrefs.edit().putString(url + "Name", e.ownText()).apply();
                        return e.ownText();
                    }
                }
                String ret = Jsoup.connect(url).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
                mSharedPrefs.edit().putString(url + "Name", ret).apply();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Nullable
        private Drawable getIcon(String chatUrl) {
            try {
                Document document = Jsoup.connect(chatUrl).get();
                Element head = document.head();
                Element link = head.select("link").first();

                String fav = link.attr("href");
                if (!fav.contains("http")) fav = "https:".concat(fav);
                URL url = new URL(fav);

                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                String FILENAME = "FAVICON_" + chatUrl.replace("/", "");
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Resources r = getResources();
                int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true));

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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

}

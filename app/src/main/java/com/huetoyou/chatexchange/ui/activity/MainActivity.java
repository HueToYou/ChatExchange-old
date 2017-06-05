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
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
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
import com.crashlytics.android.Crashlytics;
import com.huetoyou.chatexchange.ui.frags.HomeFragment;
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

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private SlidingMenu mChatroomSlidingMenu;
    private ListView chatroomsList;
    private ImgTextArrayAdapter chatroomArrayAdapter;

    private FragmentManager mFragmentManager;

    private Intent mIntent;

    private Set<String> mChatUrls = new HashSet<>();

    private boolean mUseDark;

    private final String CHAT_URLS_KEY = "URLS";

    private Handler mHandler;

    private HueUtils hueUtils = null;
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        hueUtils = new HueUtils();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();

        mHandler = new Handler();

        mChatUrls = mSharedPrefs.getStringSet(CHAT_URLS_KEY, new HashSet<String>());
        Log.e("URLS", mChatUrls.toString());

//        mEditor.putInt("tabIndex", 0).apply();

        mUseDark = mSharedPrefs.getBoolean("isDarkMode", false);

        mFragmentManager = getSupportFragmentManager();

        mIntent = getIntent();

        setupChatRoomMenu();
        setup();

        hueUtils.setActionBarColorDefault(this);
        hueUtils.setAddChatFabColorDefault(this);

        //ColorPickerDialog.newBuilder().setColor(color).show(activity);
    }

    private void setup() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_chat_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTabDialog();
            }
        });

        mAccountManager = AccountManager.get(this);

        if(mSharedPrefs.getBoolean("isFirstRun", true))
        {
            mEditor.putBoolean("isFirstRun", false);
            mEditor.apply();

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
            mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            new AddListItemsFromURLList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
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
            default:
                setFragmentByTag("home");
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showAddTabDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.activity_main_add_chat));

        View view = View.inflate(this, R.layout.add_chat_dialog, null);
        final EditText input = (EditText) view.findViewById(R.id.url_edittext);

        final Spinner domains = (Spinner) view.findViewById(R.id.domain_spinner);

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
                Log.e("URLSA", mChatUrls.toString());
                new AddListItemsFromURLList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
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
    }

    public void confirmClose(View v) {
        if (chatroomsList.getSelectedItemPosition() != 0) {
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
                                    Fragment remFrag = mFragmentManager.findFragmentByTag(mCurrentFragment);
                                    mChatUrls.remove(remFrag.getTag());
                                    Log.e("TAG", remFrag.getTag());
                                    setFragmentByTag("home");
                                    mEditor.putStringSet(CHAT_URLS_KEY, mChatUrls);
                                    mEditor.apply();
                                    Log.e("URLSR", mChatUrls.toString());
                                    new AddListItemsFromURLList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrls);
                                }
                            })
                            .setNegativeButton(getResources().getText(R.string.generic_no), null)
                            .show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mFragmentManager.findFragmentByTag("home").isDetached()) {
            hueUtils.setActionBarColorDefault(this);
            hueUtils.setAddChatFabColorDefault(this);
        } else if (!mSharedPrefs.getBoolean("dynamicallyColorBar", false)) {
            hueUtils.setActionBarColorDefault(this);
            hueUtils.setAddChatFabColorDefault(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.findFragmentByTag("home").isDetached()) {
            setFragmentByTag("home");
        } else {
            super.onBackPressed();
        }
    }

    public SlidingMenu getmChatroomSlidingMenu() {
        return mChatroomSlidingMenu;
    }

    private class AddListItemsFromURLList extends AsyncTask<Set<String>, Void, Void> {
        private ArrayList<String> chatNames = new ArrayList<>();
        private ArrayList<String> chatUrls = new ArrayList<>();
        private ArrayList<Drawable> chatIcons = new ArrayList<>();
        private ArrayList<Integer> chatColors = new ArrayList<>();
        private ArrayList<Fragment> chatFragments = new ArrayList<>();
        private ProgressBar loading;

        @Override
        protected Void doInBackground(Set<String>... params) {
            chatNames = new ArrayList<>();
            chatUrls = new ArrayList<>();
            chatIcons = new ArrayList<>();
            chatColors = new ArrayList<>();
            chatFragments = new ArrayList<>();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading = (ProgressBar) mChatroomSlidingMenu.findViewById(R.id.loading_progress);
                    loading.setVisibility(View.VISIBLE);
                }
            });

            Set<String> urls = params[0];

            if (urls.size() < 1) {
                publishProgress();
            }

            for (String s : urls) {
                addTab(s);
                publishProgress();
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
            loading.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }

        private void addTab(String url) {
            String name = getName(url);
            Drawable icon = getIcon(url);
            Integer color = getColorInt(url);

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
                    if (e.hasAttr("id") && e.attr("id").equals("roomname")) return e.ownText();
                }
                return Jsoup.connect(url).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
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

                Resources r = getResources();
                int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true));
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

                mEditor.putInt(url + "Color", Color.parseColor(colorHex));
                mEditor.apply();
                return Color.parseColor(colorHex);
            } catch (Exception e) {
                e.printStackTrace();
                return Color.parseColor("#000000");
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

    private void initiateCurrentFragments(ArrayList<Fragment> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            String tag = fragment.getArguments().getString("chatUrl");
            if (mFragmentManager.findFragmentByTag(tag) == null) {
                mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).detach(fragment).commit();
            }

            if ((mCurrentFragment == null || mCurrentFragment.equals("home")) && mFragmentManager.findFragmentByTag("home") == null) {
                mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            }

            mFragmentManager.executePendingTransactions();
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
        Log.e("LE", names.length + "");

        chatroomsList = (ListView) findViewById(R.id.chatroomsListView);
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

    private void setupChatRoomMenu()
    {
        // configure the SlidingMenu
        mChatroomSlidingMenu = new SlidingMenu(this);
        mChatroomSlidingMenu.setMode(SlidingMenu.LEFT);
        mChatroomSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mChatroomSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mChatroomSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mChatroomSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_chats_width);
        mChatroomSlidingMenu.setFadeDegree(0.35f);
        mChatroomSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mChatroomSlidingMenu.setMenu(R.layout.chatroom_slideout);
    }

    private void setFragmentByTag(String tag)
    {
        if (mFragmentManager.getFragments() != null)
        {
            for (Fragment fragment : mFragmentManager.getFragments())
            {
                if (!fragment.isDetached())
                {
                    mFragmentManager.beginTransaction().detach(fragment).commit();
                }
            }
            mFragmentManager.beginTransaction().attach(mFragmentManager.findFragmentByTag(tag)).commit();

            if(tag.equals("home"))
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                hueUtils.showAddChatFab(this, true);
                hueUtils.setAddChatFabColorDefault(this);
                hueUtils.setActionBarColorDefault(this);

            }
            else
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                hueUtils.showAddChatFab(this, false);
            }
        }
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

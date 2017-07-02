package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.ImgTextArrayAdapter;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ChatFragFabsHue;
import com.huetoyou.chatexchange.ui.misc.hue.OtherFabsHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;

public class TutorialActivity extends SlidingActivity
{
    private SlidingMenu mChatroomSlidingMenu;

    private boolean mOnCreateCalled = false;
    private ViewGroup mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThemeHue.setTheme(this);
        ActionBarHue.setActionBarColorToSharedPrefsValue(this);
        setContentView(R.layout.fragment_chat);
        setBehindContentView(R.layout.chatroom_slideout);

        OtherFabsHue.setAddChatFabColorToSharedPrefsValue(this);
        ChatFragFabsHue.setChatFragmentFabColorToSharedPrefsValue(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_black_24dp, null);
        assert drawable != null;
        drawable.setTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        mActionBar = Utils.getActionBar(getWindow().getDecorView());

        mChatroomSlidingMenu = getSlidingMenu();

        mChatroomSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mChatroomSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mChatroomSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mChatroomSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_chats_width);
        mChatroomSlidingMenu.setFadeDegree(0.35f);

        String[] names = new String[] {"Example 1", "Example 2", "Example 3"};

        String[] urls = new String[] {"U", "U", "U"};

        Drawable[] ico = new Drawable[3];
        ico[0] = VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);
        ico[1] = VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);
        ico[2] = VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);

        Integer[] colors = new Integer[] {0, 0, 0};

        ListView mChatsList = findViewById(R.id.chatroomsListView);
        ImgTextArrayAdapter mArrayAdapter = new ImgTextArrayAdapter(this, names, urls, ico, colors);
        mChatsList.setAdapter(mArrayAdapter);

        mOnCreateCalled = true;

        setUpSequence();
//        setUpHomeFragmentSequence();

//        new MaterialShowcaseView.Builder(this)
//                .setTarget(actionBar.getChildAt(1))
//                .setTitleText("HUE")
//                .setContentText("Home button")
//                .setDismissText("OK")
//                .show();

        //displayShowcases();
    }

    private void setUpSequence() {
        final FloatingActionMenu fam = findViewById(R.id.chat_menu);
        final FloatingActionButton users = findViewById(R.id.show_users_fab);
        final FloatingActionButton info = findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = findViewById(R.id.star_fab);

        final FloatingActionMenu chatFam = findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = findViewById(R.id.home_fab);
        final FloatingActionButton add = findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = findViewById(R.id.remove_all_chats_fab);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int itemIndex = 0; //i should be the current position, but it isn't working so we need this

            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {
                Log.e("Pos", itemIndex + "");

                switch (itemIndex) {
                    case 1:
                        fam.open(true);
                        break;
                    case 4:
                        fam.close(true);
                        mChatroomSlidingMenu.toggle();
                        break;
                    case 6:
                        chatFam.open(true);
                        break;
                    case 9:
                        chatFam.close(true);
                        mChatroomSlidingMenu.toggle();

                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setUpHomeFragmentSequence();
                            }
                        }, getResources().getInteger(R.integer.animation_duration_ms));
                        break;
                }

                itemIndex++;
            }
        });

        sequence.addSequenceItem(mActionBar.getChildAt(1),
                "Drawer Toggle",
                "OK");

        sequence.addSequenceItem(fam.getMenuButton(),
                "Menu",
                "OK");

        sequence.addSequenceItem(users,
                "Show Users",
                "OK");

        sequence.addSequenceItem(info,
                "Show Info",
                "OK");

        sequence.addSequenceItem(stars,
                "Stars",
                "OK");

        sequence.addSequenceItem(mChatroomSlidingMenu.findViewById(R.id.chatroomsListView),
                "Chatrooms",
                "OK");

        sequence.addSequenceItem(chatFam.getMenuButton(),
                "Menu",
                "OK");

        sequence.addSequenceItem(home,
                "Home",
                "OK");

        sequence.addSequenceItem(add,
                "Add Chat",
                "OK");

        sequence.addSequenceItem(removeAll,
                "Remove All Chats",
                "OK");

        sequence.start();
    }

    private void setUpHomeFragmentSequence() {
        setContentView(R.layout.fragment_home);

        Button chooseSE = findViewById(R.id.chooseSEView);
        Button chooseSO = findViewById(R.id.chooseSOView);
        WebView webView = findViewById(R.id.stars_view);
        webView.loadUrl("https://chat.stackexchange.com");

        findViewById(R.id.open_in_webview).setVisibility(View.GONE);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int currentIndex = 0;
            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {
                if (currentIndex == 2) {
                    startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                    finish();
                }
                currentIndex++; //keep at bottom
            }
        });

        sequence.addSequenceItem(chooseSE,
                "Load SE Chats",
                "OK");

        sequence.addSequenceItem(chooseSO,
                "Load SO Chats",
                "OK");

        config.setShape(new RectangleShape(webView.getWidth(), webView.getHeight()));

        sequence.addSequenceItem(webView,
                "Explore Chats",
                "OK").setConfig(config);

        sequence.start();
    }

    /**
     * Just to keep lint happy
     * @see MainActivity#toggleChatsSlide(View)
     */
    public void toggleChatsSlide(android.view.View view) {
    }

    /**
     * Just to keep lint happy
     * @see MainActivity#confirmClose(View)
     */
    public void confirmClose(android.view.View view) {}

    /*@Override
    protected void onResume()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
               while (true) {
                   if (!mOnCreateCalled) continue;
                   mThemeHue.setThemeOnResume(TutorialActivity.this, true);
                   break;
               }
            }
        }).start();
        super.onResume();
    }

    private void displayShowcases()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.show_chats_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Show chatrooms")
                .setContentText("Click this button to reveal the chatrooms sliding panel\n\nSwiping inwards from the left edge of the screen has the same effect")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseRemoveChat();
                    }

                })
                .build();
    }

    private void showcaseRemoveChat()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.close_chat_frag)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Remove chatroom")
                .setContentText("Click this button to remove the current chatroom from the sliding panel")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseStar();
                    }

                })
                .build();
    }

    private void showcaseStar()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.star_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Show starred messages")
                .setContentText("This button opens a browser window showing the messages currently on the starwall")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseInfo();
                    }

                })
                .build();
    }

    private void showcaseInfo()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.room_info_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Room info")
                .setContentText("This button display's the current chatrooms's description, and other data")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseOpenInBrowser();
                    }

                })
                .build();
    }

    private void showcaseOpenInBrowser()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.open_in_browser_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Open in browser")
                .setContentText("Click this button to open the current chatroom in a browser window")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseUsersPanel();
                    }

                })
                .build();
    }

    private void showcaseUsersPanel()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.show_users_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Reveal users")
                .setContentText("This button reveals current users sliding panel\n\nSwiping inwards from the right edge of the screen has the same effect")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
                        startActivity(intent);
                        SharedPreferences mSharedPrefs;
                        SharedPreferences.Editor mEditor;
                        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        mEditor = mSharedPrefs.edit();
                        mEditor.putBoolean("isFirstRun", false);
                        mEditor.apply();
                        finish();
                    }

                })
                .build();
    }*/
}

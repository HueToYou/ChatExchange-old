package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.shape.Circle;
import com.wooplr.spotlight.target.ViewTarget;
import com.wooplr.spotlight.utils.SpotlightListener;
import com.wooplr.spotlight.utils.SpotlightSequence;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;
import uk.co.deanwild.materialshowcaseview.target.Target;

public class TutorialStuff
{
    private static SharedPreferences mSharedPreferences;
    private static PreferencesManager mPrefsMan;
    private static SpotlightConfig mConfig;

    private static final String CHAT_ITEM = "ChatItem";
    private static final String CHAT_ITEM_SLIDE = "ChatItemSlide";
    private static final String CHAT_ITEM_FAM = "ChatItemFam";
    private static final String CHAT_ITEM_ADD = "ChatItemAdd";
    private static final String CHAT_ITEM_HOME = "ChatItemHome";
    private static final String CHAT_ITEM_REMOVE_ALL = "ChatItemRemAll";
    private static final String CHAT_FRAG_MENU_BTN = "ChatFragMenuBtn";
    private static final String MAIN_DRAWER = "MainDrawer";
    private static final String MAIN_MENU = "MainMenu";
    private static final String CHAT_FRAG_FAM = "ChatFragFam";
    private static final String CHAT_FRAG_USERS_FAB = "ChatFragUsersFab";
    private static final String CHAT_FRAG_INFO_FAB = "ChatFragInfoFab";
    private static final String CHAT_FRAG_STARS_FAB = "ChatFragStarsFab";
    private static final String CHAT_FRAG_OPENINBROWSER_FAB = "ChatFragOpeninbrowserFab";
    private static final String CHAT_FRAG_MESSG_ENTRY_BOX = "ChatFragMessgEntryBox";
    private static final String CHAT_FRAG_SEND_MESSG_BTN = "ChatFragSendMessgBtn";

    /*
     * Main Activity
     */
    public static void showChatSliderTutorial_MainActivity(final Activity activity)
    {
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (mConfig == null)
        {
            setConfig(activity);
        }

        final FloatingActionMenu chatFam = activity.findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = activity.findViewById(R.id.home_fab);
        final FloatingActionButton add = activity.findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = activity.findViewById(R.id.remove_all_chats_fab);

        final RecyclerView dummyChats = activity.findViewById(R.id.dummy_chat_list);

        final Drawable ico = activity.getResources().getDrawable(R.mipmap.ic_launcher);

        final RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(activity, null, swipeManager);
        recyclerAdapter.addItem(new ChatroomRecyclerObject(
                0, "Example 1", "U", ico, 0, 0, 0
        ));

        RecyclerView.Adapter adapter = swipeManager.createWrappedAdapter(recyclerAdapter);

        dummyChats.setAdapter(adapter);

        // disable change animations
        ((SimpleItemAnimator) dummyChats.getItemAnimator()).setSupportsChangeAnimations(false);

        swipeManager.attachRecyclerView(dummyChats);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dummyChats.getContext(),
                DividerItemDecoration.VERTICAL);

        dummyChats.addItemDecoration(dividerItemDecoration);

        final OnSwipeListener onSwipeListener = new OnSwipeListener()
        {
            @Override
            public void onSwipeLeft(RecyclerView.ViewHolder viewHolder)
            {
                swipeManager.performFakeSwipe(viewHolder, 2);
            }

            @Override
            public void onSwipeRight(RecyclerView.ViewHolder viewHolder)
            {
                swipeManager.performFakeSwipe(viewHolder, 1);
            }
        };

        PreferencesManager manager = new PreferencesManager(activity);
        if (!manager.isDisplayed(CHAT_ITEM)) {
            activity.findViewById(R.id.chatroomsListView).setVisibility(View.GONE);
            dummyChats.setVisibility(View.VISIBLE);
        }

        SpotlightView chats = new SpotlightView.Builder(activity)
                .target(activity.findViewById(R.id.chatroomsListView))
                .setConfiguration(mConfig)
                .headingTvText("Chats")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text))
                .usageId(CHAT_ITEM)
                .show();

        final SpotlightView.Builder chatsSwipe = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Slide To Delete")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_swipe_left_text))
                .usageId(CHAT_ITEM_SLIDE);

        final SpotlightView.Builder chatFAM = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .target(chatFam.getMenuButton())
                .headingTvText("Menu")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_FAM_tutorial_text))
                .usageId(CHAT_ITEM_FAM);

        final SpotlightView.Builder chatHome = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .target(chatFam.findViewById(R.id.home_fab))
                .headingTvText("Home")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_homeFAB_tutorial_text))
                .usageId(CHAT_ITEM_HOME);

        final SpotlightView.Builder chatAdd = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .target(chatFam.findViewById(R.id.add_chat_fab))
                .headingTvText("Add Chat")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_addChatFAB_tutorial_text))
                .usageId(CHAT_ITEM_ADD);

        final SpotlightView.Builder chatRemAll = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .target(chatFam.findViewById(R.id.remove_all_chats_fab))
                .headingTvText("Remove All Chats")
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_removeALlChatsFAB_tutorial_text))
                .usageId(CHAT_ITEM_REMOVE_ALL);

        SpotlightListener listener = new SpotlightListener()
        {
            @Override
            public void onUserClicked(String s)
            {
                switch (s)
                {
                    case CHAT_ITEM:
                        chatsSwipe.target(recyclerAdapter.getViewHolderAt(0).getCloseChatButton()).show();
                        onSwipeListener.onSwipeLeft(recyclerAdapter.getViewHolderAt(0));
                        break;
                    case CHAT_ITEM_SLIDE:
                        onSwipeListener.onSwipeRight(recyclerAdapter.getViewHolderAt(0));
                        chatFAM.show();
                        break;
                    case CHAT_ITEM_FAM:
                        chatFam.open(true);
                        chatHome.show();
                        break;
                    case CHAT_ITEM_HOME:
                        chatAdd.show();
                        break;
                    case CHAT_ITEM_ADD:
                        chatRemAll.show();
                        break;
                    case CHAT_ITEM_REMOVE_ALL:
                        chatFam.close(true);
                        activity.findViewById(R.id.chatroomsListView).setVisibility(View.VISIBLE);
                        dummyChats.setVisibility(View.GONE);
                        break;
                }
            }
        };

        chats.setListener(listener);
        chatsSwipe.setListener(listener);
        chatFAM.setListener(listener);
        chatHome.setListener(listener);
        chatAdd.setListener(listener);
        chatRemAll.setListener(listener);
    }

    /*
     * Home fragment
     */
    public static void homeFragTutorial(final MainActivity activity)
    {

        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (mConfig == null)
        {
            setConfig(activity);
        }

        SpotlightSequence.getInstance(activity, mConfig)
                .addSpotlight(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1),
                        "Drawer",
                        activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text),
                        MAIN_DRAWER)
                .addSpotlight(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(2),
                        "Menu",
                        activity.getResources().getString(R.string.homeFrag_options_menu_tutorial_text),
                        MAIN_MENU)
                .startSequence();
    }

    /*
     * Chat fragment
     */
    public static void chatFragTutorial(Activity activity, View view, int mAppBarColor)
    {
        SlidingMenu chatroomsMenu = ((MainActivity) activity).getmChatroomSlidingMenu();
        if (chatroomsMenu.isMenuShowing())
        {
            chatroomsMenu.hideMenu(false);
        }
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (mConfig == null)
        {
            setConfig(activity);
        }

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);
        final FloatingActionButton openInBrowser = view.findViewById(R.id.open_in_browser_fab);
        final EditText messageEntryBox = view.findViewById(R.id.messageToSend);
        final ImageButton sendMsg = view.findViewById(R.id.sendMessageBtn);

        final SpotlightView menuBtn = new SpotlightView.Builder(activity)
                .target(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1))
                .setConfiguration(mConfig)
                .headingTvText("Menu")
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_hamburger_tutorial_text))
                .usageId(CHAT_FRAG_MENU_BTN)
                .show();

        final SpotlightView.Builder chatFragFam = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Floating Action Menu")
                .target(fam.getMenuButton())
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_FAM_tutorial_text))
                .usageId(CHAT_FRAG_FAM);

        final SpotlightView.Builder chatFragUsersFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Users")
                .target(users)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_showUsersFAB_tutorial_text))
                .usageId(CHAT_FRAG_USERS_FAB);

        final SpotlightView.Builder chatFragInfoFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Info")
                .target(info)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_roomInfoFAB_tutorial_text))
                .usageId(CHAT_FRAG_INFO_FAB);

        final SpotlightView.Builder chatFragStarsFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Stars")
                .target(stars)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_starredMessagesFAB_tutorial_text))
                .usageId(CHAT_FRAG_STARS_FAB);

        final SpotlightView.Builder chatFragOpenInBrowserFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Open in Browser")
                .target(openInBrowser)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_openInBrowserFAB_tutorial_text))
                .usageId(CHAT_FRAG_OPENINBROWSER_FAB);

        final SpotlightView.Builder chatFragMessageEntryBox = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Message Box")
                .target(messageEntryBox)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_messageEntryBox_tutorial_text))
                .usageId(CHAT_FRAG_MESSG_ENTRY_BOX);

        final SpotlightView.Builder chatFragSendMessageButton = new SpotlightView.Builder(activity)
                .setConfiguration(mConfig)
                .headingTvText("Send Message")
                .target(sendMsg)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_sendMsgBtn_tutorial_text))
                .usageId(CHAT_FRAG_SEND_MESSG_BTN);

        SpotlightListener huehuelistener = new SpotlightListener()
        {
            @Override
            public void onUserClicked(String s)
            {
                switch (s)
                {
                    case CHAT_FRAG_MENU_BTN:
                        chatFragFam.show();
                        break;

                    case CHAT_FRAG_FAM:
                        fam.open(true);
                        chatFragUsersFAB.show();
                        break;

                    case CHAT_FRAG_USERS_FAB:
                        chatFragInfoFAB.show();
                        break;

                    case CHAT_FRAG_INFO_FAB:
                        chatFragStarsFAB.show();
                        break;

                    case CHAT_FRAG_STARS_FAB:
                        chatFragOpenInBrowserFAB.show();
                        break;

                    case CHAT_FRAG_OPENINBROWSER_FAB:
                        fam.close(true);
                        chatFragMessageEntryBox.show();
                        break;

                    case CHAT_FRAG_MESSG_ENTRY_BOX:
                        chatFragSendMessageButton.show();
                        break;
                }
            }
        };

        menuBtn.setListener(huehuelistener);
        chatFragFam.setListener(huehuelistener);
        chatFragUsersFAB.setListener(huehuelistener);
        chatFragInfoFAB.setListener(huehuelistener);
        chatFragStarsFAB.setListener(huehuelistener);
        chatFragOpenInBrowserFAB.setListener(huehuelistener);
        chatFragMessageEntryBox.setListener(huehuelistener);
    }

    public interface OnSwipeListener
    {
        void onSwipeLeft(RecyclerView.ViewHolder viewHolder);

        void onSwipeRight(RecyclerView.ViewHolder viewHolder);
    }

    private static void setConfig(Activity activity)
    {
        mConfig = new SpotlightConfig();
        mConfig.setIntroAnimationDuration(400);
        mConfig.setRevealAnimationEnabled(true);
        mConfig.setPerformClick(false);
        mConfig.setFadingTextDuration(400);
        mConfig.setHeadingTvColor(Color.WHITE);
        mConfig.setHeadingTvText("Drawer");
        mConfig.setHeadingTvSize(32);
        mConfig.setSubHeadingTvColor(Color.WHITE);
        mConfig.setSubHeadingTvSize(16);
        mConfig.setHeadingTvText(activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text));
        mConfig.setMaskColor(Color.parseColor("#99000000"));
        mConfig.setLineAnimationDuration(400);
        mConfig.setLineAndArcColor(Color.LTGRAY);
        mConfig.setDismissOnTouch(true);
        mConfig.setDismissOnBackpress(true);
    }

    public static void resetSpotlights(Activity activity)
    {
        PreferencesManager manager = new PreferencesManager(activity);
        manager.resetAll();
    }
}

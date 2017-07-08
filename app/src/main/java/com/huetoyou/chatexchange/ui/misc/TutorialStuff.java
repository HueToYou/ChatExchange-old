package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Util;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.frags.UserTileFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightListener;
import com.wooplr.spotlight.utils.SpotlightSequence;

import java.util.List;

public class TutorialStuff
{
    private static SharedPreferences mSharedPreferences;
    private static PreferencesManager mPrefsMan;
    private static SpotlightConfig mCategoryConfig;

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
    private static final String USERS_SLIDE_INTRO = "UsersSlideIntro";
    private static final String USERS_SLIDE_INTRO_MORE = "UsersSlideIntroMore";
    private static final String USER_ONE = "User1";
    private static final String USER_MOD = "UserMod";
    private static final String USER_OWNER = "UserOwner";

    private static final String USER_NAME_KEY = "userName";
    private static final String USER_AVATAR_URL_KEY = "userAvatarUrl";
    private static final String USER_URL_KEY = "chatUrl";
    private static final String USER_ID_KEY = "id";
    private static final String USER_LAST_POST_KEY = "lastPost";
    private static final String USER_REP_KEY = "rep";
    private static final String USER_IS_MOD_KEY = "isMod";
    private static final String USER_IS_OWNER_KEY = "isOwner";

    private static SpotlightConfig mItemConfig;

    /*
     * Main Activity
     */
    public static void showChatSliderTutorial_MainActivity(final Activity activity)
    {
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (mCategoryConfig == null)
        {
            setCategoryConfig(activity);
        }

        if (mItemConfig == null) setItemConfig(activity);

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
        recyclerAdapter.addItem(new ChatroomRecyclerObject(
                1, "Example 2", "U", ico, 0, 0, 1
        ));
        recyclerAdapter.addItem(new ChatroomRecyclerObject(
                2, "Example 3", "U", ico, 0, 0, 2
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
                .target(dummyChats)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text_title))
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text))
                .usageId(CHAT_ITEM)
                .targetPadding(Util.dpToPx(activity, 50))
                .show();

        final SpotlightView.Builder chatsSwipe = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text_swipe_delete))
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_swipe_left_text))
                .usageId(CHAT_ITEM_SLIDE);

        final SpotlightView.Builder chatFAM = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .target(chatFam.getMenuButton())
                .headingTvText(activity.getResources().getString(R.string.tutorial_menu))
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_FAM_tutorial_text))
                .usageId(CHAT_ITEM_FAM);

        final SpotlightView.Builder chatHome = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .target(home)
                .headingTvText(activity.getResources().getString(R.string.tutorial_home))
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_homeFAB_tutorial_text))
                .usageId(CHAT_ITEM_HOME);

        final SpotlightView.Builder chatAdd = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .target(add)
                .headingTvText(activity.getResources().getString(R.string.tutorial_add))
                .subHeadingTvText(activity.getResources().getString(R.string.chatrooms_slidingMenu_addChatFAB_tutorial_text))
                .usageId(CHAT_ITEM_ADD);

        final SpotlightView.Builder chatRemAll = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .target(removeAll)
                .headingTvText(activity.getResources().getString(R.string.tutorial_remove_all))
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

        if (mCategoryConfig == null)
        {
            setCategoryConfig(activity);
        }

        SpotlightSequence.getInstance(activity, mCategoryConfig)
                .addSpotlight(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1),
                        activity.getResources().getString(R.string.tutorial_drawer),
                        activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text),
                        MAIN_DRAWER)
                .addSpotlight(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(2),
                        activity.getResources().getString(R.string.tutorial_menu),
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

        if (mCategoryConfig == null)
        {
            setCategoryConfig(activity);
        }

        if (mItemConfig == null) setItemConfig(activity);

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);
        final FloatingActionButton openInBrowser = view.findViewById(R.id.open_in_browser_fab);
        final EditText messageEntryBox = view.findViewById(R.id.messageToSend);
        final ImageButton sendMsg = view.findViewById(R.id.sendMessageBtn);

        final SpotlightView menuBtn = new SpotlightView.Builder(activity)
                .target(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1))
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_menu))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_hamburger_tutorial_text))
                .usageId(CHAT_FRAG_MENU_BTN)
                .show();

        final SpotlightView.Builder chatFragFam = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_menu))
                .target(fam.getMenuButton())
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_FAM_tutorial_text))
                .usageId(CHAT_FRAG_FAM);

        final SpotlightView.Builder chatFragUsersFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_main))
                .target(users)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_showUsersFAB_tutorial_text))
                .usageId(CHAT_FRAG_USERS_FAB);

        final SpotlightView.Builder chatFragInfoFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_info))
                .target(info)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_roomInfoFAB_tutorial_text))
                .usageId(CHAT_FRAG_INFO_FAB);

        final SpotlightView.Builder chatFragStarsFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_stars))
                .target(stars)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_starredMessagesFAB_tutorial_text))
                .usageId(CHAT_FRAG_STARS_FAB);

        final SpotlightView.Builder chatFragOpenInBrowserFAB = new SpotlightView.Builder(activity)
                .setConfiguration(mItemConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_open_browser))
                .target(openInBrowser)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_openInBrowserFAB_tutorial_text))
                .usageId(CHAT_FRAG_OPENINBROWSER_FAB);

        final SpotlightView.Builder chatFragMessageEntryBox = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_msg_box))
                .target(messageEntryBox)
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_messageEntryBox_tutorial_text))
                .usageId(CHAT_FRAG_MESSG_ENTRY_BOX);

        final SpotlightView.Builder chatFragSendMessageButton = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.tutorial_send_btn))
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

    public static void showUsersTutorial(final Activity activity) {
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        if (mCategoryConfig == null)
        {
            setCategoryConfig(activity);
        }

        if (mItemConfig == null) setItemConfig(activity);


        Bundle args = new Bundle();
        args.putString(USER_NAME_KEY, "Example 1");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");

        args.putInt(USER_ID_KEY, 12345);
        args.putInt(USER_LAST_POST_KEY, 0);
        args.putInt(USER_REP_KEY, 123);

        args.putBoolean(USER_IS_MOD_KEY, false);
        args.putBoolean(USER_IS_OWNER_KEY, false);

        final UserTileFragment userTileFragment = new UserTileFragment();
        userTileFragment.setArguments(args);

        args = new Bundle();
        args.putString(USER_NAME_KEY, "Example 2");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");

        args.putInt(USER_ID_KEY, 12346);
        args.putInt(USER_LAST_POST_KEY, 0);
        args.putInt(USER_REP_KEY, 123);

        args.putBoolean(USER_IS_MOD_KEY, true);
        args.putBoolean(USER_IS_OWNER_KEY, false);

        final UserTileFragment userTileFragment1 = new UserTileFragment();
        userTileFragment1.setArguments(args);

        args = new Bundle();
        args.putString(USER_NAME_KEY, "Example 3");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");

        args.putInt(USER_ID_KEY, 12347);
        args.putInt(USER_LAST_POST_KEY, 0);
        args.putInt(USER_REP_KEY, 123);

        args.putBoolean(USER_IS_MOD_KEY, false);
        args.putBoolean(USER_IS_OWNER_KEY, true);

        final UserTileFragment userTileFragment2 = new UserTileFragment();
        userTileFragment2.setArguments(args);

        PreferencesManager manager = new PreferencesManager(activity);

        LinearLayout users = activity.findViewById(R.id.users_scroll_slide);

        if (!manager.isDisplayed(USERS_SLIDE_INTRO)) {
            List<android.support.v4.app.Fragment> fragments = ((AppCompatActivity)activity).getSupportFragmentManager().getFragments();

            for (int i = 0; i < fragments.size(); i++) {
                ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().hide(fragments.get(i)).commit();
            }

            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().add(R.id.users_scroll_slide, userTileFragment, "user_" + 12345).commit();
            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().add(R.id.users_scroll_slide, userTileFragment1, "user_" + 12346).commit();
            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().add(R.id.users_scroll_slide, userTileFragment2, "user_" + 12347).commit();
        }

        SpotlightView usersOverview = new SpotlightView.Builder(activity)
                .target(users)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_main))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text))
                .usageId(USERS_SLIDE_INTRO)
                .targetPadding(Util.dpToPx(activity, 50))
                .show();

        final SpotlightView.Builder overviewMore = new SpotlightView.Builder(activity)
                .target(users)
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_main))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_more))
                .usageId(USERS_SLIDE_INTRO_MORE)
                .targetPadding(Util.dpToPx(activity, 50));

        final SpotlightView.Builder user1 = new SpotlightView.Builder(activity)
                .target(users.getChildAt(0))
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_user_normal))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_normalUser_tutorial_text))
                .usageId(USER_ONE);

        final SpotlightView.Builder userMod = new SpotlightView.Builder(activity)
                .target(users.getChildAt(1))
                .setConfiguration(mCategoryConfig)
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_user_mod))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_modUser_tutorial_text))
                .usageId(USER_MOD);

        final SpotlightView.Builder userOwner = new SpotlightView.Builder(activity)
                .setConfiguration(mCategoryConfig)
                .target(users.getChildAt(2))
                .headingTvText(activity.getResources().getString(R.string.chatFrag_usersSlidingPanel_tutorial_text_title_user_owner))
                .subHeadingTvText(activity.getResources().getString(R.string.chatFrag_ROuser_tutorial_text))
                .usageId(USER_OWNER);

        SpotlightListener listener = new SpotlightListener()
        {
            @Override
            public void onUserClicked(String s)
            {
                Log.e("Which", s);
                switch (s)
                {
                    case USERS_SLIDE_INTRO:
                        overviewMore.show();
                        break;
                    case USERS_SLIDE_INTRO_MORE:
                        user1.show();
                        break;
                    case USER_ONE:
                        userMod.show();
                        break;
                    case USER_MOD:
                        userOwner.show();
                        break;
                    case USER_OWNER:
                        List<android.support.v4.app.Fragment> fragments = ((AppCompatActivity)activity).getSupportFragmentManager().getFragments();

                        for (int i = 0; i < fragments.size(); i++) {
                            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().show(fragments.get(i)).commit();
                        }

                        ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().remove(userTileFragment).remove(userTileFragment1).remove(userTileFragment2).commit();
                        break;
                }
            }
        };

        usersOverview.setListener(listener);
        overviewMore.setListener(listener);
        user1.setListener(listener);
        userMod.setListener(listener);
        userOwner.setListener(listener);
    }

    public interface OnSwipeListener
    {
        void onSwipeLeft(RecyclerView.ViewHolder viewHolder);

        void onSwipeRight(RecyclerView.ViewHolder viewHolder);
    }

    private static void setCategoryConfig(Activity activity)
    {
        mCategoryConfig = new SpotlightConfig();
        mCategoryConfig.setIntroAnimationDuration(300L);
        mCategoryConfig.setRevealAnimationEnabled(true);
        mCategoryConfig.setPerformClick(false);
        mCategoryConfig.setFadingTextDuration(300L);
        mCategoryConfig.setHeadingTvColor(Color.WHITE);
        mCategoryConfig.setHeadingTvText("Drawer");
        mCategoryConfig.setSubHeadingTvColor(Color.WHITE);
        mCategoryConfig.setHeadingTvText(activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text));
        mCategoryConfig.setMaskColor(Color.parseColor("#99000000"));
        mCategoryConfig.setLineAnimationDuration(300L);
        mCategoryConfig.setLineAndArcColor(Color.LTGRAY);
        mCategoryConfig.setDismissOnTouch(true);
        mCategoryConfig.setDismissOnBackpress(true);
        mCategoryConfig.setShowTargetArc(true);

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        float density  = metrics.density;
        float dpHeight = metrics.heightPixels / density;
        float dpWidth  = metrics.widthPixels / density;

        if (dpWidth > 600) {
            mCategoryConfig.setHeadingTvSize(48);
            mCategoryConfig.setSubHeadingTvSize(32);
        } else {
            mCategoryConfig.setHeadingTvSize(24);
            mCategoryConfig.setSubHeadingTvSize(18);
        }
    }

    private static void setItemConfig(Activity activity) {
        if (mCategoryConfig == null) setCategoryConfig(activity);
        mItemConfig = mCategoryConfig;
        mItemConfig.setIntroAnimationDuration(100L);
        mItemConfig.setFadingTextDuration(100L);
        mItemConfig.setLineAnimationDuration(100L);
    }

    public static void resetSpotlights(Activity activity)
    {
        PreferencesManager manager = new PreferencesManager(activity);
        manager.resetAll();
    }
}
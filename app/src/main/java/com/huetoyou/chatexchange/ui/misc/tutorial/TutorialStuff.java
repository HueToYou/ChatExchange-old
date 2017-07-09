package com.huetoyou.chatexchange.ui.misc.tutorial;

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
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Util;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.frags.UserTileFragment;
import com.huetoyou.chatexchange.ui.misc.ChatroomRecyclerObject;
import com.huetoyou.chatexchange.ui.misc.RecyclerAdapter;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.List;

public class TutorialStuff
{
    private static SharedPreferences mSharedPreferences;

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

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);
        final FloatingActionButton openInBrowser = view.findViewById(R.id.open_in_browser_fab);
        final EditText messageEntryBox = view.findViewById(R.id.messageToSend);
        final ImageButton sendMsg = view.findViewById(R.id.sendMessageBtn);


    }

    public static void showUsersTutorial(final Activity activity) {
        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        /*if (mCategoryConfig == null)
        {
            setCategoryConfig(activity);
        }

        if (mItemConfig == null) setItemConfig(activity);


        Bundle args = new Bundle();
        args.putString(USER_NAME_KEY, "Edwinksl");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");
        args.putBoolean("IsExampleTile", true);
        args.putInt("ExampleNum", 0);

        args.putInt(USER_ID_KEY, 12345);
        args.putInt(USER_LAST_POST_KEY, 0);
        args.putInt(USER_REP_KEY, 123);

        args.putBoolean(USER_IS_MOD_KEY, false);
        args.putBoolean(USER_IS_OWNER_KEY, false);

        final UserTileFragment userTileFragment = new UserTileFragment();
        userTileFragment.setArguments(args);

        args = new Bundle();
        args.putString(USER_NAME_KEY, "Thomas Ward");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");
        args.putBoolean("IsExampleTile", true);
        args.putInt("ExampleNum", 1);

        args.putInt(USER_ID_KEY, 12346);
        args.putInt(USER_LAST_POST_KEY, 0);
        args.putInt(USER_REP_KEY, 123);

        args.putBoolean(USER_IS_MOD_KEY, true);
        args.putBoolean(USER_IS_OWNER_KEY, false);

        final UserTileFragment userTileFragment1 = new UserTileFragment();
        userTileFragment1.setArguments(args);

        args = new Bundle();
        args.putString(USER_NAME_KEY, "Rinzwind");
        args.putString(USER_AVATAR_URL_KEY, "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages.clipshrine.com%2Fdownload%2Fdownloadpnglarge%2FBlack-Question-Mark-2269-large.png&f=1");
        args.putString(USER_URL_KEY, "https://example.stackexchange.com");
        args.putBoolean("IsExampleTile", true);
        args.putInt("ExampleNum", 2);

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

        //MainActivity.touchesAllowed = false;
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
                MainActivity.touchesAllowed = false;
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

            @Override
            public void onFinishDrawingSpotlight()
            {
                MainActivity.touchesAllowed = true;
                System.out.println("Allowing touches");
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
        mCategoryConfig.setIntroAnimationDuration((long)Utils.getAnimDuration(1000, activity));
        mCategoryConfig.setRevealAnimationEnabled(true);
        mCategoryConfig.setPerformClick(false);
        mCategoryConfig.setFadingTextDuration((long)Utils.getAnimDuration(1000, activity));
        mCategoryConfig.setHeadingTvColor(Color.WHITE);
        mCategoryConfig.setHeadingTvText("Drawer");
        mCategoryConfig.setSubHeadingTvColor(Color.WHITE);
        mCategoryConfig.setHeadingTvText(activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text));
        mCategoryConfig.setMaskColor(Color.parseColor("#aa000000"));
        mCategoryConfig.setLineAnimationDuration((long)Utils.getAnimDuration(1000, activity));
        mCategoryConfig.setLineAndArcColor(Color.LTGRAY);
        mCategoryConfig.setDismissOnTouch(true);
        mCategoryConfig.setDismissOnBackpress(true);
        mCategoryConfig.setShowTargetArc(true);

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        float density  = metrics.density;
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
        /*mItemConfig.setIntroAnimationDuration(100L);
        mItemConfig.setFadingTextDuration(100L);
        mItemConfig.setLineAnimationDuration(100L);*/
    }
}
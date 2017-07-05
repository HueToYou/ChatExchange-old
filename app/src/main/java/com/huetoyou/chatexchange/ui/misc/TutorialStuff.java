package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.HueUtils;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;

public class TutorialStuff
{
    private static SharedPreferences mSharedPreferences;

    /*
     * Main Activity
     */
    public static void showChatSliderTutorial_MainActivity(final Activity activity)
    {

        if (mSharedPreferences == null) mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final FloatingActionMenu chatFam = activity.findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = activity.findViewById(R.id.home_fab);
        final FloatingActionButton add = activity.findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = activity.findViewById(R.id.remove_all_chats_fab);

        final RecyclerView dummyChats = activity.findViewById(R.id.dummy_chat_list);

        final Drawable ico = activity.getResources().getDrawable(R.mipmap.ic_launcher);

        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(activity, null);
        recyclerAdapter.addItem(0, "Example 1", "U", ico, 0);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dummyChats.getContext(),
                DividerItemDecoration.VERTICAL);

        dummyChats.setAdapter(recyclerAdapter);
        dummyChats.addItemDecoration(dividerItemDecoration);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(0);
        config.setFadeDuration(250);
        int color = ActionBarHue.getActionBarPrefsColor((AppCompatActivity)activity);
        config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));
        config.setRenderOverNavigationBar(true);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "ChatSliderTutorial");
        sequence.setConfig(config);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener()
        {
            @Override
            public void onShow(MaterialShowcaseView materialShowcaseView, int i)
            {
                activity.findViewById(R.id.chatroomsListView).setVisibility(View.GONE);
                dummyChats.setVisibility(View.VISIBLE);
            }
        });

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int position = 0;

            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {
                switch (position)
                {
                    case 0:
                        recyclerAdapter.getViewHolderAt(0).performLongClick();
                        break;
                    case 1:
                        recyclerAdapter.getViewHolderAt(0).performLongClick();
                        break;
                    case 2:
                        chatFam.open(true);
                        break;
                    case 5:
                        chatFam.close(true);
                        dummyChats.setVisibility(View.GONE);
                        activity.findViewById(R.id.chatroomsListView).setVisibility(View.VISIBLE);
                        break;
                }

                position++;
            }
        });

        ShowcaseConfig config1 = new ShowcaseConfig();
        config1.setDelay(0);
        config1.setFadeDuration(250);
        config1.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));
        config1.setRenderOverNavigationBar(true);
        config1.setShape(new RectangleShape(0, 0));

        sequence.setConfig(config1);

        sequence.addSequenceItem(dummyChats,
                 activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text),
                "OK");

        sequence.addSequenceItem(dummyChats,
                activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_long_press_text),
                "OK");

        sequence.setConfig(config);

        sequence.addSequenceItem(chatFam.getMenuButton(),
                activity.getResources().getString(R.string.chatrooms_slidingMenu_FAM_tutorial_text),
                "OK");

        sequence.addSequenceItem(home,
                activity.getResources().getString(R.string.chatrooms_slidingMenu_homeFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(add,
                activity.getResources().getString(R.string.chatrooms_slidingMenu_addChatFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(removeAll,
                activity.getResources().getString(R.string.chatrooms_slidingMenu_removeALlChatsFAB_tutorial_text),
                "OK");

        sequence.start();
    }

    /*
     * Home fragment
     */
    public static void homeFragTutorial(final MainActivity activity)
    {

        if (mSharedPreferences == null) mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(0);
        config.setFadeDuration(250);
        int color = ActionBarHue.getActionBarPrefsColor(activity);
        config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));
        config.setRenderOverNavigationBar(true);

        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "HomeFragTutorial");
        sequence.setConfig(config);

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int currentIndex = 0;

            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {

                currentIndex++; //keep at bottom
            }
        });

//        Log.e("COUNT", ((ActionMenuView)Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(2)).getcla + "");

        sequence.addSequenceItem(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1),
                activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text),
                "OK");

        sequence.addSequenceItem(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(2),
                activity.getResources().getString(R.string.homeFrag_options_menu_tutorial_text),
                "OK");

        sequence.start();
    }

    /*
     * Chat fragment
     */
    public static void chatFragTutorial(Activity activity, View view, int mAppBarColor)
    {
        ((MainActivity)activity).getmChatroomSlidingMenu().hideMenu();
        if (mSharedPreferences == null) mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);
        final FloatingActionButton openInBrowser = view.findViewById(R.id.open_in_browser_fab);
        final EditText messageEntryBox = view.findViewById(R.id.messageToSend);
        final ImageButton sendMsg = view.findViewById(R.id.sendMessageBtn);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(0);
        config.setFadeDuration(250);
        config.setRenderOverNavigationBar(true);

        if (mSharedPreferences.getBoolean("dynamicallyColorBar", false))
        {
            config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(mAppBarColor), Color.green(mAppBarColor), Color.blue(mAppBarColor)), 0.6f));
        } else {
            int color = ActionBarHue.getActionBarPrefsColor((AppCompatActivity)activity);
            config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));
        }

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "ChatFragTutorial");
        sequence.setConfig(config);

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int itemIndex = 0; //i should be the current position, but it isn't working so we need this

            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {
                Log.e("Pos", itemIndex + "");

                switch (itemIndex)
                {
                    case 1:
                        fam.open(true);
                        break;
                    case 5:
                        fam.close(true);
                        break;
                }

                itemIndex++;
            }
        });

        sequence.addSequenceItem(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1),
                activity.getResources().getString(R.string.chatFrag_hamburger_tutorial_text),
                "OK");

        sequence.addSequenceItem(fam.getMenuButton(),
                activity.getResources().getString(R.string.chatFrag_FAM_tutorial_text),
                "OK");

        sequence.addSequenceItem(users,
                activity.getResources().getString(R.string.chatFrag_showUsersFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(info,
                activity.getResources().getString(R.string.chatFrag_roomInfoFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(stars,
                activity.getResources().getString(R.string.chatFrag_starredMessagesFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(openInBrowser,
                activity.getResources().getString(R.string.chatFrag_openInBrowserFAB_tutorial_text),
                "OK");

        sequence.addSequenceItem(messageEntryBox,
                activity.getResources().getString(R.string.chatFrag_messageEntryBox_tutorial_text),
                "OK");

        sequence.addSequenceItem(sendMsg,
                activity.getResources().getString(R.string.chatFrag_sendMsgBtn_tutorial_text),
                "OK");

        sequence.start();
    }
}

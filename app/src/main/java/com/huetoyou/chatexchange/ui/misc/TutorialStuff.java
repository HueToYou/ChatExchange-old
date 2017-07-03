package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.HueUtils;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

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

        final ListView dummyChats = activity.findViewById(R.id.dummy_chat_list);

        Drawable example = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_help_outline_black_24dp, null);

        ImgTextArrayAdapter imgTextArrayAdapter = new ImgTextArrayAdapter(activity);
        imgTextArrayAdapter.addChat("Example 1", "", example, 0);
        imgTextArrayAdapter.addChat("Example 2", "", example, 0);
        imgTextArrayAdapter.addChat("Example 3", "", example, 0);

        dummyChats.setAdapter(imgTextArrayAdapter);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        int color = ActionBarHue.getActionBarPrefsColor((AppCompatActivity)activity);
        config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));

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
                    case 1:
                        chatFam.open(true);
                        break;
                    case 4:
                        chatFam.close(true);
                        dummyChats.setVisibility(View.GONE);
                        activity.findViewById(R.id.chatroomsListView).setVisibility(View.VISIBLE);
                        break;
                }

                position++;
            }
        });

        sequence.addSequenceItem(dummyChats,
                 activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text),
                "OK");

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
    public static void homeFragTutorial(Activity activity, View view)
    {

        if (mSharedPreferences == null) mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        int color = ActionBarHue.getActionBarPrefsColor((AppCompatActivity)activity);
        config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(color), Color.green(color), Color.blue(color)), 0.6f));

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

        sequence.addSequenceItem(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1),
                activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text),
                "OK");

        /*sequence.addSequenceItem(chooseSE,
                "Load SE Chats",
                "OK");

        sequence.addSequenceItem(chooseSO,
                "Load SO Chats",
                "OK");

        config.setShape(new RectangleShape(webView.getWidth(), webView.getHeight()));

        sequence.addSequenceItem(webView,
                "Explore Chats",
                "OK");*/

        sequence.start();
    }

    /*
     * Chat fragment
     */
    public static void chatFragTutorial(Activity activity, View view, int mAppBarColor)
    {

        if (mSharedPreferences == null) mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);
        final EditText messageEntryBox = view.findViewById(R.id.messageToSend);
        final ImageButton sendMsg = view.findViewById(R.id.sendMessageBtn);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

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
                    case 4:
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

        sequence.addSequenceItem(messageEntryBox,
                activity.getResources().getString(R.string.chatFrag_messageEntryBox_tutorial_text),
                "OK");

        sequence.addSequenceItem(sendMsg,
                activity.getResources().getString(R.string.chatFrag_sendMsgBtn_tutorial_text),
                "OK");

        sequence.start();
    }
}

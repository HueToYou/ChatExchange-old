package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    /*
     * Main Activity
     */
    public static void showChatSliderTutorial_MainActivity(final Activity activity)
    {

        final FloatingActionMenu chatFam = activity.findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = activity.findViewById(R.id.home_fab);
        final FloatingActionButton add = activity.findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = activity.findViewById(R.id.remove_all_chats_fab);

        final ListView dummyChats = activity.findViewById(R.id.dummy_chat_list);

        String[] names = new String[] {"Example 1", "Example 2", "Example 3"};
        String[] urls = new String[] {"U", "U", "U"};
        Drawable example = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_help_outline_black_24dp, null);
        Drawable[] icons = new Drawable[] {example, example, example};
        Integer[] colors = new Integer[] {0, 0, 0};

        ImgTextArrayAdapter imgTextArrayAdapter = new ImgTextArrayAdapter(activity);
        imgTextArrayAdapter.addChat(names[0], urls[0], icons[0], colors[0]);
        imgTextArrayAdapter.addChat(names[1], urls[1], icons[1], colors[1]);
        imgTextArrayAdapter.addChat(names[2], urls[2], icons[2], colors[2]);

        dummyChats.setAdapter(new ImgTextArrayAdapter(activity));

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
        //setContentView(R.layout.fragment_home);

        Button chooseSE = view.findViewById(R.id.chooseSEView);
        Button chooseSO = view.findViewById(R.id.chooseSOView);
        //WebView webView = view.findViewById(R.id.stars_view);
        //webView.loadUrl("https://chat.stackexchange.com");

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
                "Drawer Toggle",
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

        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        final FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        final FloatingActionButton info = view.findViewById(R.id.room_info_fab);
        final FloatingActionButton stars = view.findViewById(R.id.star_fab);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        config.setMaskColor(HueUtils.darkenColor(Color.argb(0xbb, Color.red(mAppBarColor), Color.green(mAppBarColor), Color.blue(mAppBarColor)), 0.6f));

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

        sequence.start();
    }
}

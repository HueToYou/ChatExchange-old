package com.huetoyou.chatexchange.ui.misc.tutorial;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.ChatroomRecyclerObject;
import com.huetoyou.chatexchange.ui.misc.RecyclerAdapter;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

public class ChatSlidingMenuTutorial
{
    private Activity activity = null;
    private static final long duration = 500L;
    private static final float interpolatorFactor = 2f;

    public void show(Activity activity)
    {
        this.activity = activity;
        showcaseSlidingMenu();
    }

    private void showcaseSlidingMenu()
    {
        final RecyclerView dummyChats = activity.findViewById(R.id.dummy_chat_list);
        final Drawable ico = activity.getResources().getDrawable(R.mipmap.ic_launcher);
        final RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();
        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(activity, null, swipeManager);
        recyclerAdapter.addItem(new ChatroomRecyclerObject(0, "Example 1", "U", ico, 0, 0, 0));
        recyclerAdapter.addItem(new ChatroomRecyclerObject(1, "Example 2", "U", ico, 0, 0, 1));
        recyclerAdapter.addItem(new ChatroomRecyclerObject(2, "Example 3", "U", ico, 0, 0, 2));

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
            public void onSwipeRight(RecyclerView.ViewHolder viewHolder)
            {
                swipeManager.performFakeSwipe(viewHolder, RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT);
            }

            @Override
            public void onSwipeLeft(RecyclerView.ViewHolder viewHolder)
            {
                swipeManager.performFakeSwipe(viewHolder, RecyclerViewSwipeManager.RESULT_SWIPED_LEFT);
            }
        };

        activity.findViewById(R.id.chatroomsListView).setVisibility(View.GONE);
        dummyChats.setVisibility(View.VISIBLE);

        SimpleTarget target = new SimpleTarget.Builder(activity)
                .setPoint(dummyChats)
                .setRadius(80f)
                .setTitle(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text_title))
                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_text))
                .build();

        Spotlight.with(activity)
                .setDuration(duration)
                .setAnimation(new DecelerateInterpolator(interpolatorFactor))
                .setTargets(target)
                .setOnSpotlightStartedListener(new OnSpotlightStartedListener()
                {
                    @Override
                    public void onStarted()
                    {

                    }
                })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener()
                {
                    @Override
                    public void onEnded()
                    {
                        showcaseRemoveBtn(onSwipeListener, recyclerAdapter, dummyChats);
                    }
                })
                .start();
    }

    private void showcaseRemoveBtn(OnSwipeListener onSwipeListener, RecyclerAdapter recyclerAdapter, final RecyclerView dummyChats)
    {
        onSwipeListener.onSwipeRight(recyclerAdapter.getViewHolderAt(0));

        System.out.println("HUE 1");
        SimpleTarget target2 = new SimpleTarget.Builder(activity)
                .setPoint(recyclerAdapter.getViewHolderAt(0).getCloseChatButton())
                .setRadius(80f)
                .setTitle("HUEUEHUE")
                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_chats_tutorial_swipe_left_text))
                .build();

        Spotlight.with(activity)
                .setDuration(duration)
                .setAnimation(new DecelerateInterpolator(interpolatorFactor))
                .setTargets(target2)
                .setOnSpotlightStartedListener(new OnSpotlightStartedListener()
                {
                    @Override
                    public void onStarted()
                    {

                    }
                })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener()
                {
                    @Override
                    public void onEnded()
                    {
                        showcaseChatFAM(dummyChats);
                        System.out.println("HUE 2");
                    }
                })
                .start();
    }

    private void showcaseChatFAM(final RecyclerView dummyChats)
    {
        System.out.println("HUE 3");
        final FloatingActionMenu chatFam = activity.findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = activity.findViewById(R.id.home_fab);
        final FloatingActionButton add = activity.findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = activity.findViewById(R.id.remove_all_chats_fab);

        SimpleTarget target = new SimpleTarget.Builder(activity)
                .setPoint(chatFam.getMenuButton())
                .setRadius(80f)
                .setTitle(activity.getResources().getString(R.string.tutorial_menu))
                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_FAM_tutorial_text))
                .build();

        Spotlight.with(activity)
                .setDuration(duration)
                .setAnimation(new DecelerateInterpolator(interpolatorFactor))
                .setTargets(target)
                .setOnSpotlightStartedListener(new OnSpotlightStartedListener()
                {
                    @Override
                    public void onStarted()
                    {

                    }
                })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener()
                {
                    @Override
                    public void onEnded()
                    {
                        chatFam.open(true);

                        SimpleTarget homeTarget = new SimpleTarget.Builder(activity)
                                .setPoint(home)
                                .setRadius(80f)
                                .setTitle(activity.getResources().getString(R.string.tutorial_home))
                                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_homeFAB_tutorial_text))
                                .build();

                        SimpleTarget addTarget = new SimpleTarget.Builder(activity)
                                .setPoint(add)
                                .setRadius(80f)
                                .setTitle(activity.getResources().getString(R.string.tutorial_add))
                                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_addChatFAB_tutorial_text))
                                .build();

                        SimpleTarget remAllTarget = new SimpleTarget.Builder(activity)
                                .setPoint(removeAll)
                                .setRadius(80f)
                                .setTitle(activity.getResources().getString(R.string.tutorial_remove_all))
                                .setDescription(activity.getResources().getString(R.string.chatrooms_slidingMenu_removeALlChatsFAB_tutorial_text))
                                .build();

                        Spotlight.with(activity)
                                .setDuration(duration)
                                .setAnimation(new DecelerateInterpolator(interpolatorFactor))
                                .setTargets(homeTarget, addTarget, remAllTarget)
                                .setOnSpotlightEndedListener(new OnSpotlightEndedListener()
                                {
                                    @Override
                                    public void onEnded()
                                    {
                                        chatFam.close(true);
                                        activity.findViewById(R.id.chatroomsListView).setVisibility(View.VISIBLE);
                                        dummyChats.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public interface OnSwipeListener
    {
        void onSwipeLeft(RecyclerView.ViewHolder viewHolder);

        void onSwipeRight(RecyclerView.ViewHolder viewHolder);
    }
}

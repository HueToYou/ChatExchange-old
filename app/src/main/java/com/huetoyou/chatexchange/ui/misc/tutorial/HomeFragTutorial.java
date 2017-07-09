package com.huetoyou.chatexchange.ui.misc.tutorial;

import android.app.Activity;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

public class HomeFragTutorial
{
    private Activity activity = null;
    private static final long duration = 500L;
    private static final float interpolatorFactor = 2f;

    public void show(Activity activity)
    {
        this.activity = activity;
        showcaseHamburgerMenu();
    }

    private void showcaseHamburgerMenu()
    {
        SimpleTarget target = new SimpleTarget.Builder(activity)
                .setPoint(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(1))
                .setRadius(80f)
                .setTitle(activity.getResources().getString(R.string.tutorial_drawer))
                .setDescription(activity.getResources().getString(R.string.homeFrag_hamburger_tutorial_text))
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
                        showcaseMenuBtn();
                    }
                })
                .start();
    }

    private void showcaseMenuBtn()
    {
        SimpleTarget target = new SimpleTarget.Builder(activity)
                .setPoint(Utils.getActionBar(activity.getWindow().getDecorView()).getChildAt(2))
                .setRadius(80f)
                .setTitle(activity.getResources().getString(R.string.tutorial_menu))
                .setDescription(activity.getResources().getString(R.string.homeFrag_options_menu_tutorial_text))
                .build();

        Spotlight.with(activity)
                .setDuration(duration)
                .setAnimation(new DecelerateInterpolator(interpolatorFactor))
                .setTargets(target)
                .start();
    }
}

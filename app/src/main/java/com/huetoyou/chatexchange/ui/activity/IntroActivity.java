package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.huetoyou.chatexchange.R;


public class IntroActivity extends AppIntro
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //noinspection ConstantConditions
        getSupportActionBar().hide();

        // Note here that we DO NOT use setContentView();

        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance("Welcome!","Welcome to Chat Exchange! Thanks for downloading our app :)", R.drawable.ic_smile, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Chat Network", "Chat Exchange is an Android client for the Stack Exchange chat network. You will need a Stack Exchange account in order to use this app.", R.drawable.ic_network, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Notifications", "Chat Exchange makes participating in SE chatrooms easier by sending you push notifications when your username is mentioned", R.drawable.ic_message_notification, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Credits", "This application was developed by:\n\n10,000 monkeys on the Java islands", R.drawable.ic_people, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Bugs?", "If you find a bug or the app crashes on you, please pop us an email at huetoyou@quickmediasolutions.com instead of leaving a 1-star review on the Play Store. We'll do our best to get that bug squashed for you! :D", R.drawable.ic_bug_report, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Tutorial", "This app has a lot of buttons, so here's a quick tutorial to show you around", R.drawable.ic_tutorial, getResources().getColor(R.color.colorPrimaryDark)));

        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorAccentDark));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment)
    {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment)
    {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
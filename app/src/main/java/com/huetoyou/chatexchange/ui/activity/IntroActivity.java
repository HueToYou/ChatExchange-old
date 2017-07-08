package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        addSlide(AppIntroFragment.newInstance("Hellu there!",
                getResources().getString(R.string.intro_slide_welcome_text),
                R.drawable.ic_smile,
                getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance("Chat Network",
                getResources().getString(R.string.intro_slide_chat_network),
                R.drawable.ic_network,
                getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance("Notifications",
                getResources().getString(R.string.intro_slide_notifications),
                R.drawable.ic_message_notification,
                getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance("Credits",
                getResources().getString(R.string.intro_slide_credits),
                R.drawable.ic_people,
                getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance("Bugs?",
                getResources().getString(R.string.intro_slide_bugs),
                R.drawable.ic_bug_report,
                getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance("Tutorial",
                getResources().getString(R.string.intro_slide_tutorial),
                R.drawable.ic_tutorial,
                getResources().getColor(R.color.colorPrimaryDark)));

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
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isFirstRun", false).apply();
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
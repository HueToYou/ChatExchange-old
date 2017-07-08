package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.TutorialStuff;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

public class HelpActivity extends AppCompatActivity
{
    private SharedPreferences.Editor mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(HelpActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBarHue.setActionBarColorToSharedPrefsValue(this);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    public void showTutorial(View v)
    {
        TutorialStuff.resetSpotlights(this);
        mSharedPreferences.putBoolean("runMainTutorial", true).apply();

        finish();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}

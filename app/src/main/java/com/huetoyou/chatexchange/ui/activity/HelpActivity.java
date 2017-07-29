package com.huetoyou.chatexchange.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.TutorialStuff;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

public class HelpActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Set the theme
        ThemeHue.setTheme(HelpActivity.this);

        //Normal OnCreate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //Theme the action bar
        ActionBarHue.setActionBarColorToSharedPrefsValue(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help");
    }

    /*
     * This method is hooked to the "Show Tutorial" button through the XML onClick attribute
     */
    public void showTutorial(View v)
    {
        TutorialStuff.resetSpotlights(this);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}

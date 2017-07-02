package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class TutorialActivity extends AppCompatActivity
{
    private ThemeHue mThemeHue;
    private boolean mOnCreateCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mThemeHue = new ThemeHue();
        mThemeHue.setTheme(this);
        setContentView(R.layout.fragment_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_black_24dp, null);
        drawable.setTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        ViewGroup actionBar = Utils.getActionBar(getWindow().getDecorView());

        mOnCreateCalled = true;

//        new ShowcaseView.Builder(this)
//                .withMaterialShowcase()
//                .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
//                .setContentTitle("ShowcaseView")
//                .setContentText("This is highlighting the Home button")
//                .hideOnTouchOutside()
//                .build();

        new MaterialShowcaseView.Builder(this)
                .setTarget(actionBar.getChildAt(1))
                .setTitleText("HUE")
                .setContentText("Home button")
                .setDismissText("OK")
                .show();

        //displayShowcases();
    }

    /*@Override
    protected void onResume()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
               while (true) {
                   if (!mOnCreateCalled) continue;
                   mThemeHue.setThemeOnResume(TutorialActivity.this, true);
                   break;
               }
            }
        }).start();
        super.onResume();
    }

    private void displayShowcases()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.show_chats_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Show chatrooms")
                .setContentText("Click this button to reveal the chatrooms sliding panel\n\nSwiping inwards from the left edge of the screen has the same effect")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseRemoveChat();
                    }

                })
                .build();
    }

    private void showcaseRemoveChat()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.close_chat_frag)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Remove chatroom")
                .setContentText("Click this button to remove the current chatroom from the sliding panel")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseStar();
                    }

                })
                .build();
    }

    private void showcaseStar()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.star_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Show starred messages")
                .setContentText("This button opens a browser window showing the messages currently on the starwall")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseInfo();
                    }

                })
                .build();
    }

    private void showcaseInfo()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.room_info_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Room info")
                .setContentText("This button display's the current chatrooms's description, and other data")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseOpenInBrowser();
                    }

                })
                .build();
    }

    private void showcaseOpenInBrowser()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.open_in_browser_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Open in browser")
                .setContentText("Click this button to open the current chatroom in a browser window")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        showcaseUsersPanel();
                    }

                })
                .build();
    }

    private void showcaseUsersPanel()
    {
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.show_users_fab)))
                .withNewStyleShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Reveal users")
                .setContentText("This button reveals current users sliding panel\n\nSwiping inwards from the right edge of the screen has the same effect")
                .blockAllTouches()
                .setShowcaseEventListener(new SimpleShowcaseEventListener()
                {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                    {
                        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
                        startActivity(intent);
                        SharedPreferences mSharedPrefs;
                        SharedPreferences.Editor mEditor;
                        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        mEditor = mSharedPrefs.edit();
                        mEditor.putBoolean("isFirstRun", false);
                        mEditor.apply();
                        finish();
                    }

                })
                .build();
    }*/
}

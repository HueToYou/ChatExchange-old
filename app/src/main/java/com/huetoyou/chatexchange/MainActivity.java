package com.huetoyou.chatexchange;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private AccountManager mAccountManager;

    private TabLayout mTabLayout;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getSharedPreferences(getResources().getText(R.string.app_name).toString(), MODE_PRIVATE);
        mEditor = mSharedPrefs.edit();
        mEditor.apply();

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        try {
            mTabLayout.addTab(mTabLayout.newTab().setText("Accounts").setIcon(R.drawable.ic_stackexchange));
            mTabLayout.addTab(mTabLayout.newTab().setText("Ask Ubuntu").setIcon(new GetIcon().execute("https://cdn.sstatic.net/Sites/askubuntu/img/favicon.ico").get()));
//            mTabLayout.addTab(mTabLayout.newTab().setText("Android").setIcon(R.mipmap.ic_launcher));
//            mTabLayout.addTab(mTabLayout.newTab().setText("Español").setIcon(R.mipmap.ic_launcher));
//            mTabLayout.addTab(mTabLayout.newTab().setText("IDK").setIcon(R.mipmap.ic_launcher));
//            mTabLayout.addTab(mTabLayout.newTab().setText("HueChat").setIcon(R.mipmap.ic_launcher));
//            mTabLayout.addTab(mTabLayout.newTab().setText("EdwinK").setIcon(R.mipmap.ic_launcher));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mFragmentManager = getFragmentManager();

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
            int tabIndex = mSharedPrefs.getInt("tabIndex", 0);
            setFragment(mTabLayout.getTabAt(tabIndex));
        } else {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        tabListener();
    }

    private void setFragment(TabLayout.Tab tab) {
        Fragment fragment;

        switch (tab.getPosition()) {
            case 0:
                fragment = new AccountsFragment();
                break;
            default:
                fragment = new ChatFragment();
                break;
        }

        if (fragment instanceof ChatFragment) {
            mEditor.putString("chatTitle", tab.getText().toString());
            mEditor.apply();
        }

        mFragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
    }

    private void tabListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFragment(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void addTab(String chatUrl) {
        String chatName = "Test";
        TabLayout.Tab tab = null;
        try {
            tab = mTabLayout.newTab().setText(chatName).setIcon(new GetIcon().execute(chatUrl).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tab != null) mTabLayout.addTab(tab);
    }

    private class GetIcon extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            Bitmap bmp = null;
            try {
                URL url = new URL(params[0]);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new BitmapDrawable(Resources.getSystem(), bmp);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
        }
    }
}

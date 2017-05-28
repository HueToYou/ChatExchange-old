package com.huetoyou.chatexchange;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPrefs;
    private AccountManager mAccountManager;

    private TabLayout mTabLayout;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getSharedPreferences(getResources().getText(R.string.app_name).toString(), MODE_PRIVATE);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText("Accounts").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 1").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 2").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 3").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 4").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 5").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Chat 6").setIcon(R.mipmap.ic_launcher));

        mFragmentManager = getFragmentManager();

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
            int tabIndex = mSharedPrefs.getInt("tabIndex", 0);
            setFragment(tabIndex);
        } else {
            startActivity(new Intent(this, AuthenticatorActivity.class));
            finish();
        }

        tabListener();
    }

    private void setFragment(int tabIndex) {
        Fragment fragment;

        switch (tabIndex) {
            case 1:
                fragment = new ChatFragment();
                break;
            default:
                fragment = new AccountsFragment();
                break;
        }
        mFragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
    }

    private void tabListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

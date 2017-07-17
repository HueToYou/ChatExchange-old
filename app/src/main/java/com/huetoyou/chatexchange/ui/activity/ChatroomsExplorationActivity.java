package com.huetoyou.chatexchange.ui.activity;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.ChatroomRecyclerObject;
import com.huetoyou.chatexchange.ui.misc.CustomWebView;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.HueUtils;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

import java.net.URL;

public class ChatroomsExplorationActivity extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private BroadcastReceiver hueNetworkStatusChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(this);
        super.onCreate(savedInstanceState);
        normalOnCreate();

        new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {

            }

            @Override
            public void onFailed(String message)
            {
                Intent intent = new Intent(ChatroomsExplorationActivity.this, OfflineActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void normalOnCreate()
    {
        hueNetworkStatusChanged = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
                    {
                        @Override
                        public void onSucceeded(URL url, String data)
                        {

                        }

                        @Override
                        public void onFailed(String message)
                        {
                            Intent hueIntent = new Intent(ChatroomsExplorationActivity.this, OfflineActivity.class);
                            startActivity(hueIntent);
                            finish();
                        }
                    });
                }
            }
        };

        this.registerReceiver(hueNetworkStatusChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_chatrooms_exploration);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        ActionBarHue.setActionBarColorToSharedPrefsValue(this);
        ActionBarHue.setTabBarColorToSharedPrefsValue(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.unregisterReceiver(hueNetworkStatusChanged);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                registerReceiver(hueNetworkStatusChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            }

            @Override
            public void onFailed(String message)
            {
                Intent intent = new Intent(ChatroomsExplorationActivity.this, OfflineActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction)
    {

    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction)
    {

    }

    public static class SE_ChatroomsFrag extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public SE_ChatroomsFrag()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SE_ChatroomsFrag newInstance()
        {
            SE_ChatroomsFrag fragment = new SE_ChatroomsFrag();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_chatrooms_exploration_se, container, false);

            WebView webView = rootView.findViewById(R.id.stars_view);
            CustomWebView customWebView = new CustomWebView(getActivity(),rootView, webView, false);
            customWebView.loadUrl(getResources().getText(R.string.stackexchange).toString());

            return rootView;
        }
    }

    public static class SO_ChatroomsFrag extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public SO_ChatroomsFrag()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SO_ChatroomsFrag newInstance()
        {
            SO_ChatroomsFrag fragment = new SO_ChatroomsFrag();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_chatrooms_exploration_so, container, false);

            WebView webView = rootView.findViewById(R.id.stars_view);
            CustomWebView customWebView = new CustomWebView(getActivity(), rootView, webView, false);
            customWebView.loadUrl(getResources().getText(R.string.stackoverflow).toString());
            return rootView;
        }
    }

    public static class HueFrag extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public HueFrag()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HueFrag newInstance()
        {
            HueFrag fragment = new HueFrag();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_chatrooms_exploration_hue, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("hue3");
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItemAt is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    return SE_ChatroomsFrag.newInstance();

                case 1:
                    return SO_ChatroomsFrag.newInstance();
            }

            return HueFrag.newInstance();
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.tab_se_title);
                case 1:
                    return getResources().getString(R.string.tab_so_title);
                case 2:
                    return getResources().getString(R.string.tab_hue_title);
            }
            return null;
        }
    }
}

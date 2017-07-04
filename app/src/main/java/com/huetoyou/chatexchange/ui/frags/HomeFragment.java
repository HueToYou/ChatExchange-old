package com.huetoyou.chatexchange.ui.frags;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.github.clans.fab.Util;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.activity.WebViewActivity;
import com.huetoyou.chatexchange.ui.misc.TutorialStuff;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.HueUtils;
import com.huetoyou.chatexchange.ui.misc.hue.OtherFabsHue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;

public class HomeFragment extends Fragment
{

    private AccountManager mAccountManager;
    private View view;
    private boolean oncreateHasBeenCalled = false;
    private SharedPreferences mSharedPreferences;
    private WebView webView;

    public HomeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle(getResources().getText(R.string.app_name));

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAccountManager = AccountManager.get(getActivity());

//        view.findViewById(R.id.open_in_webview).setVisibility(View.GONE);
//        setupWebView();

        oncreateHasBeenCalled = true;

        TutorialStuff.homeFragTutorial(getActivity(), view);

        Button chooseSE = view.findViewById(R.id.chooseSEView);
        Button chooseSO = view.findViewById(R.id.chooseSOView);

        chooseSE.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("url", getResources().getText(R.string.stackexchange).toString());
                startActivity(intent);
            }
        });

        chooseSO.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("url", getResources().getText(R.string.stackoverflow).toString());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        //noinspection StatementWithEmptyBody
        if (!getActivity().getSupportFragmentManager().findFragmentByTag("home").isDetached())
        {
            //setChatButtonTint();
        }
        super.onAttach(context);
    }

    @Override
    public void onResume()
    {
        hueAllTheThings();

        //noinspection StatementWithEmptyBody
        if (!getActivity().getSupportFragmentManager().findFragmentByTag("home").isDetached())
        {
            //setChatButtonTint();
        }
//        setChatButtonTint();
        super.onResume();
    }

    /*private void setChatButtonTint() {
        mHueUtils = new Utils();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int initialColor = mSharedPreferences.getInt("default_color", getResources().getColor(R.color.colorPrimary));
        ColorStateList colorStateList = ColorStateList.valueOf(initialColor);
        mHueUtils.showChatsTint(colorStateList, (AppCompatActivity)getActivity());
    }*/

    /*
     * Set appropriate colors
     */

    public void hueTest()
    {
        System.out.println("Hue");

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                //noinspection StatementWithEmptyBody
                while (!oncreateHasBeenCalled) ;

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        hueAllTheThings();
                    }
                });
            }
        };
        thread.start();
    }

    private void hueAllTheThings()
    {

        ActionBarHue.setActionBarColorToSharedPrefsValue((AppCompatActivity) getActivity());
        OtherFabsHue.setAddChatFabColorToSharedPrefsValue((AppCompatActivity) getActivity());
    }
}

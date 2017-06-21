package com.huetoyou.chatexchange.ui.frags;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ChatFragFabsHue;
import com.huetoyou.chatexchange.ui.misc.hue.OtherFabsHue;

public class HomeFragment extends Fragment {

    private AccountManager mAccountManager;
    private View view;
    private Utils mHueUtils;
    private ActionBarHue actionBarHue;
    private OtherFabsHue otherFabsHue;
    private boolean oncreateHasBeenCalled = false;
    private SharedPreferences mSharedPreferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle(getResources().getText(R.string.app_name));

        mHueUtils = new Utils();
        actionBarHue = new ActionBarHue();
        otherFabsHue = new OtherFabsHue();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAccountManager = AccountManager.get(getActivity());

        oncreateHasBeenCalled = true;

        return view;
    }

    @Override
    public void onAttach(Context context) {
        if (!getActivity().getSupportFragmentManager().findFragmentByTag("home").isDetached()) {
            //setChatButtonTint();
        }
        super.onAttach(context);
    }

    public void hueTest()
    {
        System.out.println("Hue");

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                while(!oncreateHasBeenCalled);

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

        actionBarHue.setActionBarColorToSharedPrefsValue((AppCompatActivity) getActivity());
        otherFabsHue.setAddChatFabColorToSharedPrefsValue((AppCompatActivity) getActivity());
    }

    @Override
    public void onResume()
    {
        hueAllTheThings();

        if (!getActivity().getSupportFragmentManager().findFragmentByTag("home").isDetached())
        {
            //setChatButtonTint();
        }
//        setChatButtonTint();
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*private void setChatButtonTint() {
        mHueUtils = new Utils();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int initialColor = mSharedPreferences.getInt("default_color", getResources().getColor(R.color.colorPrimary));
        ColorStateList colorStateList = ColorStateList.valueOf(initialColor);
        mHueUtils.showChatsTint(colorStateList, (AppCompatActivity)getActivity());
    }*/
}

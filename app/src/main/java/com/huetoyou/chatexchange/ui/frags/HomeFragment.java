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

        return view;
    }

    @Override
    public void onAttach(Context context) {
        if (!getActivity().getSupportFragmentManager().findFragmentByTag("home").isDetached()) {
            //setChatButtonTint();
        }
        super.onAttach(context);
    }

    @Override
    public void onResume()
    {
        actionBarHue.setActionBarColorToSharedPrefsValue((AppCompatActivity) getActivity());
        otherFabsHue.setAddChatFabColorToSharedPrefsValue((AppCompatActivity) getActivity());

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

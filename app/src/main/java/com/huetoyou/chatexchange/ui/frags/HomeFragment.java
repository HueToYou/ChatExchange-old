package com.huetoyou.chatexchange.ui.frags;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.OtherFabsHue;

public class HomeFragment extends Fragment
{
    private View view;
    private boolean oncreateHasBeenCalled = false;

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
        oncreateHasBeenCalled = true;

        return view;
    }

    @Override
    public void onResume()
    {
        hueAllTheThings();

        super.onResume();
    }

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

package com.huetoyou.chatexchange.ui.frags;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huetoyou.chatexchange.R;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private View view;

    private LinearLayout mUsersLayout;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getResources().getText(R.string.app_name).toString(), Context.MODE_PRIVATE);

        mUsersLayout = (LinearLayout) view.findViewById(R.id.users_scroll);

        Bundle args = getArguments();

        parseUsers(args.getString("chatUrl", "ERROR"));
        Log.e("ChatURL", args.getString("chatUrl"));

        getActivity().setTitle(args.getString("chatTitle", "Error"));

        //call addUser() here somehow....
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void parseUsers(final String... params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String html = "";
                SparseArray<String> names = new SparseArray<>();

                URL url;
                InputStream is = null;
                BufferedReader br;
                String line;

                try {
                    html = Jsoup.connect(params[0]).get().html();
                    Log.e("HTML", html);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException ioe) {
                        // nothing to see here
                    }
                }
                Pattern p = Pattern.compile("<li class=\"present-user user-container(.+?)\"(.+?)>(.+?)</li>");
                Matcher m = p.matcher(html);
                String name = null;

                try {
                    boolean idk = m.find();
                    name = m.group();
                    Log.e("T", name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addUser(String name, String imgUrl) {
        Bundle args = new Bundle();
        args.putString("userName", name);
        args.putString("userAvatarUrl", imgUrl);

        UserTileFragment userTileFragment = new UserTileFragment();
        userTileFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.users_scroll, userTileFragment).commit();
    }
}

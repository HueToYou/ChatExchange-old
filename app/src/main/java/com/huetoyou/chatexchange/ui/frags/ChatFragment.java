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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import com.huetoyou.chatexchange.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
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
                Document html = new Document("");
                SparseArray<String> names = new SparseArray<>();

                String users;

                try {
                    html = Jsoup.connect(params[0]).get();
//                    Log.e("HTML", html.html());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Elements el = html.select("script");
                if (el.hasAttr("type")) el = html.select("script");

                ArrayList<String> data = new ArrayList<>();

                users = el.html();
                String users2 = "";

                Pattern p = Pattern.compile("\\{id:(.*?)\\}");
                Matcher m = p.matcher(users);

                while (!m.hitEnd()) {
                    if (m.find()) {
                        try {
                            data.add(m.group());
                            users2 = users2.concat(m.group());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                users2 = "{\"users\": ["
                        .concat(users2)
                        .concat("]}")
                        .replace("(", "")
                        .replace(")", "")
                        .replace("id:", "\"id\":")
                        .replace("name", "\"name\"")
                        .replace("email_hash", "\"email_hash\"")
                        .replace("reputation", "\"reputation\"")
                        .replace("last_post", "\"last_post\"")
                        .replace("is_moderator", "\"is_moderator\"")
                        .replace("is_owner", "\"is_owner\"")
                        .replace("true", "\"true\"")
                        .replace("}{", "},{")
                        .replace("!", "");

                try {
                    JSONObject object = new JSONObject(users2);
                    JSONArray jArray = object.getJSONArray("users");

                    for (int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject jsonObject = jArray.getJSONObject(i);

                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String icon = jsonObject.getString("email_hash");
                        if (!(icon.contains("http://") || icon.contains("https://"))) icon = "https://www.gravatar.com/avatar/".concat(icon).concat("?d=identicon");
                        if (name.equals("Android Dev")) Log.e("ICON", icon);

                        addUser(name, icon);
                    }
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

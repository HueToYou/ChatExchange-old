package com.huetoyou.chatexchange.ui.frags;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import com.huetoyou.chatexchange.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ChatFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private View view;

    private LinearLayout mUsersLayout;
    private ToggleButton mShowUsers;
    private Button mOpenInBrowser;

    private @ColorInt int mAppBarColor;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean usersShown = mSharedPreferences.getBoolean("showUserList", false);

        mUsersLayout = (LinearLayout) view.findViewById(R.id.user_list_layout);
        mUsersLayout.setVisibility(usersShown ? View.VISIBLE : View.GONE);

        mShowUsers = (ToggleButton) view.findViewById(R.id.show_user_list);
        mShowUsers.setChecked(usersShown);

        mOpenInBrowser = (Button) view.findViewById(R.id.open_in_browser);

        Bundle args = getArguments();
        String chatUrl = args.getString("chatUrl", "ERROR");

        if (mSharedPreferences.getBoolean("dynamicallyColorBar", false)) {
            try {
                new GetColorInt().execute(chatUrl);
            } catch (Exception e) {
                mAppBarColor = -1;
                e.printStackTrace();
            }
        }

        addChatButtons(chatUrl);
        parseUsers(chatUrl);

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
//                        .replace("true", "\"true\"")
                        .replace("}{", "},{")
                        .replace("!", "");

                try {
                    JSONObject object = new JSONObject(users2);
                    JSONArray jArray = object.getJSONArray("users");

                    for (int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject jsonObject = jArray.getJSONObject(i);

                        int id = jsonObject.getInt("id");
                        int lastPost = jsonObject.getInt("last_post");
                        int rep = jsonObject.getInt("reputation");

                        boolean isMod = jsonObject.has("is_moderator") && jsonObject.getBoolean("is_moderator");
                        boolean isOwner = jsonObject.has("is_owner") && jsonObject.getBoolean("is_owner");

                        String name = jsonObject.getString("name");
                        String icon = jsonObject.getString("email_hash");

                        if (!(icon.contains("http://") || icon.contains("https://"))) icon = "https://www.gravatar.com/avatar/".concat(icon).concat("?d=identicon");
                        if (name.equals("Android Dev")) Log.e("ICON", icon);

                        addUser(name, icon, id, lastPost, rep, isMod, isOwner, params[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addUser(final String name, final String imgUrl, final int id, final int lastPost, final int rep, final boolean isMod, final boolean isOwner, final String chatUrl) {
        Bundle args = new Bundle();
        args.putString("userName", name);
        args.putString("userAvatarUrl", imgUrl);
        args.putString("chatUrl", chatUrl);

        args.putInt("id", id);
        args.putInt("lastPost", lastPost);
        args.putInt("rep", rep);

        args.putBoolean("isMod", isMod);
        args.putBoolean("isOwner", isOwner);

        UserTileFragment userTileFragment = new UserTileFragment();
        userTileFragment.setArguments(args);

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.users_scroll, userTileFragment).commit();
    }

    private void addChatButtons(final String url) {
        mShowUsers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUsersLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mSharedPreferences.edit().putBoolean("showUserList", isChecked).apply();
            }
        });

        mOpenInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    private class GetColorInt extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];

            try {
                Document doc = Jsoup.connect(url).get();

                Elements styles = doc.select("link");
                Element element = new Element("hue");

                for (int i = 0; i < styles.size(); i++) {
                    Element current = styles.get(i);

                    if (current.hasAttr("href") && current.attr("rel").equals("stylesheet")) {
                        element = current;
                        break;
                    }
                }

                String link = "";
                if (element.hasAttr("href")) {
                    link = element.attr("href");
                    if (!(link.contains("http://") || link.contains("https://"))) link = "https:".concat(link);
                }

                Log.e("L", link);

                URL url1 = new URL(link);

                InputStream inStr = url1.openStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStr));
                String line;
                String css = "";

                while ((line = bufferedReader.readLine()) != null) {
                    css = css.concat(line);
                }

                Log.e("In", css);

                Pattern p = Pattern.compile("\\.msparea\\{(.+?)\\}");
                Matcher m = p.matcher(css);
                String a = "";

                if (m.find()) {
                    a = m.group();
                    Log.e("A", a);
                }

                p = Pattern.compile("color:(.*?);");
                m = p.matcher(a);

                String colorHex = "#000000";

                if (m.find()) {
                    Log.e("MGROUP", m.group());
                    colorHex = m.group().replace("color", "").replace(":", "").replace(";", "").replaceAll(" ", "");
                }

                return Color.parseColor(colorHex);
            } catch (Exception e) {
                e.printStackTrace();
                return Color.parseColor("#000000");
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mAppBarColor = integer;
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mAppBarColor));
        }
    }
}

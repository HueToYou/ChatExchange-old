package com.huetoyou.chatexchange.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.activity.WebViewActivity;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import jodd.io.filter.RegExpFileFilter;

public class ChatFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private View view;

    private @ColorInt int mAppBarColor;
    private SlidingMenu mSlidingMenu;

    private HueUtils hueUtils = null;
    private Spanned mChatDesc;
    private ArrayList<String> mChatTags = new ArrayList<>();
    private Spanned mChatTagsSpanned;
    private String mChatUrl;
    //    private Spanned mStarsSpanned;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        hueUtils = new HueUtils();

        // configure the SlidingMenu
        mSlidingMenu = new SlidingMenu(getActivity());
        mSlidingMenu.setMode(SlidingMenu.RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.users_slideout);

        mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                if (((MainActivity)getActivity()).getmChatroomSlidingMenu().isMenuShowing()) ((MainActivity)getActivity()).getmChatroomSlidingMenu().showContent(true);
            }
        });

        Bundle args = getArguments();
        mChatUrl = args.getString("chatUrl", "ERROR");

        new GetDesc().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrl);
        new GetTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrl);

        mAppBarColor = args.getInt("chatColor", -1);

        addChatButtons(mChatUrl);
        ParseUsers parseUsers = new ParseUsers();
        parseUsers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mChatUrl);

        getActivity().setTitle(args.getString("chatTitle", "Error"));
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

    private class ParseUsers extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Document html = new Document("");

            String users;

            try {
                html = Jsoup.connect(params[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Elements el = html.select("script");
            if (el.hasAttr("type")) el = html.select("script");

            users = el.html();
            String users2 = "";

            Pattern p = Pattern.compile("\\{id:(.*?)\\}");
            Matcher m = p.matcher(users);

            while (!m.hitEnd()) {
                if (m.find()) {
                    try {
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

                    addUser(name, icon, id, lastPost, rep, isMod, isOwner, params[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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
        fragmentManager.beginTransaction().add(R.id.users_scroll_slide, userTileFragment).commit();
    }

    private void addChatButtons(final String url) {

        FloatingActionButton openInBrowser = (FloatingActionButton) view.findViewById(R.id.open_in_browser_fab);
        FloatingActionButton roomInfo = (FloatingActionButton) view.findViewById(R.id.room_info_fab);
        FloatingActionButton stars = (FloatingActionButton) view.findViewById(R.id.star_fab);
        FloatingActionButton showChats = (FloatingActionButton) view.findViewById(R.id.show_chats_fab);
        FloatingActionButton users = (FloatingActionButton) view.findViewById(R.id.show_users_fab);

        openInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        roomInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog d = new AlertDialog.Builder(getActivity())
                        .setTitle("Info")
                        .setView(R.layout.room_desc)
                        .setPositiveButton(getResources().getText(R.string.generic_ok), null)
                        .create();
                d.show();

                TextView desc = (TextView) d.findViewById(R.id.desc_text);
                desc.setText(mChatDesc);
                desc.setMovementMethod(LinkMovementMethod.getInstance());

                TextView tag = (TextView) d.findViewById(R.id.tag_text);
                tag.setText(mChatTagsSpanned);
                tag.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", mChatUrl.replace("rooms/", "rooms/info/").replace("#", "").concat("/?tab=stars"));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        showChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.getmChatroomSlidingMenu().toggle();
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                mSlidingMenu.toggle();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mSharedPreferences.getBoolean("dynamicallyColorBar", false)) {
            hueUtils.setActionBarColor((AppCompatActivity) getActivity(), mAppBarColor);
            hueUtils.setChatFragmentFabColor((AppCompatActivity) getActivity(), mAppBarColor);
            hueUtils.setAddChatFabColor((AppCompatActivity) getActivity(), mAppBarColor);
        }

        else
        {
            hueUtils.setActionBarColorDefault((AppCompatActivity) getActivity());
            hueUtils.setChatFragmentFabColorDefault((AppCompatActivity) getActivity());
            hueUtils.setAddChatFabColorDefault((AppCompatActivity) getActivity());
        }
    }

    private class GetDesc extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Elements divs = Jsoup.connect(params[0]).get().select("div");

                for (Element e : divs) {
                    if (e.hasAttr("id") && e.attr("id").equals("roomdesc")) return e.html();
                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mChatDesc = Html.fromHtml("<b>Desc: </b>" + s);
        }
    }

    private class GetTags extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                Elements divs = Jsoup.connect(params[0]).get().select("div").select("a");
                ArrayList<String> tagList = new ArrayList<>();

                for (Element e : divs) {
                    if (e.hasAttr("class") && e.attr("class").equals("tag")) {
                        tagList.add(e.html());
                    }
                }

                return tagList;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            mChatTags = strings;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String tags = "";
                    if (mChatTags != null) {
                        tags = mChatTags.toString();
                        tags = tags.replace("[", "").replace("]", "");
                    }

                    mChatTagsSpanned = Html.fromHtml("<b>Tags: </b>" + tags);
                }
            }).start();
        }
    }

    public SlidingMenu getmSlidingMenu() {
        return mSlidingMenu;
    }

//    private class GetStars extends AsyncTask<String, Void, ArrayList<String >> {
//        @Override
//        protected ArrayList<String> doInBackground(String... params) {
//            String chatUrl = params[0];
//            String starUrl = chatUrl.replace("rooms/", "rooms/info/").replace("#", "").concat("/?tab=stars");
//
//            ArrayList<String> ret = new ArrayList<>();
//
//            try {
//                Elements monologues = Jsoup.connect(starUrl).get().select("div");
//                for (Element e : monologues) {
//                    if (e.hasAttr("class") && e.attr("class").contains("monologue")) ret.add(e.toString());
//                }
//
//                return ret;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<String> strings) {
//            String stars = strings.toString().replace("[", "").replace("]", "").replace(">,", ">-----").replace("href=\"//", "href=\"http://");
//            mStarsSpanned = Html.fromHtml(stars);
//        }
//    }
}

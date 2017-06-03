package com.huetoyou.chatexchange.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

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

    private @ColorInt int mAppBarColor;
    private SlidingMenu mSlidingMenu;

    private HueUtils hueUtils = null;
    private Spanned mChatDesc;
    private ArrayList<String> mChatTags = new ArrayList<>();
    private Spanned mChatTagsSpanned;

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
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        //        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //        mSlidingMenu.setBehindOffset((int)(dpWidth + getResources().getDimension(R.dimen.user_tile_width)));
        //        mSlidingMenu.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.users_slideout);

        Bundle args = getArguments();
        mChatDesc = Html.fromHtml("<b>Desc: </b>" + args.getString("chatDesc", "ERROR"));
        mChatTags = args.getStringArrayList("chatTags");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String s : mChatTags) {
//                    mChatTagsSpanned = mChatTagsSpanned. Html.fromHtml(s);
                }
            }
        }).start();

        String chatUrl = args.getString("chatUrl", "ERROR");

        mAppBarColor = args.getInt("AppBarColor", -1);

        addChatButtons(chatUrl);
        ParseUsers asyncTask = new ParseUsers();
        CancelTask canceller = new CancelTask(asyncTask);
        Handler handler = new Handler();
        handler.postDelayed(canceller, 10000);
        asyncTask.execute(chatUrl);

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

    private class CancelTask implements Runnable {
        private AsyncTask task;

        public CancelTask(AsyncTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (task.getStatus() == AsyncTask.Status.RUNNING )
                task.cancel(true);
        }
    }

    private class ParseUsers extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Document html = new Document("");
            SparseArray<String> names = new SparseArray<>();

            String users;

            try {
                html = Jsoup.connect(params[0]).get();
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

                    addUser(name, icon, id, lastPost, rep, isMod, isOwner, params[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    private void parseUsers(final String... params) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
//    }

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

        FloatingActionButton showUsers = (FloatingActionButton) view.findViewById(R.id.show_users_fab);
        FloatingActionButton openInBrowser = (FloatingActionButton) view.findViewById(R.id.open_in_browser_fab);
        FloatingActionButton roomInfo = (FloatingActionButton) view.findViewById(R.id.room_info_fab);

        showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggle();
            }
        });

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
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mSharedPreferences.getBoolean("dynamicallyColorBar", false)) {
            System.out.println("hue.....");
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
}

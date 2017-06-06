package com.huetoyou.chatexchange.ui.frags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.MainActivity;
import com.huetoyou.chatexchange.ui.activity.WebViewActivity;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private View view;

    private @ColorInt int mAppBarColor;
    private SlidingMenu mSlidingMenu;

    private EditText messageToSend;
    private HorizontalScrollView pingSuggestionsScrollView;

    private HueUtils hueUtils = null;
    private Spanned mChatDesc;
    private ArrayList<String> mChatTags = new ArrayList<>();
    private Spanned mChatTagsSpanned;
    private String mChatUrl;
    ArrayList<Bundle> mUserInfo = new ArrayList<>();
    //    private Spanned mStarsSpanned;

    public static final String USER_NAME_KEY = "userName";
    public static final String USER_AVATAR_URL_KEY = "userAvatarUrl";
    public static final String USER_URL_KEY = "chatUrl";
    public static final String USER_ID_KEY = "id";
    public static final String USER_LAST_POST_KEY = "lastPost";
    public static final String USER_REP_KEY = "rep";
    public static final String USER_IS_MOD_KEY = "isMod";
    public static final String USER_IS_OWNER_KEY = "isOwner";
    private FragmentManager mFragmentManager;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        messageToSend = (EditText) view.findViewById(R.id.messageToSend);
        pingSuggestionsScrollView = (HorizontalScrollView) view.findViewById(R.id.pingSuggestionsScrollView);

        messageToSend.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                {
                    Toast.makeText(getActivity(), "Got focus",
                            Toast.LENGTH_LONG).show();
                    pingSuggestionsScrollView.setVisibility(View.VISIBLE);
                }
            }
        });

        mFragmentManager = getFragmentManager();

        hueUtils = new HueUtils();

        // configure the SlidingMenu
        mSlidingMenu = new SlidingMenu(getActivity());
        mSlidingMenu.setMode(SlidingMenu.RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        mSlidingMenu.setBehindWidthRes(R.dimen.sliding_menu_width);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.users_slideout);

        mSlidingMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
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

        setupMessagePingList();

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

    private void setupMessagePingList() {
        EditText message = (EditText) view.findViewById(R.id.messageToSend);

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Fragment> fragments = new ArrayList<Fragment>();

                if (s.toString().contains("@")) {
                    for (Bundle args : mUserInfo) {
                        UsernameTilePingFragment pingFragment = new UsernameTilePingFragment();
                        pingFragment.setArguments(args);
                        String name = args.getString(USER_NAME_KEY);
                        String currentName = s.toString();

                        Pattern p = Pattern.compile("\\.*@(.+?).*");
                        Matcher m = p.matcher(currentName);

                        try {
                            while (!m.hitEnd()) {
                                if (m.find()) {
                                    currentName = m.group().replace("@", "");
                                    Log.e("NAME", currentName);
                                }
                            }
                        } catch (IllegalStateException e) {
//                            e.printStackTrace()
                        }

                        if (name.replace(" ", "").toLowerCase().contains(currentName)) {
                            fragments.add(pingFragment);
                        }
                    }
                }

                mFragmentManager.beginTransaction().replace(R.id.pingSuggestions, new Fragment()).commit();

                for (Fragment f : fragments) {
                    mFragmentManager.beginTransaction().add(R.id.pingSuggestions, f, "pingFrag").commit();
                }
            }
        });
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
        args.putString(USER_NAME_KEY, name);
        args.putString(USER_AVATAR_URL_KEY, imgUrl);
        args.putString(USER_URL_KEY, chatUrl);

        args.putInt(USER_ID_KEY, id);
        args.putInt(USER_LAST_POST_KEY, lastPost);
        args.putInt(USER_REP_KEY, rep);

        args.putBoolean(USER_IS_MOD_KEY, isMod);
        args.putBoolean(USER_IS_OWNER_KEY, isOwner);

        UserTileFragment userTileFragment = new UserTileFragment();
        userTileFragment.setArguments(args);

        mUserInfo.add(args);
        mFragmentManager.beginTransaction().add(R.id.users_scroll_slide, userTileFragment).commit();
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

                TextView url = (TextView) d.findViewById(R.id.url_text);
                url.setText(Html.fromHtml("<b>URL: </b><a href=\"".concat(mChatUrl).concat("\">").concat(mChatUrl).concat("</a>")));
                url.setMovementMethod(LinkMovementMethod.getInstance());
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

    public ArrayList<Bundle> getmUserInfo() {
        return mUserInfo;
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

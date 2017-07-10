package com.huetoyou.chatexchange.ui.frags;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.activity.WebViewActivity;
import com.huetoyou.chatexchange.ui.misc.TutorialStuff;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ChatFragFabsHue;
import com.huetoyou.chatexchange.ui.misc.hue.OtherFabsHue;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFragment extends Fragment
{

    private SharedPreferences mSharedPreferences;
    private View view;
    private boolean oncreateHasBeenCalled = false;

    private @ColorInt
    int mAppBarColor;
    private SlidingMenu mSlidingMenu;

    private EditText messageToSend;
    private HorizontalScrollView pingSuggestionsScrollView;

    private Spanned mChatDesc;
    private ArrayList<String> mChatTags = new ArrayList<>();
    private Spanned mChatTagsSpanned;
    private String mChatUrl;
    private final ArrayList<Bundle> mUserInfo = new ArrayList<>();
    private final ArrayList<UserTileFragment> mUserTiles = new ArrayList<>();
    //    private Spanned mStarsSpanned;

    public static final String USER_NAME_KEY = "userName";
    private static final String USER_AVATAR_URL_KEY = "userAvatarUrl";
    private static final String USER_URL_KEY = "chatUrl";
    private static final String USER_ID_KEY = "id";
    private static final String USER_LAST_POST_KEY = "lastPost";
    private static final String USER_REP_KEY = "rep";
    private static final String USER_IS_MOD_KEY = "isMod";
    private static final String USER_IS_OWNER_KEY = "isOwner";
    private static final String CHAT_HOST_DOMAIN = "hostDomain";

    private FragmentManager mFragmentManager;
    private EditText mMessage;

    private RequestFactory mRequestFactory;
    private String mChatTitle;
    private String mChatDomain;
    private Integer mChatId;

    private SwipeRefreshLayout mRefreshLayout;

    public ChatFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mRequestFactory = new RequestFactory("t=NETipskrs%2bbmhDM1HqswLwfyvDthu2SS");

        messageToSend = view.findViewById(R.id.messageToSend);
        pingSuggestionsScrollView = view.findViewById(R.id.pingSuggestionsScrollView);

        mFragmentManager = getFragmentManager();

        mSlidingMenu = ((MainActivity) getActivity()).getCurrentUsers_SlidingMenu();

        mRefreshLayout = mSlidingMenu.findViewById(R.id.users_refresh_view);
        mRefreshLayout.setScrollContainer(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                doUserParse();
            }
        });

        messageToSend.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    pingSuggestionsScrollView.setVisibility(View.VISIBLE);
                }
            }
        });

        Bundle args = getArguments();
        mChatUrl = args.getString("chatUrl", "ERROR");
        mChatTitle = args.getString("chatTitle", "ERROR");
        mChatId = args.getInt("chatId", -1);

        mAppBarColor = args.getInt("chatColor", -1);

        showThatFam();

        addChatButtons(mChatUrl);

        mRequestFactory.get(mChatUrl, true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                GetDesc getDesc = GetDesc.newInstance(new DescGotten()
                {
                    @Override
                    public void onSuccess(String desc)
                    {
                        mChatDesc = Html.fromHtml("<b>Desc: </b>" + desc);
                    }

                    @Override
                    public void onFail(String message)
                    {

                    }
                });

                getDesc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

                GetTags getTags = GetTags.newInstance(new TagsGotten()
                {
                    @Override
                    public void onSuccess(ArrayList<String> tabList)
                    {
                        mChatTags = tabList;

                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                String tags = "";
                                if (mChatTags != null)
                                {
                                    tags = mChatTags.toString();
                                    tags = tags.replace("[", "").replace("]", "");
                                }

                                mChatTagsSpanned = Html.fromHtml("<b>Tags: </b>" + tags);
                            }
                        }).start();
                    }

                    @Override
                    public void onFail(String message)
                    {

                    }
                });
                getTags.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
            }

            @Override
            public void onFailed(String message)
            {

            }
        });

        doUserParse();

        getActivity().setTitle(mChatTitle);

        setupMessagePingList();
        setupMessages();

        mChatDomain = mSharedPreferences.getString(CHAT_HOST_DOMAIN.concat(mChatUrl), null);

        if (mChatDomain == null || mChatDomain.isEmpty())
        {
            if (mChatUrl.contains("overflow"))
            {
                mChatDomain = "stackoverflow.com";
            }
            else
            {
                mRequestFactory.get(mChatUrl, true, new RequestFactory.Listener()
                {
                    @Override
                    public void onSucceeded(URL url, String data)
                    {
                        GetHostDomainFromHtml.newInstance(new DomainFoundListener()
                        {
                            @Override
                            public void onSuccess(String text)
                            {
                                mSharedPreferences.edit().putString(CHAT_HOST_DOMAIN.concat(mChatUrl), text).apply();
                                mChatDomain = text;
                            }

                            @Override
                            public void onFail(String text)
                            {
                                mChatDomain = text;
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
                    }

                    @Override
                    public void onFailed(String message)
                    {
                        Log.e("WHOOPS", message);
                        mChatDomain = "Not Found";
                    }
                });
            }
        }

        oncreateHasBeenCalled = true;

        TutorialStuff.chatFragTutorial(getActivity(), view, mAppBarColor);

        return view;
    }

    private void doUserParse() {
        mRequestFactory.get(mChatUrl, true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                ParseUsers parseUsers = ParseUsers.newInstance(new UserParsed()
                {
                    @Override
                    public void onSuccess(String usersJson)
                    {
                        for (int i = 0; i < mFragmentManager.getFragments().size(); i++) {
                            Fragment fragment = mFragmentManager.getFragments().get(i);

                            if (fragment.getTag() != null && fragment.getTag().contains("user")) {
                                mFragmentManager.beginTransaction().hide(fragment).commit();
                            }
                        }

                        try
                        {
                            JSONObject object = new JSONObject(usersJson);
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

                                if (!(icon.contains("http://") || icon.contains("https://")))
                                {
                                    icon = "https://www.gravatar.com/avatar/".concat(icon).concat("?d=identicon");
                                }

                                if (mFragmentManager.findFragmentByTag("user_" + id) == null)
                                {
                                    addUser(name, icon, id, lastPost, rep, isMod, isOwner, mChatUrl);
                                } else {
                                    mFragmentManager.beginTransaction().attach(mFragmentManager.findFragmentByTag("user_" + id)).show(mFragmentManager.findFragmentByTag("user_" + id)).commit();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFail(String message)
                    {

                    }
                });
                parseUsers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
            }

            @Override
            public void onFailed(String message)
            {

            }
        });
    }

    /*
     * Handle loading messages
     */

    private void setupMessages()
    {
        mRequestFactory.get(mChatUrl, true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(final URL url, final String data)
            {
                processMessageViews(url, data);

            }

            @Override
            public void onFailed(String message)
            {

            }
        });
    }

    private void processMessageViews(URL url, String html)
    {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("user-container");

        for (Element e : elements)
        {
            Elements link = e.select("a");
            Element signature = new Element("");

            for (Element e1 : link)
            {
                if (e1.hasAttr("class") && e1.attr("class").equals("signature"))
                {
                    signature = e1;
                    break;
                }
            }

        }
    }

    /*
     * Set proper coloring for the chat
     */

    private void hueAllTheThings()
    {
        if (mSharedPreferences.getBoolean("dynamicallyColorBar", false))
        {
            ActionBarHue.setActionBarColor((AppCompatActivity) getActivity(), mAppBarColor);
            ChatFragFabsHue.setChatFragmentFabColor((AppCompatActivity) getActivity(), mAppBarColor);
            OtherFabsHue.setAddChatFabColor((AppCompatActivity) getActivity(), mAppBarColor);
        }
        else
        {
            ActionBarHue.setActionBarColorToSharedPrefsValue((AppCompatActivity) getActivity());
            ChatFragFabsHue.setChatFragmentFabColorToSharedPrefsValue((AppCompatActivity) getActivity());
            OtherFabsHue.setAddChatFabColorToSharedPrefsValue((AppCompatActivity) getActivity());
        }

        getActivity().setTitle(mChatTitle);
    }

    public void hueTest()
    {
        System.out.println("Hue");

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                //noinspection StatementWithEmptyBody

                while (true)
                {
                    if (!oncreateHasBeenCalled)
                    {
                        continue;
                    }
                    break;
                }

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

    /*
     * Handle ping suggestions
     */

    private void setupMessagePingList()
    {
        mMessage = view.findViewById(R.id.messageToSend);

        mMessage.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                ArrayList<UsernameTilePingFragment> fragments = new ArrayList<>();

                if (s.toString().contains("@"))
                {

                    for (UserTileFragment tile : mUserTiles)
                    {
                        Bundle args = tile.getArguments();
                        UsernameTilePingFragment pingFragment = UsernameTilePingFragment.newInstance(tile, new SetTabCompleteName()
                        {
                            @Override
                            public void setName(UsernameTilePingFragment usernameTilePingFragment)
                            {
                                setTabCompleteName(usernameTilePingFragment);
                            }
                        });
                        pingFragment.setArguments(args);
                        String name = args.getString(USER_NAME_KEY);
                        String currentName = s.toString();

                        Pattern p = Pattern.compile("\\B@(.+?)\\b");
                        Matcher m = p.matcher(currentName);

                        try
                        {
                            while (!m.hitEnd())
                            {
                                if (m.find())
                                {
                                    currentName = m.group().replace("@", "");
//                                    Log.e("NAME", currentName);
                                }
                            }
                        }
                        catch (IllegalStateException e)
                        {
//                            e.printStackTrace()
                        }

                        assert name != null;
                        if (name.replace(" ", "").toLowerCase().startsWith(currentName.toLowerCase()))
                        {
                            fragments.add(pingFragment);
                        }
                    }
                }
                mFragmentManager.beginTransaction().replace(R.id.pingSuggestions, new Fragment()).commit();

                for (UsernameTilePingFragment f : fragments)
                {
                    mFragmentManager.beginTransaction().add(R.id.pingSuggestions, f, "pingFrag").commit();
                }
            }
        });
    }

    private void setTabCompleteName(UsernameTilePingFragment usernameTilePingFragment)
    {
//        Toast.makeText(getActivity(), usernameTilePingFragment.getmUsername(), Toast.LENGTH_SHORT).show();
        String name = usernameTilePingFragment.getmUsername();
        name = name.replace(" ", "");
        String currentText = mMessage.getText().toString();

        Pattern p = Pattern.compile("\\B@(.+?)\\b");
        Matcher m = p.matcher(currentText);

        while (!m.hitEnd())
        {
            if (m.find() && name.toLowerCase().contains(m.group().replace("@", "").toLowerCase()))
            {
                String before = currentText.substring(0, currentText.toLowerCase().lastIndexOf(m.group().toLowerCase()));
                String after = currentText.substring(currentText.toLowerCase().lastIndexOf(m.group().toLowerCase()) + m.group().length());
                String middle = "@" + name;

                mMessage.setText(before.concat(middle).concat(after));
                mMessage.setSelection(mMessage.getText().toString().length());
            }
        }
    }

    public interface SetTabCompleteName
    {
        void setName(UsernameTilePingFragment usernameTilePingFragment);
    }

    /*
     * Parse userdata from URL
     */

    private static class ParseUsers extends AsyncTask<String, Void, String>
    {
        private final UserParsed mUserParsed;

        static ParseUsers newInstance(UserParsed userParsed)
        {
            return new ParseUsers(userParsed);
        }

        ParseUsers(UserParsed userParsed)
        {
            mUserParsed = userParsed;
        }

        @Override
        protected String doInBackground(String... params)
        {
            Document html = new Document("");

            String users;

            try
            {
                html = Jsoup.parse(params[0]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Elements el = html.select("script");
            if (el.hasAttr("type"))
            {
                el = html.select("script");
            }

            users = el.html();
            String users2 = "";

            Pattern p = Pattern.compile("\\{id:(.*?)\\}");
            Matcher m = p.matcher(users);

            while (!m.hitEnd())
            {
                if (m.find())
                {
                    try
                    {
                        users2 = users2.concat(m.group());
                    }
                    catch (Exception e)
                    {
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
                    .replace(" name", " \"name\"")
                    .replace("email_hash", "\"email_hash\"")
                    .replace("reputation", "\"reputation\"")
                    .replace("last_post", "\"last_post\"")
                    .replace("is_moderator", "\"is_moderator\"")
                    .replace("is_owner", "\"is_owner\"")
//                        .replace("true", "\"true\"")
                    .replace("}{", "},{")
                    .replace("!", "")
                    .replace("\"\"", "\\\"")
                    .replace("=", "\\=")
                    .replace("&", "\\&");

            return users2;
        }

        @Override
        protected void onPostExecute(String s)
        {
            mUserParsed.onSuccess(s);
            super.onPostExecute(s);
        }
    }

    private interface UserParsed
    {
        void onSuccess(String usersJson);

        void onFail(String message);
    }

    /**
     * Add user to user {@link SlidingMenu}
     *
     * @param name     Name of user
     * @param imgUrl   URL for user's avatar
     * @param id       Chat ID of user
     * @param lastPost Last Post time in UNIX time
     * @param rep      User's total on-site reputation
     * @param isMod    Is user a mod?
     * @param isOwner  Is user a room owner? (Always true if isMod is true)
     * @param chatUrl  URL of current chat
     */

    private void addUser(final String name, final String imgUrl, final int id, final int lastPost, final int rep, final boolean isMod, final boolean isOwner, final String chatUrl)
    {
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
        mUserTiles.add(userTileFragment);
        mFragmentManager.beginTransaction().add(R.id.users_scroll_slide, userTileFragment, "user_" + id).commit();
    }

    /**
     * Instantiate and handle taps of chat buttons
     *
     * @param url Chat URL
     */
    private void addChatButtons(final String url)
    {

//        FloatingActionButton openInBrowser = view.findViewById(R.id.open_in_browser_fab);
        com.github.clans.fab.FloatingActionButton roomInfo = view.findViewById(R.id.room_info_fab);
        com.github.clans.fab.FloatingActionButton stars = view.findViewById(R.id.star_fab);
        com.github.clans.fab.FloatingActionButton users = view.findViewById(R.id.show_users_fab);
        FloatingActionButton browser = view.findViewById(R.id.open_in_browser_fab);
        final FloatingActionMenu fam = view.findViewById(R.id.chat_menu);

//        openInBrowser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);
//            }
//        });

        roomInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                View roomInfo = View.inflate(getActivity(), R.layout.room_desc, null);

                new MaterialDialog.Builder(getActivity())
                        .title("Info")
                        .customView(roomInfo, true)
                        .positiveText(R.string.generic_ok)
                        .show();

                TextView desc = roomInfo.findViewById(R.id.desc_text);
                assert desc != null;
                desc.setText(mChatDesc);
                desc.setMovementMethod(LinkMovementMethod.getInstance());

                TextView tag = roomInfo.findViewById(R.id.tag_text);
                assert tag != null;
                tag.setText(mChatTagsSpanned);
                tag.setMovementMethod(LinkMovementMethod.getInstance());

                TextView url = roomInfo.findViewById(R.id.url_text);
                assert url != null;
                url.setText(Html.fromHtml("<b>URL: </b><a href=\"".concat(mChatUrl).concat("\">").concat(mChatUrl).concat("</a>")));
                url.setMovementMethod(LinkMovementMethod.getInstance());

                TextView host = roomInfo.findViewById(R.id.domain_text);
                assert host != null;
                host.setText(Html.fromHtml("<b>Domain: </b><a href=\"".concat("https://").concat(mChatDomain).concat("\">").concat("https://").concat(mChatDomain).concat("</a>")));
                host.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        stars.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", mChatUrl.replace("rooms/", "rooms/info/").replace("#", "").concat("/?tab=stars"));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        browser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChatUrl));
                startActivity(intent);
            }
        });

        users.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSlidingMenu.toggle();
                fam.close(true);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (view.findViewById(R.id.chat_menu).getVisibility() != View.VISIBLE) showThatFam();

        System.out.println("Chat Frag OnResume");

        hueAllTheThings();

    }

    private void showThatFam() {
        FloatingActionMenu fam = view.findViewById(R.id.chat_menu);
        fam.hideMenuButton(false);
        fam.showMenuButton(true);
    }

    /*
     * Parse Description of chat
     */

    private static class GetDesc extends AsyncTask<String, Void, String>
    {
        private final DescGotten mDescGotten;

        static GetDesc newInstance(DescGotten descGotten)
        {
            return new GetDesc(descGotten);
        }

        GetDesc(DescGotten descGotten)
        {
            mDescGotten = descGotten;
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                Elements divs = Jsoup.parse(params[0]).select("div");

                for (Element e : divs)
                {
                    if (e.hasAttr("id") && e.attr("id").equals("roomdesc"))
                    {
                        return e.html();
                    }
                }

                mDescGotten.onFail("NULL");
                return null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            mDescGotten.onSuccess(s);
        }
    }

    private interface DescGotten
    {
        void onSuccess(String desc);

        void onFail(@SuppressWarnings("SameParameterValue") String message);
    }

    /*
     * Parse Tags of chat
     */

    private static class GetTags extends AsyncTask<String, Void, ArrayList<String>>
    {
        private final TagsGotten mTagsGotten;

        @NonNull
        static GetTags newInstance(TagsGotten tagsGotten)
        {
            return new GetTags(tagsGotten);
        }

        GetTags(TagsGotten tagsGotten)
        {
            mTagsGotten = tagsGotten;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            try
            {
                Elements divs = Jsoup.parse(params[0]).select("div").select("a");
                ArrayList<String> tagList = new ArrayList<>();

                for (Element e : divs)
                {
                    if (e.hasAttr("class") && e.attr("class").equals("tag"))
                    {
                        tagList.add(e.html());
                    }
                }

                return tagList;
            }
            catch (Exception e)
            {
                mTagsGotten.onFail("NULL");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings)
        {
            mTagsGotten.onSuccess(strings);
        }
    }

    private interface TagsGotten
    {
        void onSuccess(ArrayList<String> tabList);

        void onFail(@SuppressWarnings("SameParameterValue") String message);
    }

    /**
     * Access the fragment's {@link SlidingMenu} from elsewhere
     *
     * @return returns {@link ChatFragment#mSlidingMenu}
     */

    public SlidingMenu getmSlidingMenu()
    {
        return mSlidingMenu;
    }

    /**
     * Access all users' info from elsewhere
     *
     * @return returns the user info Bundles
     */

    public ArrayList<Bundle> getmUserInfo()
    {
        return mUserInfo;
    }

    /**
     * Get the current chat color from elsewhere
     *
     * @return returns the color int of the chat accent
     */

    public int getmAppBarColor()
    {
        return mAppBarColor;
    }

    /*
     * Parse the chat's host domain
     */

    private static class GetHostDomainFromHtml extends AsyncTask<String, Void, String>
    {
        final DomainFoundListener mDomainFoundListener;

        static GetHostDomainFromHtml newInstance(DomainFoundListener domainFoundListener)
        {
            return new GetHostDomainFromHtml(domainFoundListener);
        }

        GetHostDomainFromHtml(DomainFoundListener domainFoundListener)
        {
            mDomainFoundListener = domainFoundListener;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                Log.e("STARTED", "DOOOO");
                Document document = Jsoup.parse(strings[0]);
//                Log.e("DOC", document.html());
                Elements scripts = document.select("script");
                Log.e("S", scripts.html());

                Pattern p = Pattern.compile("host:(.*?),");
                Matcher m = p.matcher(scripts.html());

                while (!m.hitEnd())
                {
                    Log.e("INWHILE", "III");
                    if (m.find())
                    {
                        Log.e("HOST", m.group());
                        return m.group().replace(",", "").replace("host: ", "").replace("'", "");
                    }
                }
                throw new Exception("Host Domain Not Found");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("NOTFOUND", e.getMessage());
                mDomainFoundListener.onFail(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String domain)
        {
            mDomainFoundListener.onSuccess(domain);
        }
    }

    private interface DomainFoundListener
    {
        void onSuccess(String text);

        void onFail(String text);
    }

    /**
     * Access the current chat's ID from elsewhere
     * @return the chat ID as Integer
     */

    public Integer getChatId()
    {
        return mChatId;
    }

}

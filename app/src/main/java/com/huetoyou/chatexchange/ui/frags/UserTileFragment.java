package com.huetoyou.chatexchange.ui.frags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.text.Html;

/**
 * Created by Zacha on 5/31/2017.
 */

public class UserTileFragment extends Fragment implements Parcelable {
    private View mView;
    private SharedPreferences mSharedPreferences;
    private TextView mUserInfo;

    private final int NAME_INDEX = 0;
    private final int URL_INDEX = 1;
    private final int ID_INDEX = 2;
    private final int LP_INDEX = 3;
    private final int REP_INDEX = 4;
    private final int MOD_INDEX = 5;
    private final int OWN_INDEX = 6;
    private String mChatUrl;
    private Bundle mArgs;
    private ImageView user_image_info;
    private Bitmap mIconBitmap;

    private String CREATOR;
    private GetIcon mGetIcon;
    private GetIconForInfo mGetIconForInfo;

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_tile, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getResources().getText(R.string.app_name).toString(), Context.MODE_PRIVATE);

        mUserInfo = mView.findViewById(R.id.user_info_tile);

        mArgs = getArguments();

        final String name = mArgs.getString("userName", "Not Found!");
        final String url = mArgs.getString("userAvatarUrl", "");
        mChatUrl = mArgs.getString("chatUrl", "");

        final int id = mArgs.getInt("id", -1);
        final int lastPost = mArgs.getInt("lastPost", -1);
        final int rep = mArgs.getInt("rep", -1);

        final boolean isMod = mArgs.getBoolean("isMod", false);
        final boolean isOwner = mArgs.getBoolean("isOwner", false);

        setUserName(name);
        setAvatar(url);
        setIsModOwner(isMod, isOwner);

        displayInfoOnTap(id, lastPost, rep);

        return mView;
    }

    private void setUserName(String text) {
        mUserInfo.setText(text);
        mUserInfo.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void setAvatar(String url) {
        mGetIcon = new GetIcon();
        mGetIcon.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, "50");
    }

    private void setIsModOwner(boolean isMod, boolean isOwner) {
        if (isMod) mUserInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
        else if (isOwner) mUserInfo.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC | Typeface.BOLD);
    }

    public Bitmap getmIconBitmap() {
        return mIconBitmap;
    }

    private void displayInfoOnTap(final int id, final int lastPost, final int rep) {
        mUserInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long t = (long)lastPost * 1000;
                Date date = new Date(t);
                date.setTime(t);
                String d = SimpleDateFormat.getDateInstance().format(date);

                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                calendar.setTimeZone(TimeZone.getDefault());
                calendar.setTime(date);   // assigns calendar to given date
                int hr24 = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                int hr12 = calendar.get(Calendar.HOUR);        // gets hour in 12h format
                int min = calendar.get(Calendar.MINUTE);
                int sec = calendar.get(Calendar.SECOND);

                String time = String.format(Locale.US, "%02d:%02d:%02d", hr24, min, sec);

                View view = View.inflate(getActivity(), R.layout.user_info, null);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setTitle(getResources().getText(R.string.user_info) + " | " + mArgs.getString("userName", "Not Found!"))
                        .setView(view)
                        .setPositiveButton("OK", null)
                        .create();

                alertDialog.show();

                user_image_info = view.findViewById(R.id.user_image);
                TextView user_id = view.findViewById(R.id.user_id);
                TextView user_last_post = view.findViewById(R.id.user_last_post);
                TextView user_rep = view.findViewById(R.id.user_rep);

                try {
                    mGetIconForInfo = new GetIconForInfo();
                    mGetIconForInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mArgs.getString("userAvatarUrl", ""), "140");
                    user_id.setText(TextUtils.concat(Html.fromHtml("<b>" + getResources().getText(R.string.user_id) + " </b>"), String.valueOf(id)));
                    user_last_post.setText(TextUtils.concat(Html.fromHtml("<b>" + getResources().getText(R.string.user_last_talked) + " </b>"), d + " " + time));
                    user_rep.setText(TextUtils.concat(Html.fromHtml("<b>" + getResources().getText(R.string.user_rep) + " </b>"), String.valueOf(rep)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mUserInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String addr;
                if (mChatUrl.contains("stackexchange")) addr = "https://chat.stackexchange.com/users/";
                else addr = "https://chat.stackoverflow.com/users/";

                addr = addr.concat(String.valueOf(id));

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(addr));
                startActivity(browserIntent);
                return true;
            }
        });
    }

    private class GetIcon extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            try {
                String bmpKey = "AVATAR_" + params[0].replace("/", "");

                try {
                    FileInputStream fis = getActivity().openFileInput(bmpKey);
                    mIconBitmap = BitmapFactory.decodeStream(fis);
                } catch (Exception e) {
                    InputStream is = (InputStream) new URL(params[0]).getContent();
                    mIconBitmap = BitmapFactory.decodeStream(is);
                }

                int p = Integer.decode(params[1]);

                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, p, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(mIconBitmap, px, px, true));
            } catch (Exception e) {
                e.printStackTrace();
                return VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (Build.VERSION.SDK_INT >= 21) {
                mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            } else {
                //noinspection deprecation
                mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }
        }
    }

    private class GetIconForInfo extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            try {
                String bmpKey = "AVATAR_" + params[0].replace("/", "");

                try {
                    FileInputStream fis = getActivity().openFileInput(bmpKey);
                    mIconBitmap = BitmapFactory.decodeStream(fis);
                } catch (Exception e) {
                    InputStream is = (InputStream) new URL(params[0]).getContent();
                    mIconBitmap = BitmapFactory.decodeStream(is);
                }

                int p = Integer.decode(params[1]);

                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, p, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(mIconBitmap, px, px, true));
            } catch (Exception e) {
                e.printStackTrace();
                return VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            user_image_info.setImageDrawable(drawable);
        }
    }

    @Override
    public void onDestroy() {
        try {
            mGetIcon.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

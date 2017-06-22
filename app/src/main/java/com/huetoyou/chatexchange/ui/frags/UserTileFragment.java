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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class UserTileFragment extends Fragment {
    private View mView;
    private SharedPreferences mSharedPreferences;
    private TextView mUserInfo;

    private String mChatUrl;
    private Bundle mArgs;
    private ImageView user_image_info;
    private Bitmap mIconBitmap;

    private GetIcon mGetIcon;
    private ProgressBar mLoading;
    private View mUserInfoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_tile, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getResources().getText(R.string.app_name).toString(), Context.MODE_PRIVATE);

        mUserInfo = mView.findViewById(R.id.user_info_tile);
        mLoading = mView.findViewById(R.id.avatar_loading);

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
        mGetIcon = new GetIcon(url, 50, false);
        mGetIcon.start();
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

                mUserInfoView = View.inflate(getActivity(), R.layout.user_info, null);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setTitle(getResources().getText(R.string.user_info) + " | " + mArgs.getString("userName", "Not Found!"))
                        .setView(mUserInfoView)
                        .setPositiveButton("OK", null)
                        .create();

                alertDialog.show();

                user_image_info = mUserInfoView.findViewById(R.id.user_image);
                TextView user_id = mUserInfoView.findViewById(R.id.user_id);
                TextView user_last_post = mUserInfoView.findViewById(R.id.user_last_post);
                TextView user_rep = mUserInfoView.findViewById(R.id.user_rep);

                try {
                    mGetIcon = new GetIcon(mArgs.getString("userAvatarUrl", ""), 140, true);
                    mGetIcon.start();
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

    private class GetIcon extends Thread {
        private String mUrl;
        private int mSize;
        private boolean mIsForInfoDialog;

        GetIcon(String url, int size, boolean forInfo) {
            mUrl = url;
            mSize = size;
            mIsForInfoDialog = forInfo;
        }

        @Override
        public void run() {
            Drawable drawable;

            try {
                String bmpKey = "AVATAR_" + mUrl.replace("/", "");

                try {
                    FileInputStream fis = getActivity().openFileInput(bmpKey);
                    mIconBitmap = BitmapFactory.decodeStream(fis);
                } catch (Exception e) {
                    InputStream is = (InputStream) new URL(mUrl).getContent();
                    mIconBitmap = BitmapFactory.decodeStream(is);

                    try {
                        FileOutputStream fos = getActivity().openFileOutput(bmpKey, Context.MODE_PRIVATE);
                        mIconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                int p = mSize;

                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, p, r.getDisplayMetrics());

                drawable = new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(mIconBitmap, px, px, true));
            } catch (Exception e) {
                e.printStackTrace();
                drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_help_outline_black_24dp, null);
            }

            final Drawable drawable1 = drawable;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mIsForInfoDialog) {
                        user_image_info.setImageDrawable(drawable1);
                        mUserInfoView.findViewById(R.id.info_loading).setVisibility(View.GONE);
                    } else {
                        if (Build.VERSION.SDK_INT >= 21) {
                            mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable1, null, null);
                        } else {
                            //noinspection deprecation
                            mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable1, null, null);
                        }
                        mLoading.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        try {
            mGetIcon.interrupt();
//            mGetIconForInfo.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

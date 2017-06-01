package com.huetoyou.chatexchange.ui.frags;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zacha on 5/31/2017.
 */

public class UserTileFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_tile, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getResources().getText(R.string.app_name).toString(), Context.MODE_PRIVATE);

        mUserInfo = (TextView) mView.findViewById(R.id.user_info_tile);

        Bundle args = getArguments();

        final String name = args.getString("userName", "Not Found!");
        final String url = args.getString("userAvatarUrl", "");
        mChatUrl = args.getString("chatUrl", "");

        final int id = args.getInt("id", -1);
        final int lastPost = args.getInt("lastPost", -1);
        final int rep = args.getInt("rep", -1);

        final boolean isMod = args.getBoolean("isMod", false);
        final boolean isOwner = args.getBoolean("isOwner", false);

        setUserName(name);
        setAvatar(url);
        setIsMod(isMod);
        setIsOwner(isOwner);

        displayInfoOnTap(id, lastPost, rep);

        return mView;
    }

    private void setUserName(String text) {
        mUserInfo.setText(text);
        mUserInfo.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void setAvatar(String url) {
        new GetIcon().execute(url);
    }

    private void setIsMod(boolean isMod) {
        if (isMod) mUserInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setIsOwner(boolean isOwner) {
        if (isOwner) mUserInfo.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC | Typeface.BOLD);
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

                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setTitle(R.string.user_info)
                        .setMessage("ID : " + id + "\n" +
                                    "Last Post Time: " + d + " " + time + "\n" +
                                    "Rep: " + rep)
                        .setPositiveButton("OK", null)
                        .show();
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
                InputStream is = (InputStream) new URL(params[0]).getContent();
                Bitmap b = BitmapFactory.decodeStream(is);

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(b, 64, 64, true));
            } catch (Exception e) {
                e.printStackTrace();
                return getResources().getDrawable(R.drawable.ic_help_outline_black_24dp);
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
}

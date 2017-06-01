package com.huetoyou.chatexchange;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Zacha on 5/31/2017.
 */

public class UserTileFragment extends Fragment {
    private View mView;
    private SharedPreferences mSharedPreferences;
    private TextView mUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_tile, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(getResources().getText(R.string.app_name).toString(), Context.MODE_PRIVATE);

        mUserInfo = (TextView) mView.findViewById(R.id.user_info_tile);

        Bundle args = getArguments();

        setUserName(args.getString("userName", "Not Found???"));
        setAvatar(args.getString("userAvatarUrl", ""));

        return mView;
    }

    private void setUserName(String text) {
        mUserInfo.setText(text);
        mUserInfo.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void setAvatar(String url) {
        Drawable drawable = null;

        try {
            drawable = new GetIcon().execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            //noinspection deprecation
            mUserInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    private class GetIcon extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            try {
                InputStream is = (InputStream) new URL(params[0]).getContent();
                Bitmap b = BitmapFactory.decodeStream(is);

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(b, 144, 144, true));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

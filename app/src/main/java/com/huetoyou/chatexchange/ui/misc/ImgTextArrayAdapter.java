package com.huetoyou.chatexchange.ui.misc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ImgTextArrayAdapter extends ArrayAdapter<String>
{
    private final Activity context;
//    private String[] chatroomNames;
//    private String[] chatroomUrls;
//    private Integer[] chatroomColors;
//    private Drawable[] icons;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Integer> mColors = new ArrayList<>();
    private ArrayList<Drawable> mIcons = new ArrayList<>();

    public ImgTextArrayAdapter(Activity context)
    {
        super(context, R.layout.chatroom_list_item, new ArrayList<String>());
        // TODO Auto-generated constructor stub

        this.context = context;
//        this.chatroomNames = chatroomNames;
//        this.chatroomUrls = chatroomUrls;
//        this.icons = icons;
//        this.chatroomColors = chatroomColors;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
        {
            view = inflater.inflate(R.layout.chatroom_list_item, null, false);
        }

        TextView chatroomNameTextView = view.findViewById(R.id.chatroomName);
        ImageView chatroomIconImgView = view.findViewById(R.id.chatroomImg);

//        chatroomNameTextView.setText(chatroomNames[position]);
//        chatroomIconImgView.setImageDrawable(icons[position]);

        chatroomNameTextView.setText(mNames.get(position));
        chatroomIconImgView.setImageDrawable(mIcons.get(position));

        return view;
    }

    public void addChat(String name, String url, Drawable icon, Integer color) {
        if (!mNames.contains(name))
        {
            mNames.add(name);
            mUrls.add(url);
            mIcons.add(icon);
            mColors.add(color);

            addAll(name);
        }
    }

    public ArrayList<String> getNames()
    {
        return mNames;
    }

    public ArrayList<String> getUrls()
    {
        return mUrls;
    }

    public ArrayList<Integer> getColors()
    {
        return mColors;
    }
}
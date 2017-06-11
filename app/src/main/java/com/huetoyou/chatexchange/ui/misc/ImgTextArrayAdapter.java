package com.huetoyou.chatexchange.ui.misc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.huetoyou.chatexchange.R;

public class ImgTextArrayAdapter extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] chatroomNames;
    private final String[] chatroomUrls;
    private final Integer[] chatroomColors;
    private final Drawable[] icons;

    public ImgTextArrayAdapter(Activity context, String[] chatroomNames, String[] chatroomUrls, Drawable[] icons, Integer[] chatroomColors)
    {
        super(context, R.layout.chatroom_list_item,  chatroomNames);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.chatroomNames = chatroomNames;
        this.chatroomUrls = chatroomUrls;
        this.icons = icons;
        this.chatroomColors = chatroomColors;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) view = inflater.inflate(R.layout.chatroom_list_item, null, false);

        TextView chatroomNameTextView = view.findViewById(R.id.chatroomName);
        ImageView chatroomIconImgView = view.findViewById(R.id.chatroomImg);

        chatroomNameTextView.setText(chatroomNames[position]);
        chatroomIconImgView.setImageDrawable(icons[position]);

        return view;
    }

    public String[] getNames() {
        return chatroomNames;
    }

    public String[] getUrls() {
        return chatroomUrls;
    }

    public Integer[] getColors() {
        return chatroomColors;
    }
}
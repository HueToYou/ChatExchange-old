package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.huetoyou.chatexchange.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImgTextArrayAdapter extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] chatroomNames;
    private final String[] chatroomDescs;
    private final Drawable[] icons;

    public ImgTextArrayAdapter(Activity context, String[] chatroomNames, String[] chatroomDescs, Drawable[] icons)
    {
        super(context, R.layout.chatroom_list_item,  chatroomNames);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.chatroomNames = chatroomNames;
        this.chatroomDescs = chatroomDescs;
        this.icons = icons;
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.chatroom_list_item, null,true);

        TextView chatroomNameTextView = (TextView) row.findViewById(R.id.chatroomName);
        ImageView chatroomIconImgView = (ImageView) row.findViewById(R.id.chatroomImg);
        TextView chatroomDescTextView = (TextView) row.findViewById(R.id.chatroomDesc);

        chatroomNameTextView.setText(chatroomNames[position]);
        chatroomIconImgView.setImageDrawable(icons[position]);
        chatroomDescTextView.setText(chatroomDescs[position]);
        return row;
    };
}
package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;

import java.util.ArrayList;

/**
 * Created by Zacha on 7/3/2017.
 */

public class RecyclerAdapter
        extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Activity mContext;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Integer> mColors = new ArrayList<>();
    private ArrayList<Drawable> mIcons = new ArrayList<>();

    private OnItemClicked onItemClicked;

    @Override
    public int getItemCount()
    {
        return mNames.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_list_item, parent, false);
        return new MyViewHolder(mView);
    }

    public String getNameAt(int position) {
        return mNames.get(position);
    }

    public String getUrlAt(int position) {
        return mUrls.get(position);
    }

    public Integer getColorAt(int position) {
        return mColors.get(position);
    }

    public Drawable getIconAt(int position) {
        return mIcons.get(position);
    }

//    public RecyclerAdapter(@NonNull ArrayList<String> mNames) {
//        this.mNames = mNames;
//    }

    public RecyclerAdapter(Activity activity, OnItemClicked onItemClicked) {
        this.mContext = activity;
        this.onItemClicked = onItemClicked;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
//        mViewHolder = holder;
        Log.e("POS", position + "");
        holder.setClickListener(position);
        holder.setOnLongClickListener(position);
        holder.setCloseClickListener(position);
        holder.setText(position);
        holder.setImage(position);
    }

    //Remove an item at position and notify changes.
    public String removeItem(int position) {
        if (mNames.get(position) != null)
        {
            final String model = mNames.remove(position);
            mUrls.remove(position);
            mIcons.remove(position);
            mColors.remove(position);
            notifyItemRemoved(position);
            return model;
        }
        return null;
    }

    //Add an item at position and notify changes.
    public void addItem(int position, String name, String url, Drawable icon, Integer color) {
        if (!mNames.contains(name))
        {
            mNames.add(position, name);
            mUrls.add(position, url);
            mIcons.add(position, icon);
            mColors.add(position, color);
            notifyItemInserted(position);
        }
    }

    //Move an item at fromPosition to toPosition and notify changes.
    public void moveItem(int fromPosition, int toPosition) {
        final String model = mNames.remove(fromPosition);
        mNames.add(toPosition, model);

        final String url = mUrls.remove(fromPosition);
        mUrls.add(toPosition, url);

        final Drawable icon = mIcons.remove(fromPosition);
        mIcons.add(toPosition, icon);

        final Integer color = mColors.remove(fromPosition);
        mColors.add(toPosition, color);

        notifyItemMoved(fromPosition, toPosition);
    }

    //Remove items that no longer exist in the new mNames.
    public void applyAndAnimateRemovals(@NonNull final ArrayList<String> urls) {
        for (int i = mUrls.size() - 1; i >= 0; i--) {
            final String model = mUrls.get(i);
            if (urls.contains(model)) {
                removeItem(i);
            }
        }
    }

    //Add items that do not exist in the old mNames.
    public void applyAndAnimateAdditions(@NonNull final ArrayList<String> newNames,
                                          @NonNull final ArrayList<String> newUrls,
                                          @NonNull final ArrayList<Drawable> newIcons,
                                          @NonNull final ArrayList<Integer> newColors) {
        for (int i = 0, count = newNames.size(); i < count; i++) {
            final String name = newNames.get(i);
            final String url = newUrls.get(i);
            final Drawable icon = newIcons.get(i);
            final Integer color = newColors.get(i);

            if (!mNames.contains(name)) {
                if (newNames.size() < 2) addItem(i + mNames.size(), name, url, icon, color);
                else addItem(i, name, url, icon, color);
            }

        }
    }

    //Move items that have changed their position.
    public void applyAndAnimateMovedItems(@NonNull final ArrayList<String> urls) {
        for (int toPosition = urls.size() - 1; toPosition >= 0; toPosition--) {
            final String url = urls.get(toPosition);
            final int fromPosition = mUrls.indexOf(url);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // TODO: whatever views you need to bind
        TextView mTextView;
        ImageView mImageView;
        ImageButton mCloseChat;
        View mItem;

        MyViewHolder(View v) {
            super(v); // done this way instead of view tagging
            mItem = v;
            mTextView = v.findViewById(R.id.chatroomName);
            mImageView =  v.findViewById(R.id.chatroomImg);
            mCloseChat = v.findViewById(R.id.close_chat_button);
        }

        public void setText(int position) {
            if (mNames.size() > 0) mTextView.setText(mNames.get(position));
        }

        public void setImage(int position) {
            if (mIcons.size() > 0) mImageView.setImageDrawable(mIcons.get(position));
        }

        public void setClickListener(final int position) {
            mItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mCloseChat.getVisibility() == View.VISIBLE) mCloseChat.setVisibility(View.INVISIBLE);
                    else if (onItemClicked != null) onItemClicked.onClick(view, position);
                }
            });
        }

        public void setOnLongClickListener(final int position) {
            mItem.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    if (mCloseChat.getVisibility() == View.INVISIBLE) mCloseChat.setVisibility(View.VISIBLE);
                    else mCloseChat.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
        }

        public void setCloseClickListener(final int position) {
            mCloseChat.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (onItemClicked != null) onItemClicked.onCloseClick(mCloseChat, position);
                }
            });
        }
    }

    public interface OnItemClicked {
        void onClick(View view, int position);
        void onCloseClick(View view, int position);
    }
}

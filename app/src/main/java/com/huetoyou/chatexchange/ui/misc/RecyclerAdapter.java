package com.huetoyou.chatexchange.ui.misc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToOrigin;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.backend.database.HueDatabase;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements SwipeableItemAdapter<RecyclerAdapter.MyViewHolder>
{
    private Activity mContext;
    private OnItemClicked onItemClicked;

    private ArrayList<MyViewHolder> mVHs = new ArrayList<>();
    //private ArrayList<Chatroom> mChatroomObjects = new ArrayList<>();

    private RecyclerViewSwipeManager mSwipeManager;

    public RecyclerAdapter(Activity activity, HueDatabase hueDatabase, OnItemClicked onItemClicked, RecyclerViewSwipeManager swipeManager)
    {
        this.mContext = activity;
        this.hueDatabase = hueDatabase;
        this.onItemClicked = onItemClicked;
        this.mSwipeManager = swipeManager;

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getItemCount()
    {
        return hueDatabase.getAllChatrooms().size();
    }

    @Override
    public long getItemId(int position)
    {
        return sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position).getId();
    }



    @Override
    public int getItemViewType(int position)
    {
        return sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.setClickListener();
        holder.setCloseClickListener();
        holder.setText();
        holder.setImage();
        mVHs.add(position, holder);
        holder.setMaxLeftSwipeAmount(0f);
        holder.setMaxRightSwipeAmount(1.0f);
        holder.setSwipeItemHorizontalSlideAmount(sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position).isPinned() ? 0.25f : 0);
        holder.setProportionalSwipeAmountModeEnabled(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_list_item, parent, false);

        return new MyViewHolder(mView);
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y)
    {
        return Swipeable.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type)
    {
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result)
    {
        Log.d("SWIPED", "onSwipeItem(position = " + position + ", result = " + result + ")");

        Chatroom item;

        try
        {
            item = sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            item = null;
        }

        switch (result)
        {
            // swipe left --- pin
            case Swipeable.RESULT_SWIPED_RIGHT:
                if (!holder.isCloseButtonRevealed())
                {
                    holder.revealCloseButton();
                }

                if (item != null && !item.isPinned())
                {
                    item.setPinned(true);
                    notifyItemChanged(position);
                }
                return null;
            // other --- do nothing
            case Swipeable.RESULT_SWIPED_LEFT:
            case Swipeable.RESULT_CANCELED:
            default:
                if (item != null && item.isPinned())
                {
                    item.setPinned(false);
                    notifyItemChanged(position);
                }
                holder.hideCloseButton();
//                if (position != RecyclerView.NO_POSITION) {
//                    return new UnpinResultAction(this, position);
//                } else {
//                    return null;
//                }
                return null;
        }
    }

    public void update()
    {
        ArrayList<Chatroom> mChatrooms = hueDatabase.getAllChatrooms();

        for (int i = 0; i < mChatrooms.size(); i++)
        {

        }
    }

    public void addItem(Chatroom hueObject)
    {
        if (!chatroomObjectsContainsID(hueObject.getId()))
        {
            hueDatabase.addChatroom(hueObject);

            notifyDataSetChanged();
        }
    }

    public Chatroom getItemAt(int position)
    {
        return sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position);
    }

    RecyclerViewSwipeManager getSwipeManager()
    {
        return mSwipeManager;
    }

    MyViewHolder getViewHolderAt(int position)
    {
        return mVHs.get(position);
    }

    /*//Move an item at fromPosition to toPosition and notify changes.
    public void moveItem(int fromPosition, int toPosition)
    {
        final Chatroom object = mChatroomObjects.remove(fromPosition);
        mChatroomObjects.add(toPosition, object);

        notifyItemMoved(fromPosition, toPosition);
    }*/

    //Remove an item at position and notify changes.
    public Chatroom removeItem(int position)
    {
        if (hueDatabase.getAllChatrooms().size() > position && sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position) != null)
        {
            final Chatroom item = sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position);
            if (mVHs.size() > position && mVHs.get(position) != null)
            {
                mVHs.remove(position);
            }
            notifyDataSetChanged();
            return item;
        }

        return null;
    }

    //Remove an item at position and notify changes.
    public void removeItemWithSnackbar(Activity activity, final int position, final SnackbarListener listener)
    {
        getSwipeManager().performFakeSwipe(mVHs.get(position), SwipeableItemConstants.RESULT_SWIPED_LEFT);

        if (sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(position) != null)
        {
            final Chatroom huehuehue = removeItem(position);

            if (huehuehue != null)
            {
                final SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

                huehuehue.setPinned(false);
                String chatroomName = huehuehue.getName();
                String snackTextDel = "Deleted " + chatroomName;
                final String snackTextRestore = "Chatroom restored!";

                final SpannableStringBuilder snackTextDelSSB = new SpannableStringBuilder().append(snackTextDel);
                final SpannableStringBuilder snackTextRestoreSSB = new SpannableStringBuilder().append(snackTextRestore);


                if (mSharedPrefs.getBoolean("darkTheme", false))
                {
                    snackTextDelSSB.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorDark)), 0, snackTextDel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    snackTextRestoreSSB.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorDark)), 0, snackTextRestore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                else
                {
                    snackTextDelSSB.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.white)), 0, snackTextDel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    snackTextRestoreSSB.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.white)), 0, snackTextRestore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                final View parentLayout = activity.findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, snackTextDelSSB, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Snackbar hue = Snackbar.make(parentLayout, snackTextRestoreSSB, Snackbar.LENGTH_SHORT);

                                if (mSharedPrefs.getBoolean("darkTheme", false))
                                {
                                    hue.getView().setBackgroundColor(Color.WHITE);
                                }

                                hue.show();
                                addItem(huehuehue);
                                listener.onUndo();
                            }
                        });

                if (mSharedPrefs.getBoolean("darkTheme", false))
                {
                    snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.white));
                }

                snackbar.show();
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>()
                {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event)
                    {
                        switch (event)
                        {
                            case DISMISS_EVENT_TIMEOUT:
                                listener.onUndoExpire(huehuehue.getUrl());
                                break;
                        }
                    }
                });
            }
        }
    }

    /*private void resetPositions()
    {
        for (int i = 0; i < mChatroomObjects.size(); i++)
        {
            mChatroomObjects.get(i).setPosition(i);
        }
    }*/

    public interface OnItemClicked
    {
        void onClick(View view, int position);

        void onCloseClick(View view, int position);
    }

    public interface SnackbarListener
    {
        void onUndo();

        void onUndoExpire(String url);
    }

    private interface Swipeable extends SwipeableItemConstants
    {
    }

    public class MyViewHolder extends AbstractSwipeableItemViewHolder implements SwipeableItemViewHolder
    {
        // TODO: whatever views you need to bind
        TextView mTextView;
        ImageView mImageView;
        ImageView mCloseChat;
        View mItem;

        FrameLayout mContainer;
        RelativeLayout mBehind;

        boolean closeButtonRevealed = false;

        private final AnimatorSet mCloseButtonRevealSet = new AnimatorSet();
        private final AnimatorSet mCloseButtonHideSet = new AnimatorSet();
        private final AnimatorListenerAdapter mRevealListener = new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                mCloseChat.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        };
        private final AnimatorListenerAdapter mHideListener = new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mCloseChat.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                super.onAnimationStart(animation);
            }
        };

        MyViewHolder(View v)
        {
            super(v); // done this way instead of view tagging
            mItem = v;
            mTextView = v.findViewById(R.id.chatroomName);
            mImageView = v.findViewById(R.id.chatroomImg);
            mCloseChat = v.findViewById(R.id.close_chat_img);

            mBehind = v.findViewById(R.id.behind_views);
            mContainer = v.findViewById(R.id.chat_item_container);

            mCloseChat.setScaleX(0f);
            mCloseChat.setScaleY(0f);

            ObjectAnimator revealAnimatorX = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleX",
                    0f,
                    1.0f
            );

            ObjectAnimator revealAnimatorY = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleY",
                    0f,
                    1.0f
            );

            mCloseButtonRevealSet.play(revealAnimatorX);
            mCloseButtonRevealSet.play(revealAnimatorY);
            mCloseButtonRevealSet.setInterpolator(new OvershootInterpolator());
            mCloseButtonRevealSet.setDuration((long) Utils.getAnimDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200, mContext));
            mCloseButtonRevealSet.addListener(mRevealListener);

            ObjectAnimator hideAnimatorX = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleX",
                    1.0f,
                    0f
            );

            ObjectAnimator hideAnimatorY = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleY",
                    1.0f,
                    0f
            );

            mCloseButtonHideSet.play(hideAnimatorX);
            mCloseButtonHideSet.play(hideAnimatorY);
            mCloseButtonHideSet.setInterpolator(new AnticipateInterpolator());
            mCloseButtonHideSet.setDuration((long) Utils.getAnimDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200, mContext));
            mCloseButtonHideSet.addListener(mHideListener);
        }

        @Override
        public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping)
        {
            if (horizontalAmount >= 1.0f && isSwiping)
            {
                clickClose();
            }
            else if (horizontalAmount >= 0.0f && isSwiping)
            {
                sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(getLayoutPosition()).setPinned(true);
                if (!isCloseButtonRevealed())
                {
                    revealCloseButton();
                }
            }

            super.onSlideAmountUpdated(horizontalAmount, verticalAmount, isSwiping);
        }

        @Override
        public View getSwipeableContainerView()
        {
            return mContainer;
        }

        public View getItem()
        {
            return mItem;
        }

        ImageView getCloseChatButton()
        {
            return mCloseChat;
        }

        void setText()
        {
            if (hueDatabase.getAllChatrooms().size() > getLayoutPosition())
            {
                mTextView.setText(sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(getLayoutPosition()).getName());
            }
        }

        void setImage()
        {
            if (hueDatabase.getAllChatrooms().size() > getLayoutPosition())
            {
                mImageView.setImageDrawable(sortChatroomsArrayList(hueDatabase.getAllChatrooms()).get(getLayoutPosition()).getIcon());
            }
        }

        void setClickListener()
        {
            mItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.e("CLICKED", getLayoutPosition() + "");

                    if (mCloseChat.getScaleX() == 1.0f)
                    {
                        mCloseButtonRevealSet.cancel();
                        mCloseButtonHideSet.start();
                        getSwipeManager().performFakeSwipe(mVHs.get(getLayoutPosition()), SwipeableItemConstants.RESULT_SWIPED_LEFT);
                        //mCloseChat.setVisibility(View.INVISIBLE);
                        Log.e("CLOSE", "HIDING");
                    }
                    else if (onItemClicked != null)
                    {
                        Log.e("SENDING", "CLICKTERFACE");
                        onItemClicked.onClick(view, getLayoutPosition());
                    }
                }
            });
        }

        void revealCloseButton()
        {
            mCloseButtonHideSet.cancel();
            mCloseButtonRevealSet.start();
            setCloseButtonRevealed(true);
        }

        void hideCloseButton()
        {
            mCloseButtonRevealSet.cancel();
            mCloseButtonHideSet.start();
            setCloseButtonRevealed(false);
        }

        void setCloseClickListener()
        {
            mCloseChat.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    clickClose();
                }
            });
        }

        void clickClose()
        {
            if (onItemClicked != null)
            {
                onItemClicked.onCloseClick(mCloseChat, getLayoutPosition());
            }
        }

        boolean isCloseButtonRevealed()
        {
            return closeButtonRevealed;
        }

        void setCloseButtonRevealed(boolean set)
        {
            closeButtonRevealed = set;
        }
    }
}

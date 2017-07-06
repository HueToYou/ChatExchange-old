package com.huetoyou.chatexchange.ui.misc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.huetoyou.chatexchange.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
        implements SwipeableItemAdapter<RecyclerAdapter.MyViewHolder>
{

    private Activity mContext;

    private View.OnClickListener mSwipeableViewContainerOnClickListener;
    private View.OnClickListener mUnderSwipeableViewButtonOnClickListener;

    private EventListener mEventListener;

    private OnItemClicked onItemClicked;

    private ArrayList<MyViewHolder> mVHs = new ArrayList<>();
    private ArrayList<ChatroomRecyclerObject> mChatroomObjects = new ArrayList<>();

    private RecyclerViewSwipeManager mSwipeManager;

    public interface EventListener {
        void onItemPinned(int position);

        void onItemViewClicked(View v);

        void onUnderSwipeableViewButtonClicked(View v);
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result)
    {
        Log.d("SWIPED", "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            // swipe left --- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                holder.revealCloseButton();
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_SWIPED_RIGHT:
            case Swipeable.RESULT_CANCELED:
            default:
                holder.hideCloseButton();
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type)
    {
//        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
//            holder.itemView.setBackgroundColor(Color.YELLOW);
//        } else {
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        }
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y)
    {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public int getItemCount()
    {
        return mChatroomObjects.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_list_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(mView);
        return myViewHolder;
    }

    public String getNameAt(int position)
    {
        return mChatroomObjects.get(position).getName();
    }

    public String getUrlAt(int position)
    {
        return mChatroomObjects.get(position).getUrl();
    }

    public Integer getColorAt(int position)
    {
        return mChatroomObjects.get(position).getColor();
    }

    public Drawable getIconAt(int position)
    {
        return mChatroomObjects.get(position).getIcon();
    }

    public MyViewHolder getViewHolderAt(int position)
    {
        return mVHs.get(position);
    }

    public RecyclerAdapter(Activity activity, OnItemClicked onItemClicked, RecyclerViewSwipeManager swipeManager)
    {
        this.mContext = activity;
        this.onItemClicked = onItemClicked;
        this.mSwipeManager = swipeManager;

        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };
        mUnderSwipeableViewButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnderSwipeableViewButtonClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);

//        RecyclerViewSwipeManager manager = new RecyclerViewSwipeManager();
//        manager.createWrappedAdapter(this);
    }

    public RecyclerViewSwipeManager getSwipeManager() {
        return mSwipeManager;
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(
                    RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    private void onUnderSwipeableViewButtonClick(View v) {
        if (mEventListener != null) {
            mEventListener.onUnderSwipeableViewButtonClicked(
                    RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position)
    {
        return mChatroomObjects.get(position).getId();
    }

    @Override
    public int getItemViewType(int position)
    {
        return mChatroomObjects.get(position).getViewType();
    }

    private interface Swipeable extends SwipeableItemConstants
    {
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        ChatroomRecyclerObject item = mChatroomObjects.get(position);

        //mViewHolder = holder;
        holder.setClickListener(position);
//        holder.setOnLongClickListener(position);
        holder.setCloseClickListener(position);
        holder.setText(position);
        holder.setImage(position);
        mVHs.add(position, holder);

//        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);
//        holder.mCloseChat.setOnClickListener(mUnderSwipeableViewButtonOnClickListener);

        holder.setMaxLeftSwipeAmount(-0.25f);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(item.isPinned() ? -0.25f : 0);
    }

    //Remove an item at position and notify changes.
    public void removeItem(int position)
    {
        if (mChatroomObjects.get(position) != null)
        {
            mChatroomObjects.remove(position);
            notifyItemRemoved(position);
        }
    }

    //Add an item at position and notify changes.
//    public void addItem(int position, String name, String url, Drawable icon, Integer color)
//    {
//        if (!mNames.contains(name))
//        {
//            mNames.add(position, name);
//            mUrls.add(position, url);
//            mIcons.add(position, icon);
//            mColors.add(position, color);
//            notifyItemInserted(position);
//        }
//    }

    public void addItem(ChatroomRecyclerObject hueObject)
    {
        if (!mChatroomObjects.contains(hueObject))
        {
            int position = hueObject.getPosition();
            mChatroomObjects.add(position, hueObject);
            notifyItemInserted(position);
        }
    }

    public ChatroomRecyclerObject getItem(int position)
    {
        return mChatroomObjects.get(position);
    }

    //Move an item at fromPosition to toPosition and notify changes.
    public void moveItem(int fromPosition, int toPosition)
    {
        final ChatroomRecyclerObject object = mChatroomObjects.remove(fromPosition);
        mChatroomObjects.add(toPosition, object);

        notifyItemMoved(fromPosition, toPosition);
    }

    public class MyViewHolder extends AbstractSwipeableItemViewHolder
        implements SwipeableItemViewHolder
    {
        // TODO: whatever views you need to bind
        TextView mTextView;
        ImageView mImageView;
        ImageButton mCloseChat;
        View mItem;

        FrameLayout mContainer;
        RelativeLayout mBehind;

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
            mCloseChat = v.findViewById(R.id.close_chat_button);

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
            mCloseButtonRevealSet.setDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200);
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
            mCloseButtonHideSet.setDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200);
            mCloseButtonHideSet.addListener(mHideListener);
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

        public ImageButton getCloseChatButton()
        {
            return mCloseChat;
        }

        public void setText(int position)
        {
            if (mChatroomObjects.size() > position)
            {
                mTextView.setText(mChatroomObjects.get(position).getName());
            }
        }

        public void setImage(int position)
        {
            if (mChatroomObjects.size() > position)
            {
                mImageView.setImageDrawable(mChatroomObjects.get(position).getIcon());
            }
        }

        public void setClickListener(final int position)
        {
            mItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.e("CLICKED", position + "");

                    if (mCloseChat.getScaleX() == 1.0f)
                    {
                        mCloseButtonRevealSet.cancel();
                        mCloseButtonHideSet.start();
                        //mCloseChat.setVisibility(View.INVISIBLE);
                        Log.e("CLOSE", "HIDING");
                    }
                    else if (onItemClicked != null)
                    {
                        Log.e("SENDING", "CLICKTERFACE");
                        onItemClicked.onClick(view, position);
                    }
                }
            });
        }

        public void performLongClick()
        {
            if (mCloseChat.getScaleX() == 0f)
            {
                revealCloseButton();
                //mCloseChat.setVisibility(View.VISIBLE);
            }
            else
            {
                hideCloseButton();
                //mCloseChat.setVisibility(View.INVISIBLE);
            }
        }

        public void revealCloseButton() {
            mCloseButtonHideSet.cancel();
            mCloseButtonRevealSet.start();
        }

        public void hideCloseButton() {
            mCloseButtonRevealSet.cancel();
            mCloseButtonHideSet.start();
        }

        public void setOnLongClickListener(final int position)
        {
            mItem.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    performLongClick();
                    return true;
                }
            });
        }

        public void setCloseClickListener(final int position)
        {
            mCloseChat.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (onItemClicked != null)
                    {
                        onItemClicked.onCloseClick(mCloseChat, position);
                    }
                }
            });
        }
    }

    public interface OnItemClicked
    {
        void onClick(View view, int position);

        void onCloseClick(View view, int position);
    }

    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection
    {
        private RecyclerAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(RecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            ChatroomRecyclerObject item = mAdapter.mChatroomObjects.get(mPosition);

            if (!item.isPinned()) {
                item.setIsPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault
    {
        private RecyclerAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(RecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            ChatroomRecyclerObject item = mAdapter.mChatroomObjects.get(mPosition);
            if (item.isPinned()) {
                item.setIsPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    /*//Remove items that no longer exist in the new mNames.
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
    }*/
}

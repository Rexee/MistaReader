package com.mistareader.ui.topics;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.model.Topic;
import com.mistareader.util.views.Recycler.RecyclerAdapter;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;
import com.mistareader.util.ThemesManager;

import butterknife.BindView;


public class TopicsAdapter extends RecyclerAdapter<Topic, TopicsAdapter.ViewHolder> {
    private static final int TYPE_USER  = 1;
    private static final int TYPE_USER0 = 2;

    public interface TopicClicks {
        void onTopicClick(Topic topic);

        void onUserClick(String username);

        void onTopicLongClick(View v, Topic topic);
    }

    private boolean mShowSections;
    private static int mDefaultColor = -1;
    private static int mAuthorColor  = -1;
    private static int mAccountColor = -1;
    private String  mAccount;
    private boolean mModeSubscription;

    public boolean reachedMaxTopics;

    private TopicClicks mCallback;

    public TopicsAdapter(Context context, TopicClicks callback, String accountName, String selectedSection, boolean modeSubscription) {
        mCallback = callback;
        mAccount = accountName;

        mModeSubscription = modeSubscription;

        mShowSections = selectedSection.isEmpty();

        TypedValue typedValue = new TypedValue();
        Theme theme = context.getTheme();
        mDefaultColor = ThemesManager.getColorByAttr(theme, typedValue, android.R.attr.textColorPrimary);
        mAuthorColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_author);
        mAccountColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_account);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position, Topic item) {
        holder.replies.setText(String.valueOf(item.answ));
        holder.text.setText(item.text);
        holder.time.setText(item.time_text);
        holder.user0.setText(item.user0);
        holder.user.setText(item.user);

        if (mModeSubscription) {
            if (item.newAnsw == 0) {
                holder.newReplies.setVisibility(View.INVISIBLE);
            } else {
                holder.newReplies.setVisibility(View.VISIBLE);
                holder.newReplies.setText("+" + item.newAnsw);
            }
        }

        if (mShowSections)
            holder.section.setText(item.sect1);
        else
            holder.section.setText("");

        if (item.user0.equals(mAccount))
            holder.user0.setTextColor(mAccountColor);
        else
            holder.user0.setTextColor(mAuthorColor);

        if (item.user.equals(mAccount))
            holder.user.setTextColor(mAccountColor);
        else
            holder.user.setTextColor(mDefaultColor);

        if (item.answ >= 100)
            holder.text.setTypeface(null, Typeface.BOLD);
        else
            holder.text.setTypeface(null, Typeface.NORMAL);

        if (item.closed == 1) {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block, 0, 0, 0);
        } else if (item.is_voting == 1) {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vote, 0, 0, 0);
        } else {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).id;
    }

    public long getTopicTime(int pos) {
        if (mItems == null || mItems.isEmpty()) {
            return 0;
        }

        if (mItems.size() > pos) {
            return mItems.get(mItems.size() - 1).utime;
        } else {
            return mItems.get(pos - 1).utime;
        }
    }

    public class ViewHolder extends RecyclerViewHolder {
        @BindView(R.id.topic_text)        TextView text;
        @BindView(R.id.topic_time)        TextView time;
        @BindView(R.id.topic_user0)       TextView user0;
        @BindView(R.id.topic_user)        TextView user;
        @BindView(R.id.topic_replies)     TextView replies;
        @BindView(R.id.topic_section)     TextView section;
        @BindView(R.id.topic_replies_new) TextView newReplies;

        public ViewHolder(View v) {
            super(v);
            if (mModeSubscription) {
                newReplies.setVisibility(View.VISIBLE);
            }
            v.setOnClickListener(v1 -> {
                int pos = getCheckedAdapterPosition();
                if (getCheckedAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCallback.onTopicClick(mItems.get(pos));
                }
            });
            v.setOnLongClickListener(v12 -> {
                int pos = getCheckedAdapterPosition();
                if (getCheckedAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCallback.onTopicLongClick(v12, mItems.get(pos));
                    return true;
                }
                return false;
            });
            user.setOnClickListener(view -> onUserClick(getCheckedAdapterPosition(), TYPE_USER));
            user0.setOnClickListener(view -> onUserClick(getCheckedAdapterPosition(), TYPE_USER0));
        }

        private int getCheckedAdapterPosition() {
            if (mCallback == null || mItems == null || mItems.isEmpty()) {
                return RecyclerView.NO_POSITION;
            }
            return getAdapterPosition();
        }
    }

    private void onUserClick(int adapterPosition, int type) {
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        if (type == TYPE_USER) {
            mCallback.onUserClick(mItems.get(adapterPosition).user);
        } else {
            mCallback.onUserClick(mItems.get(adapterPosition).user0);
        }
    }
}

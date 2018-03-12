package com.mistareader.ui.messages;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.model.Message;
import com.mistareader.model.Topic;
import com.mistareader.util.views.Recycler.RecyclerAdapter;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;
import com.mistareader.util.S;
import com.mistareader.util.ThemesManager;

import butterknife.BindView;


public class MessagesAdapter extends RecyclerAdapter<Message, RecyclerViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM   = 2;

    public interface MessageClicks {
        void onUserClick(Message message);

        void onMessageLongClick(View v, Message message);
    }

    private static int mDefaultColor = -1;
    private static int mAuthorColor  = -1;
    private static int mAccountColor = -1;

    private String         mAuthor;
    private String         mAccount;
    private MessageClicks  mCallback;
    private LayoutInflater mInflater;
    private boolean        mHasVotes;
    private Topic          mCurrentTopic;
    private String[]       mVotes;

    public MessagesAdapter(Context context, MessageClicks callback, Topic currentTopic, String account) {
        mCallback = callback;
        mAuthor = currentTopic.user0;
        mItems = currentTopic.messages;
        mCurrentTopic = currentTopic;
        mAccount = account;
        mInflater = LayoutInflater.from(context);

        if (currentTopic.is_voting == 1) {
            mHasVotes = true;
            updateVotes();
        } else {
            mHasVotes = false;
        }

        TypedValue typedValue = new TypedValue();
        Theme theme = context.getTheme();
        mDefaultColor = ThemesManager.getColorByAttr(theme, typedValue, android.R.attr.textColorPrimary);
        mAuthorColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_author);
        mAccountColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_account);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolderHeader(mInflater.inflate(R.layout.message_header, viewGroup, false));
        } else {
            return new ViewHolderItem(mInflater.inflate(mLayoutItemResId, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder h, int position, Message currentMessage) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {
            ViewHolderHeader holder = (ViewHolderHeader) h;

            if (mHasVotes) {
                holder.votes.setVisibility(View.VISIBLE);

                tryShowVote(1, holder.vote1);
                tryShowVote(2, holder.vote2);
                tryShowVote(3, holder.vote3);
                tryShowVote(4, holder.vote4);
                tryShowVote(5, holder.vote5);
            }

        }
        ViewHolderItem holder = (ViewHolderItem) h;

        if (currentMessage.isDeleted || !currentMessage.isLoaded) {
            holder.root.setVisibility(View.GONE);
            holder.itemView.setTag(false);
            return;
        } else {
            holder.root.setVisibility(View.VISIBLE);
            holder.itemView.setTag(true);
        }

        if (currentMessage.vote > 0) {
            if (mVotes != null) {
                holder.vote.setText(currentMessage.vote + ". " + mVotes[currentMessage.vote - 1]);
            } else {
                holder.vote.setText(currentMessage.vote + ". ");
            }
            holder.vote.setVisibility(View.VISIBLE);
        } else {
            holder.vote.setVisibility(View.GONE);
        }

        holder.text.setText(currentMessage.text);
        if (currentMessage.quoteRepresentation.isEmpty())
            holder.replies.setVisibility(View.GONE);
        else
            holder.replies.setVisibility(View.VISIBLE);

        holder.user.setText(currentMessage.user);
        holder.replies.setText(currentMessage.quoteRepresentation);
        holder.time.setText(currentMessage.timeText);
        holder.n.setText("" + currentMessage.n);

        String messUser = currentMessage.user;
        if (messUser.equals(mAuthor)) {
            holder.user.setTextColor(mAuthorColor);
        } else if (messUser.equals(mAccount)) {
            holder.user.setTextColor(mAccountColor);
        } else {
            holder.user.setTextColor(mDefaultColor);
        }
    }

    public void updateVotes() {
        if (!S.isEmpty(mCurrentTopic.votes)) {
            //TODO: refresh items with votes in case them was displayed before topic data is loaded
            mVotes = new String[5];
            for (int i = 0; i < mCurrentTopic.votes.size(); i++) {
                mVotes[i] = mCurrentTopic.votes.get(i).voteName;
            }
        } else {
            mVotes = null;
        }
    }

    private void tryShowVote(int index, TextView textView) {
        Topic.Votes vote = mCurrentTopic.votes.get(index - 1);
        if (!vote.voteName.isEmpty()) {
            textView.setText("" + index + ". " + vote.voteName + ": " + vote.voteCount);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return -1;
        }
        return mItems.get(position).id;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void updateMessagesList() {
        if (mItems == null) {
            mItems = mCurrentTopic.messages;
        }

        notifyDataSetChanged();
    }

    public class ViewHolderItem extends RecyclerViewHolder {
        @BindView(R.id.mess_n)           TextView     n;
        @BindView(R.id.mess_time)        TextView     time;
        @BindView(R.id.mess_user)        TextView     user;
        @BindView(R.id.mess_text)        TextView     text;
        @BindView(R.id.mess_replies)     TextView     replies;
        @BindView(R.id.mess_vote)        TextView     vote;
        @BindView(R.id.messageMainFrame) LinearLayout root;

        public ViewHolderItem(View v) {
            super(v);

            user.setOnClickListener(view -> onUserClick(getAdapterPosition()));
        }
    }

    public class ViewHolderHeader extends ViewHolderItem {
        @BindView(R.id.mess_headerVote1) TextView     vote1;
        @BindView(R.id.mess_headerVote2) TextView     vote2;
        @BindView(R.id.mess_headerVote3) TextView     vote3;
        @BindView(R.id.mess_headerVote4) TextView     vote4;
        @BindView(R.id.mess_headerVote5) TextView     vote5;
        @BindView(R.id.votes)            LinearLayout votes;
        @BindView(R.id.mess_n)           TextView     n;
        @BindView(R.id.mess_time)        TextView     time;
        @BindView(R.id.mess_user)        TextView     user;
        @BindView(R.id.mess_text)        TextView     text;
        @BindView(R.id.mess_replies)     TextView     replies;
        @BindView(R.id.mess_vote)        TextView     vote;
        @BindView(R.id.messageMainFrame) LinearLayout root;

        public ViewHolderHeader(View v) {
            super(v);
            user.setOnClickListener(view -> onUserClick(getAdapterPosition()));
        }
    }

    private void onUserClick(int adapterPosition) {
        if (mCallback == null || S.isEmpty(mItems) || adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        mCallback.onUserClick(mItems.get(adapterPosition));
    }
}

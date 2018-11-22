package com.mistareader.ui.messages;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.model.Message;
import com.mistareader.model.Topic;
import com.mistareader.model.Votes;
import com.mistareader.util.Empty;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.views.Recycler.RecyclerAdapter;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;

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
    private Topic          mCurrentTopic;

    public MessagesAdapter(Context context, MessageClicks callback, Topic currentTopic, String account) {
        super(R.layout.message_row);
        mCallback = callback;
        mAuthor = currentTopic.user0;
        mItems = currentTopic.getMessages();
        mCurrentTopic = currentTopic;
        mAccount = account;
        mInflater = LayoutInflater.from(context);

        TypedValue typedValue = new TypedValue();
        Theme theme = context.getTheme();
        mDefaultColor = ThemesManager.getColorByAttr(theme, typedValue, android.R.attr.textColorPrimary);
        mAuthorColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_author);
        mAccountColor = ThemesManager.getColorByAttr(theme, typedValue, R.attr.text_account);
    }

    @NonNull @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolderHeader(mInflater.inflate(R.layout.message_header, viewGroup, false));
        } else {
            return new ViewHolderItem(mInflater.inflate(mLayoutItemResId, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder h, int position, Message currentMessage) {
        addVotes(h, position);

        ViewHolderItem holder = (ViewHolderItem) h;

        if (currentMessage.isDeleted() || !currentMessage.isLoaded()) {
            holder.root.setVisibility(View.GONE);
            holder.itemView.setTag(false);
            return;
        } else {
            holder.root.setVisibility(View.VISIBLE);
            holder.itemView.setTag(true);
        }

        if (currentMessage.vote > 0) {
            if (!Empty.is(mCurrentTopic.votes) && mCurrentTopic.votes.size() >= currentMessage.vote) {
                holder.vote.setText(currentMessage.vote + ". " + mCurrentTopic.votes.get(currentMessage.vote - 1).voteName);
            } else {
                holder.vote.setText(currentMessage.vote + ". ");
            }
            holder.vote.setVisibility(View.VISIBLE);
        } else {
            holder.vote.setVisibility(View.GONE);
        }

        holder.text.setText(currentMessage.getMessage());
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());
        if (currentMessage.getQuoteRepresentation().isEmpty())
            holder.replies.setVisibility(View.GONE);
        else
            holder.replies.setVisibility(View.VISIBLE);

        holder.user.setText(currentMessage.user);
        holder.replies.setText(currentMessage.getQuoteRepresentation());
        holder.time.setText(currentMessage.getTimeText());
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

    private void addVotes(RecyclerViewHolder h, int position) {
        if (mCurrentTopic.is_voting != 1) {
            return;
        }

        int viewType = getItemViewType(position);
        if (viewType != TYPE_HEADER) {
            return;
        }

        ViewHolderHeader holder = (ViewHolderHeader) h;
        if (Empty.is(mCurrentTopic.votes)) {
            return;
        }

        holder.votes.removeAllViews();
        holder.votes.setVisibility(View.VISIBLE);
        holder.votes_divider.setVisibility(View.VISIBLE);
        holder.votes_header.setVisibility(View.VISIBLE);

        TextView textView;
        int index = 1;
        Context context = h.itemView.getContext();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (Votes vote : mCurrentTopic.votes) {
            if (Empty.is(vote.voteName)) {
                continue;
            }

            textView = new TextView(context);
            textView.setTextAppearance(context, R.style.TextDefault_Vote);
            textView.setText("" + index + ". " + vote.voteName + ": " + vote.voteCount);

            holder.votes.addView(textView, lp);
            index++;
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
            mItems = mCurrentTopic.getMessages();
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
        @BindView(R.id.votes)                 LinearLayout votes;
        @BindView(R.id.votes_divider)         View         votes_divider;
        @BindView(R.id.mess_headerVoteHeader) TextView     votes_header;

        public ViewHolderHeader(View v) {
            super(v);
            user.setOnClickListener(view -> onUserClick(getAdapterPosition()));
        }
    }

    private void onUserClick(int adapterPosition) {
        if (mCallback == null || Empty.is(mItems) || adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        mCallback.onUserClick(mItems.get(adapterPosition));
    }
}

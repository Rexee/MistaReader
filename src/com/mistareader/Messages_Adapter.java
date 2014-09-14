package com.mistareader;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Messages_Adapter extends BaseAdapter {

    private ArrayList<Message> mMessages;
    private int                mResource;
    private LayoutInflater     mInflater;

    private static int         mUserTextColor = -1;
    private static int         mAuthorColor   = -1;
    private static int         mAccountColor  = -1;
    
    private String             mAuthor;
    private String             mAccount;
    
    private String[]    mVotes;

    public Messages_Adapter(Context context, Topic currentTopic, String account, int resource) {
        
        mMessages = currentTopic.messages;
        mAuthor = currentTopic.user0;
        
        if (currentTopic.is_voting == 1) {
            mVotes = new String[5];
            for (int i = 0; i < currentTopic.votes.size(); i++) {
                mVotes[i] = currentTopic.votes.get(i).voteName;
            }
        }
        
        mAccount = account;

        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Resources locRes = context.getResources();

        mAuthorColor = locRes.getColor(R.color.lightCyan);
        mAccountColor = locRes.getColor(R.color.lvLightGreenCol);

        TypedValue typedValue = new TypedValue();
        Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.text_user, typedValue, true);
        mUserTextColor = typedValue.data;

        theme.resolveAttribute(R.attr.text_author, typedValue, true);
        mAuthorColor = typedValue.data;

        theme.resolveAttribute(R.attr.text_account, typedValue, true);
        mAccountColor = typedValue.data;

    }

    static class ViewHolder {
        public TextView mess_N;
        public TextView mess_Time;
        public TextView mess_User;
        public TextView mess_Text;
        public TextView mess_Replies;
        public TextView mess_Vote;
        public RelativeLayout mess_filled;
    }

    @Override
    public int getCount() {
        return mMessages.size(); 
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        View v = convertView;

        Message currentMessage = mMessages.get(position);
        
        if (v == null) {
            v = mInflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.mess_filled = (RelativeLayout) v.findViewById(R.id.messageMainFrame);
            holder.mess_N = (TextView) v.findViewById(R.id.mess_n);
            holder.mess_Time = (TextView) v.findViewById(R.id.mess_time);
            holder.mess_User = (TextView) v.findViewById(R.id.mess_user);
            holder.mess_Text = (TextView) v.findViewById(R.id.mess_text);
            holder.mess_Replies = (TextView) v.findViewById(R.id.mess_replies);
            holder.mess_Vote = (TextView) v.findViewById(R.id.mess_vote);

            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) v.getTag();}
        
        if (currentMessage.isDeleted == true || !currentMessage.isLoaded)
        {
            holder.mess_filled.setVisibility(View.GONE);
            return v;
        }
            else
                holder.mess_filled.setVisibility(View.VISIBLE);

        if (currentMessage.vote > 0) {
            holder.mess_Vote.setText(currentMessage.vote+". "+mVotes[currentMessage.vote-1]);
            holder.mess_Vote.setVisibility(View.VISIBLE);
        }
        else
            holder.mess_Vote.setVisibility(View.GONE);
        
        holder.mess_Text.setText(Html.fromHtml(currentMessage.text));
        if (currentMessage.quoteRepresentation.isEmpty())
            holder.mess_Replies.setVisibility(View.GONE);
        else
            holder.mess_Replies.setVisibility(View.VISIBLE);

        holder.mess_User.setText(currentMessage.user);
        holder.mess_Replies.setText(currentMessage.quoteRepresentation);
        holder.mess_Time.setText(currentMessage.timeText);
        holder.mess_N.setText("" + currentMessage.n);

        String messUser = currentMessage.user;

        if (messUser.equals(mAuthor)) {
            holder.mess_User.setTextColor(mAuthorColor);
        }
        else if (messUser.equals(mAccount))
            holder.mess_User.setTextColor(mAccountColor);
        else
            holder.mess_User.setTextColor(mUserTextColor);

        return v;

    }

}
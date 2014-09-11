package com.mistareader;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Topics_Adapter extends BaseAdapter {

    private ArrayList<Topic> mTopics;
    private int              mResource;
    private LayoutInflater   mInflater;
    private boolean          mShowSections;

    private static int       mUserTextColor = -1;
    private static int       mAuthorColor   = -1;
    private static int       mAccountColor  = -1;

    private String           mAccount;

    public Topics_Adapter(Context context, Forum forum, String selectedSection, int resource) {
        mTopics = forum.topics;
        mAccount = forum.accountName;
        mResource = resource;

        mShowSections = selectedSection.isEmpty();

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
        public TextView topic_Time;
        public TextView topic_User0;
        public TextView topic_User;
        public TextView topic_Text;
        public TextView topic_Replies;
        public TextView topic_Section;
    }

    @Override
    public int getCount() {
        return mTopics.size();
    }

    @Override
    public Topic getItem(int position) {
        return mTopics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View v;

        if (convertView == null) {
            v = mInflater.inflate(mResource, parent, false);

            holder = new ViewHolder();

            holder.topic_Time = (TextView) v.findViewById(R.id.topic_time);
            holder.topic_User0 = (TextView) v.findViewById(R.id.topic_user0);
            holder.topic_User = (TextView) v.findViewById(R.id.topic_user);
            holder.topic_Text = (TextView) v.findViewById(R.id.topic_text);
            holder.topic_Replies = (TextView) v.findViewById(R.id.topic_replies);
            holder.topic_Section = (TextView) v.findViewById(R.id.topic_section);

            v.setTag(holder);

        }
        else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        Topic currentTopic = mTopics.get(position);

        holder.topic_Replies.setText("" + currentTopic.answ);
        holder.topic_Text.setText(Html.fromHtml(currentTopic.text));
        holder.topic_Time.setText(currentTopic.time_text);
        holder.topic_User0.setText(currentTopic.user0);
        holder.topic_User.setText(currentTopic.user);

        if (mShowSections)
            holder.topic_Section.setText(currentTopic.sect1);
        else
            holder.topic_Section.setText("");

        if (currentTopic.user0.equals(mAccount))
            holder.topic_User0.setTextColor(mAccountColor);
        else
            holder.topic_User0.setTextColor(mAuthorColor);

        if (currentTopic.user.equals(mAccount))
            holder.topic_User.setTextColor(mAccountColor);
        else
            holder.topic_User.setTextColor(mUserTextColor);

        if (currentTopic.answ >= 100)
            holder.topic_Text.setTypeface(null, Typeface.BOLD);
        else
            holder.topic_Text.setTypeface(null, Typeface.NORMAL);

        return v;

    }

}
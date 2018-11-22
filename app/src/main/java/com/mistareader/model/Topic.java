package com.mistareader.model;

import android.text.Spanned;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.mistareader.util.DateUtils;
import com.mistareader.util.MessagesUtils;
import com.mistareader.util.S;

import java.util.ArrayList;
import java.util.List;


@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Topic {
    //    public String v8;
    //    public Integer created;
    public String  error;
    public long    id;
    public String  forum;
    public String  sect1;
    public String  sect2;
    public String  text;
    public int     closed;
    public int     down;
    public String  user0;
    public String  user;
    public long    utime;
    public int     answ;
    public Integer answers_count;
    public int     is_voting;
    public int     deleted;

    @JsonField(name = "voting")
    public ArrayList<Votes> votes;

    private Spanned forumTitle;
    private String  timeText;
    private int newAnsw;
    private ArrayList<Message> messages;

    public Topic() {}

    @OnJsonParseComplete
    void onParseComplete() {
        if (answers_count != null) {
            answ = answers_count;
        }
        if (text != null) {
            forumTitle = S.fromHtml(text);
        }
        timeText = DateUtils.SDF_D_M_H_M.format(utime * 1000);
    }

    public void addNewMessages(List<Message> newMessages, int messagesFrom, int messagesTo) {
        if (messages == null) {
            allocateMessages(answ);
        }

        int len = messages.size();
        for (Message newMessage : newMessages) {
            MessagesUtils.setQuotesInMessages(newMessage, messages);

            if (newMessage.n >= len) {
                messages.add(newMessage);
            } else {
                //TODO: check if need to update reply links
                messages.set(newMessage.n, newMessage);
            }
        }

        //mark deleted posts as loaded too
        messagesTo = Math.min(answ, messagesTo);
        for (int i = messagesFrom; i < messagesTo; i++) {
            messages.get(i).setLoaded(true);
        }
    }

    //preallocate list with messages amount
    public void updateAnsw(int newAnsw) {
        if (answ != newAnsw) {
            if (newAnsw > answ) {
                allocateMessages(newAnsw - answ);
            }
            answ = newAnsw;
        }
    }

    private void allocateMessages(int count) {
        if (messages == null) {
            messages = new ArrayList<>(count);
        }

        for (int i = 0; i <= count; i++) {
            messages.add(new Message());
        }
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    public int getNewAnsw() {
        return newAnsw;
    }

    public void setNewAnsw(int newAnsw) {
        this.newAnsw = newAnsw;
    }

    public ArrayList<Votes> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Votes> votes) {
        this.votes = votes;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public Spanned getForumTitle() {
        return forumTitle;
    }

    public void setForumTitle(Spanned forumTitle) {
        this.forumTitle = forumTitle;
    }
}

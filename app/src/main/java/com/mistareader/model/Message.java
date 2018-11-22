package com.mistareader.model;

import android.text.Spanned;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.mistareader.util.DateUtils;
import com.mistareader.util.MessagesUtils;
import com.mistareader.util.S;

import java.util.ArrayList;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Message {
    public long   id;
    public int    n;
    public String text;
    public String user;
    public long   utime;
    public int    vote;

    private Spanned            message;
    private ArrayList<Integer> repliedTo;
    private ArrayList<Reply>   quote;
    private String             quoteRepresentation;
    private String             isUserStarter;
    private String             timeText;
    private boolean            isLoaded;
    private boolean            isDeleted;

    public Message() {
        this.quoteRepresentation = "";
        this.isLoaded = false;
        this.isDeleted = true;
    }

    @OnJsonParseComplete
    void onParseComplete() {
        isLoaded = true;
        isDeleted = false;
        if (text != null) {
            message = S.linkifyHTML(text);
            repliedTo = MessagesUtils.extractReplies(text);
        }
        timeText = DateUtils.SDF_D_M_H_M.format(utime * 1000);
    }

    public ArrayList<Integer> getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(ArrayList<Integer> repliedTo) {
        this.repliedTo = repliedTo;
    }

    public ArrayList<Reply> getQuote() {
        return quote;
    }

    public void setQuote(ArrayList<Reply> quote) {
        this.quote = quote;
    }

    public String getQuoteRepresentation() {
        return quoteRepresentation;
    }

    public void setQuoteRepresentation(String quoteRepresentation) {
        this.quoteRepresentation = quoteRepresentation;
    }

    public String getIsUserStarter() {
        return isUserStarter;
    }

    public void setIsUserStarter(String isUserStarter) {
        this.isUserStarter = isUserStarter;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Spanned getMessage() {
        return message;
    }

    public void setMessage(Spanned message) {
        this.message = message;
    }
}
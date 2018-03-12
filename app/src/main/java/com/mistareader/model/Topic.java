package com.mistareader.model;

import android.text.Spanned;

import java.util.ArrayList;

public class Topic {

    public Topic() {}

    public long    id;
    public String  forum;
    public String  sect1;
    public String  sect2;
    public Spanned text;
    public int     closed;
    public int     down;
    public String  user0;
    public String  user;
    public long    utime;
    public int     answ;
    public int     is_voting;
    public String  time_text;
    public int     deleted;
    public int     newAnsw;

    public ArrayList<Votes>   votes;
    public ArrayList<Message> messages;

    public class Votes {
        public String voteName;
        public int    voteCount;
    }

    public void deleteMessages() {
        messages.clear();
    }

    public int getLastMessageNumber() {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        return messages.get(messages.size() - 1).n;
    }

    public void addNewMessages(ArrayList<Message> newMessages, int messagesFrom, int messagesTo) {
        if (messages == null) {
            allocateMessages(answ);
        }

        int len = messages.size();
        for (Message newMessage : newMessages) {
            if (newMessage.n > len) {
                messages.add(newMessage);
            } else {
                //TODO: check if need to update reply links
                messages.set(newMessage.n, newMessage);
            }
        }

        //mark deleted posts as loaded too
        messagesTo = Math.min(answ, messagesTo);
        for (int i = messagesFrom; i < messagesTo; i++) {
            messages.get(i).isLoaded = true;
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
}

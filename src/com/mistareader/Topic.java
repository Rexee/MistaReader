package com.mistareader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.mistareader.TextProcessors.JSONProcessor;

public class Topic {

    public Topic() {}

    public long               id;
    public String             forum;
    public String             sect1;
    public String             sect2;
    public String             text;
    public int                closed;
    public int                down;
    public String             user0;
    public String             user;
    public long               utime;
    public int                answ;
    public int                is_voting;
    public String             time_text;
    public int                deleted;

    public ArrayList<Votes>   votes;

    public ArrayList<Message> messages;

    final static String       MESSAGE_ATTRIBUTE_id     = "id";
    final static String       MESSAGE_ATTRIBUTE_n      = "n";
    final static String       MESSAGE_ATTRIBUTE_text   = "text";
    final static String       MESSAGE_ATTRIBUTE_utime  = "utime";
    final static String       MESSAGE_ATTRIBUTE_user   = "user";
    final static String       MESSAGE_ATTRIBUTE_quotes = "quotes";

    public class Votes {
        public String voteName;
        public int    voteCount;

    }

    public static String[] MessageGetFrom() {
        String[] from = { MESSAGE_ATTRIBUTE_n, MESSAGE_ATTRIBUTE_text, MESSAGE_ATTRIBUTE_utime, MESSAGE_ATTRIBUTE_user, MESSAGE_ATTRIBUTE_quotes };

        return from;
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

    public void addNewMessages(String JSONresult, int messages_from, int messages_to) throws UnsupportedEncodingException {

        if (messages == null) {
            messages = new ArrayList<Message>(answ);
            for (int i = 0; i <= answ; i++) {
                Message newMessage = new Message();
                messages.add(newMessage);
            }
        }

//        long d1 = System.nanoTime();
//        JSONProcessor2.ParseMessages(JSONresult, messages, answ, messages_from, messages_to);
//        long d2 = System.nanoTime();
//        JSONProcessor.ParseMessages(JSONresult, messages, answ, messages_from, messages_to);
//        long d3 = System.nanoTime();
//        JSONProcessor2.ParseMessages(JSONresult, messages, answ, messages_from, messages_to);
//        long d4 = System.nanoTime();
//        JSONProcessor.ParseMessages(JSONresult, messages, answ, messages_from, messages_to);
//        long d5 = System.nanoTime();
//        
//        S.L("" + (d2-d1)+" == "+(d3-d2)+" == "+(d4-d3)+" == "+(d5-d4));

        JSONProcessor.ParseMessages(JSONresult, messages, answ, messages_from, messages_to);

    }

}

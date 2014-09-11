package com.mistareader.TextProcessors;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

import com.mistareader.API;
import com.mistareader.Message;
import com.mistareader.Message.Reply;
import com.mistareader.Section;
import com.mistareader.Topic;
import com.mistareader.TextProcessors.S.ResultContainer;

@SuppressLint("SimpleDateFormat")
public class JSONProcessor {

    public static ArrayList<Topic> ParseTopics(String inputString) throws UnsupportedEncodingException {

        ArrayList<Topic> locTopics = new ArrayList<Topic>();

        try {

            if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {
                return locTopics;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:mm");

            JSONArray jArray = new JSONArray(inputString);

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject mainObj = (JSONObject) jArray.get(i);

                Topic newTopic = new Topic();

                newTopic.id = mainObj.getLong("id");
                newTopic.forum = mainObj.getString("forum");
                newTopic.sect1 = mainObj.getString("sect1");
                newTopic.sect2 = mainObj.getString("sect2");
                newTopic.text = mainObj.getString("text");
                newTopic.closed = mainObj.getInt("closed");
                newTopic.down = mainObj.getInt("down");
                newTopic.user0 = mainObj.getString("user0");
                newTopic.user = mainObj.getString("user");
                newTopic.utime = mainObj.getLong("utime");

                Date date = new Date(newTopic.utime * 1000L);
                newTopic.time_text = sdf.format(date).toString();

                newTopic.answ = mainObj.getInt("answ");
                newTopic.is_voting = mainObj.getInt("is_voting");

                locTopics.add(newTopic);

            }

        }
        catch (Exception e) {

            S.L("Forum.ParseTopics: " + Log.getStackTraceString(e));
        }

        return locTopics;
    }

    public static void ParseMessages(String inputString, ArrayList<Message> messages, int answ, int messages_from, int messages_to)
            throws UnsupportedEncodingException {

        try {

            if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:mm");
            Message newMessage;
            
            JSONArray jArray = new JSONArray(inputString);

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject mainObj = (JSONObject) jArray.get(i);

                int n = mainObj.getInt("n");

                if (n > answ)
                    newMessage = new Message();
                else
                    newMessage = messages.get(n);

                newMessage.id = mainObj.getLong("id");
                newMessage.n = n;
                newMessage.text = mainObj.getString("text");
                newMessage.user = mainObj.getString("user");
                newMessage.vote =  mainObj.getInt("vote");
                newMessage.utime = mainObj.getLong("utime");
                newMessage.isLoaded = true;
                newMessage.isDeleted = false;

                Date date = new Date(newMessage.utime * 1000L);
                newMessage.timeText = sdf.format(date).toString();

                newMessage.repliedTo = extractReplies(newMessage.text);
                setQuotesInMessages(newMessage, messages);

            }

            for (int i = messages_from; i <= messages_to; i++) {
                messages.get(i).isLoaded = true;
            }

        }
        catch (Exception e) {

            S.L("Forum.ParseMessages: " + Log.getStackTraceString(e));
        }

        return;
    }

    public static ResultContainer parseLogin(String inputString) {

        // {"error":"","userid":"11350","username":"vhl","hashkey":"00000000000000000000"}
        // {"error":"Ошибка авторизации ","userid":0,"username":""}

        final ResultContainer err = new ResultContainer();

        if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {

            err.result = false;
            err.errorString = "Empty result string...";
            return err;
        }

        try {
            
            JSONObject mainObj = new JSONObject(inputString);

            String errorText = mainObj.getString(API.LOGIN_RESULT_ERROR);

            int userId = mainObj.getInt(API.LOGIN_RESULT_USERID);
            if (userId == 0) {
                err.result = false;
                err.errorString = errorText;
                return err;
            }

            String cookieID = mainObj.getString(API.LOGIN_RESULT_SESSION_ID);
            if (cookieID.isEmpty()) {
                err.result = false;
                err.errorString = errorText;
                return err;
            }

            err.result = true;
            err.userID = Integer.toString(userId);
            err.resultSessionID = cookieID;

        }
        catch (Exception e) {

            String res = Log.getStackTraceString(e);
            err.result = false;
            err.errorString = res;

            S.L("Forum.parseLogin: " + res);
        }

        return err;

    }

    public static Topic ParseTopicInfo(String inputString) {

        Topic newTopic = new Topic();

        try {

            if (inputString.equals("{}") || inputString.isEmpty()) {
                return newTopic;
            }

            JSONObject mainObj = new JSONObject(inputString);

            newTopic.id = mainObj.getLong("id");
            newTopic.text = mainObj.getString("text");
            newTopic.closed = mainObj.getInt("closed");
            newTopic.down = mainObj.getInt("down");
            newTopic.deleted = mainObj.getInt("deleted");
            newTopic.answ = mainObj.getInt("answers_count");
            newTopic.is_voting = mainObj.getInt("is_voting");

            if (newTopic.is_voting == 1) {
                newTopic.votes = new ArrayList<Topic.Votes>(5);

                JSONArray jArray = mainObj.getJSONArray("voting");
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject mainObj2 = (JSONObject) jArray.get(i);

                    Topic.Votes newVote = newTopic.new Votes();
                    newVote.voteName = mainObj2.getString("select");
                    newVote.voteCount = mainObj2.getInt("result");

                    newTopic.votes.add(newVote);
                }
            }

        }
        catch (Exception e) {

            S.L("Forum.ParseMessages: " + Log.getStackTraceString(e));
        }

        return newTopic;
    }

    public static ArrayList<Section> parseSectionsList(String inputString) {

        ArrayList<Section> locSections = new ArrayList<Section>();
        String str = "";

        if (inputString == null || inputString.isEmpty() || inputString.equals("{}")) {
            String errorString = "Empty result string...";
            S.L(errorString);
        }

        try {
            JSONArray jArray = new JSONArray(inputString);
            for (int k = 0; k <= jArray.length() - 1; k++) {

                JSONObject mainObj = (JSONObject) jArray.get(k);

                Section newSection = new Section();

                str = mainObj.getString("shortn");
                newSection.sectionId = Section.getSectionID(str);
                newSection.sectionShortName = str;

                // newSection.sectionId = getString("id");
                // newSection.sectionId = getString("shortn");
                newSection.sectionFullName = mainObj.getString("fulln");
                newSection.forumName = mainObj.getString("forum");

                locSections.add(newSection);

            }
        }
        catch (Exception e) {
            String res = Log.getStackTraceString(e);
            S.L("Forum.parseSectionsList: " + res);
        }

        return locSections;

    }

    private static Message getMessageByN(int n, ArrayList<Message> messages) {

        for (Message locMessage : messages) {
            if (locMessage.n == n) {
                return locMessage;
            }
        }
        return null;
    }

    private static boolean isReplyInArray(int n, ArrayList<Reply> replies) {

        for (Reply loceReply : replies) {
            if (loceReply.n == n) {
                return true;
            }
        }
        return false;
    }

    public static void setQuotesInMessages(Message newMessage, ArrayList<Message> messages) {

        Message locMessage;

        for (int i = 0; i < newMessage.repliedTo.size(); i++) {

            locMessage = getMessageByN(newMessage.repliedTo.get(i), messages);
            if (locMessage != null) {
                if (locMessage.quote == null)
                    locMessage.quote = new ArrayList<Reply>(1);

                if (!isReplyInArray(newMessage.n, locMessage.quote)) {
                    locMessage.quote.add(new Reply(newMessage.id, newMessage.n));
                    locMessage.quoteRepresentation = locMessage.quoteRepresentation + " (" + newMessage.n + ")";
                }
            }

        }

    }

    static ArrayList<Integer> extractReplies(String s) {

        ArrayList<Integer> replies = new ArrayList<Integer>();
        int startPos, endPos, n;

        startPos = s.indexOf("(");

        while (startPos != -1) {
            endPos = s.indexOf(")", startPos + 1);
            if (endPos == -1)
                break;

            String messNum = s.substring(startPos + 1, endPos);
            if (isNumeric(messNum) && messNum.length() <= 4) {

                n = Integer.parseInt(messNum);

                replies.add(n);
            }

            startPos = s.indexOf("(", endPos + 1);
        }

        return replies;

    }

    public static boolean isNumeric(String s) {
        if (s == null)
            return false;

        if (s.length() == 0)
            return false;

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

    public static String arrayToString(ArrayList<Section> list) {

        String jsonText = "";

        try {

            JSONArray jArray = new JSONArray();
            
            for (int i = 0; i < list.size(); i++) {
                Section sec = list.get(i);

                JSONObject obj = new JSONObject();
                obj.put("id", sec.sectionId);
                obj.put("fulln", sec.sectionFullName);
                obj.put("shortn", sec.sectionShortName);
                obj.put("forum", sec.forumName);

                jArray.put(i, obj);
            }

            jsonText = jArray.toString();
        }
        catch (Exception e) {
            String res = Log.getStackTraceString(e);

            S.L("arrayToString: " + res);
        }

        return jsonText;

    }

    public static ArrayList<Section> stringToArray(String inputString) {

        ArrayList<Section> result = new ArrayList<>();

        if (inputString.isEmpty()) {
            return result;
        }

        try {
            JSONArray jArray = new JSONArray(inputString);
            for (int k = 0; k < jArray.length(); k++) {

                JSONObject mainObj = (JSONObject) jArray.get(k);

                Section newSection = new Section();

                newSection.sectionId = mainObj.getString("id");
                newSection.sectionShortName = mainObj.getString("shortn");
                newSection.sectionFullName = mainObj.getString("fulln");
                newSection.forumName = mainObj.getString("forum");

                result.add(newSection);

            }
        }
        catch (Exception e) {
            String res = Log.getStackTraceString(e);

            S.L("stringToArray: " + res);
        }

        return result;

    }

}

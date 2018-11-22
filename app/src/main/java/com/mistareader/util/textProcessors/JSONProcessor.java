package com.mistareader.util.textProcessors;

import android.annotation.SuppressLint;
import android.text.Html;
import android.util.Log;

import com.mistareader.api.API;
import com.mistareader.model.Message;
import com.mistareader.model.Section;
import com.mistareader.model.Topic;
import com.mistareader.model.User;
import com.mistareader.util.DateUtils;
import com.mistareader.util.Empty;
import com.mistareader.util.MessagesUtils;
import com.mistareader.util.S;
import com.mistareader.util.S.ResultContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class JSONProcessor {

    public static ArrayList<Topic> parseTopics(String inputString) {

        ArrayList<Topic> locTopics = new ArrayList<>();

        try {

            if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {
                return locTopics;
            }

            JSONArray jArray = new JSONArray(inputString);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject mainObj = (JSONObject) jArray.get(i);

                Topic newTopic = new Topic();

                //                newTopic.id = mainObj.getLong("id");
                //                newTopic.forum = mainObj.getString("forum");
                //                newTopic.sect1 = mainObj.getString("sect1");
                //                newTopic.sect2 = mainObj.getString("sect2");
                //                newTopic.text = Html.fromHtml(mainObj.getString("text"));
                //                newTopic.closed = mainObj.getInt("closed");
                //                newTopic.down = mainObj.getInt("down");
                //                newTopic.user0 = mainObj.getString("user0");
                //                newTopic.user = mainObj.getString("user");
                //                newTopic.utime = mainObj.getLong("utime");
                //
                //                Date date = new Date(newTopic.utime * 1000L);
                //                newTopic.time_text = DateUtils.SDF_D_M_H_M.format(date);
                //
                //                newTopic.answ = mainObj.getInt("answ");
                //                newTopic.is_voting = mainObj.optInt("is_voting");

                locTopics.add(newTopic);

            }

        } catch (Exception e) {

            S.L("Forum.parseTopics: " + Log.getStackTraceString(e));
        }

        return locTopics;
    }

    public static ArrayList<Message> parseMessages(String inputString) {
        ArrayList<Message> messages = new ArrayList<>();
        if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {
            return messages;
        }

        try {
            Message newMessage;
            JSONArray jArray = new JSONArray(inputString);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject mainObj = (JSONObject) jArray.get(i);

                newMessage = new Message();

                newMessage.id = mainObj.getLong("id");
                newMessage.n = mainObj.getInt("n");
                newMessage.text = mainObj.getString("text");
                newMessage.user = mainObj.getString("user");
                newMessage.vote = mainObj.getInt("vote");
                newMessage.utime = mainObj.getLong("utime");

                newMessage.setLoaded(true);
                newMessage.setDeleted(false);
                newMessage.setMessage(Html.fromHtml(newMessage.text));
                Date date = new Date(newMessage.utime * 1000L);
                newMessage.setTimeText(DateUtils.SDF_D_M_H_M.format(date));
                newMessage.setRepliedTo(MessagesUtils.extractReplies(newMessage.text));
                MessagesUtils.setQuotesInMessages(newMessage, messages);

                //                newMessage.isLoaded = true;
                //                newMessage.isDeleted = false;
                //
                //                Date date = new Date(newMessage.utime * 1000L);
                //                newMessage.timeText = DateUtils.SDF_D_M_H_M.format(date);
                //
                //                newMessage.repliedTo = extractReplies(textMessage);
                //                setQuotesInMessages(newMessage, messages);

                messages.add(newMessage);
            }

        } catch (Exception e) {

            S.L("Forum.parseMessages: " + Log.getStackTraceString(e));
        }

        return messages;
    }

    public static ResultContainer parseLogin(String inputString) {

        // {"error":"","userid":"11350","username":"vhl","hashkey":"00000000000000000000"}
        // {"error":"Ошибка авторизации ","userid":0,"username":""}

        final ResultContainer err = new ResultContainer();

        if (inputString == null || inputString.equals("{}") || inputString.isEmpty()) {
            err.result = false;
            err.resultStr = "Empty result string...";
            return err;
        }

        try {

            JSONObject mainObj = new JSONObject(inputString);

            String errorText = mainObj.getString(API.LOGIN_RESULT_ERROR);

            int userId = mainObj.getInt(API.LOGIN_RESULT_USERID);
            if (userId == 0) {
                err.result = false;
                err.resultStr = errorText;
                return err;
            }

            String cookieID = mainObj.getString(API.LOGIN_RESULT_SESSION_ID);
            if (cookieID.isEmpty()) {
                err.result = false;
                err.resultStr = errorText;
                return err;
            }

            err.result = true;
            err.userID = Integer.toString(userId);
            err.resultSessionID = cookieID;

        } catch (Exception e) {

            String res = Log.getStackTraceString(e);
            err.result = false;
            err.resultStr = res;

            S.L("Forum.parseLogin: " + res);
        }

        return err;

    }

    public static Topic parseTopicInfo(String inputString) {
        Topic newTopic = new Topic();

        try {

            if (inputString.equals("{}") || inputString.isEmpty() || inputString.equals("[\"Topic not found\"]")) {
                return newTopic;
            }

            JSONObject mainObj = new JSONObject(inputString);

            //            newTopic.id = mainObj.getLong("id");
            //            newTopic.text = Html.fromHtml(mainObj.getString("text"));
            //            newTopic.closed = mainObj.getInt("closed");
            //            newTopic.down = mainObj.getInt("down");
            //            newTopic.deleted = mainObj.getInt("deleted");
            //            newTopic.answ = mainObj.getInt("answers_count");
            //            newTopic.is_voting = mainObj.getInt("is_voting");
            //
            //            if (newTopic.is_voting == 1) {
            //                newTopic.votes = new ArrayList<>(5);
            //
            //                JSONArray jArray = mainObj.getJSONArray("voting");
            //                for (int i = 0; i < jArray.length(); i++) {
            //
            //                    JSONObject mainObj2 = (JSONObject) jArray.get(i);
            //
            //                    Topic.Votes newVote = newTopic.new Votes();
            //                    newVote.voteName = mainObj2.getString("select");
            //                    newVote.voteCount = mainObj2.getInt("result");
            //
            //                    newTopic.votes.add(newVote);
            //                }
            //            }

        } catch (Exception e) {
            S.L("Forum.parseTopicInfo: " + Log.getStackTraceString(e));
        }

        return newTopic;
    }

    public static User parseUserInfo(String inputString) {
        User user = new User();

        try {
            if (inputString.equals("{}") || inputString.isEmpty()) {
                return user;
            }

            JSONObject mainObj = new JSONObject(inputString);
//            user.id = getLong(mainObj, "id");
//            user.real_name = getString(mainObj, "real_name");
//            user.url = getString(mainObj, "url");
//            user.skype = getString(mainObj, "skype");
//            user.registered_unixtime = getDate(mainObj, "registered_unixtime");
//            user.is_moderator = getBool(mainObj, "is_moderator");
//            user.light_moderator = getBool(mainObj, "light_moderator");
//            user.town = getString(mainObj, "town");
//            user.country = getString(mainObj, "country");
//            user.expirience = getString(mainObj, "expirience");
//            user.interest = getString(mainObj, "interest");
//            user.profession = getString(mainObj, "profession");
//            user.birthyear = getInt(mainObj, "birthyear");
//            user.female = getBool(mainObj, "female");
//            user.last_acted = getDate(mainObj, "last_acted");
//            user.topics = getInt(mainObj, "topics");
//            user.messages = getInt(mainObj, "messages");
//            user.name = getString(mainObj, "name");
//            user.photo = getString(mainObj, "photo");
//            if (user.photo.equals("/")) {
//                user.photo = null;
//            } else if (user.photo.startsWith("/")) {
//                user.photo = API.MAIN_DOMAIN + user.photo;
//            }

        } catch (Exception e) {
            S.L("parseUserInfo: " + Log.getStackTraceString(e));
        }

        return user;
    }

    private static int getInt(JSONObject mainObj, String field) throws JSONException {
        if (mainObj.has(field)) {
            return mainObj.getInt(field);
        }
        return 0;
    }

    private static long getLong(JSONObject mainObj, String field) throws JSONException {
        if (mainObj.has(field)) {
            return mainObj.getLong(field);
        }
        return 0;
    }

    private static String getString(JSONObject mainObj, String field) throws JSONException {
        if (mainObj.has(field)) {
            return mainObj.getString(field);
        }
        return "";
    }

    private static Date getDate(JSONObject jsonObject, String field) throws JSONException {
        long value = jsonObject.getLong(field);
        if (value <= 0) {
            return null;
        }
        return new Date(value * 1000L);
    }

    private static boolean getBool(JSONObject mainObj, String field) throws JSONException {
        int value = mainObj.getInt(field);
        return value == 1;
    }

    public static Topic getTopicAnsw(String inputString) {

        try {

            if (inputString.equals("{}") || inputString.isEmpty() || inputString.equals("[\"Topic not found\"]")) {
                return null;
            }

            JSONObject mainObj = new JSONObject(inputString);

            Topic res = new Topic();
            //            res.answ = mainObj.getInt("answers_count");
            //            res.user = mainObj.getString("updated_name");
            //            res.utime = mainObj.getLong("updated");

            Date date = new Date(res.utime * 1000L);
            //            res.time_text = DateUtils.SDF_D_M_H_M.format(date);

            return res;

        } catch (Exception e) {

            S.L("Forum.getTopicAnsw: " + Log.getStackTraceString(e));
            return null;
        }

    }

    public static ArrayList<Section> parseSectionsList(String inputString) {

        ArrayList<Section> locSections = new ArrayList<Section>();
        String str = "";

        if (Empty.is(inputString) || inputString.equals("{}")) {
            String errorString = "Sections list empty...";
            S.L(errorString);
            return locSections;
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
        } catch (Exception e) {
            String res = Log.getStackTraceString(e);
            S.L("Forum.parseSectionsList: " + res);
        }

        return locSections;

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
        } catch (Exception e) {
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
        } catch (Exception e) {
            String res = Log.getStackTraceString(e);

            S.L("stringToArray: " + res);
        }

        return result;

    }

}

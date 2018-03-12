package com.mistareader.api;

import com.mistareader.api.NetRequestExecutor.Callback;
import com.mistareader.api.WebIteraction.RequestResult;
import com.mistareader.util.S;
import com.mistareader.util.textProcessors.JSONProcessor;
import com.mistareader.util.textProcessors.StringUtils;

public class API {

    // 718342
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=710089";
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=708776"; 0xFFFFFFF
    // http://www.forum.mista.ru/ajax_topic.php?id=719832 - html в заголовке

    final static String MAIN_URL = "https://www.forum.mista.ru/";
    //    final static String MAIN_URL = "http://192.168.1.100/";

    final static int DEFAULT_TOPICS_COUNT = 20;

    final static String AJAX_GET_TOPICS      = "ajax_index.php";
    final static String AJAX_GET_MESSAGES    = "ajax_topic.php";
    final static String AJAX_LOGIN           = "ajax_login.php";
    final static String AJAX_GETSECTIONSLIST = "ajax_getsectionslist.php";
    final static String AJAX_MYTOPICS        = "?mytopics=1";
    final static String AJAX_TOPICS_WITH_ME  = "ajax_mytopics.php?user_id=";
    final static String AJAX_NEWMESSAGE      = "ajax_newmessage.php";
    final static String AJAX_NEWTOPIC        = "index.php";
    final static String AJAX_TOPIC_INFO      = "ajax_gettopic.php";
    final static String AJAX_USER_INFO       = "ajax_user.php";

    final static String AJAX_GET_COOKIE   = "ajax_cookie.php";
    final static String AJAX_ADD_BOOKMARK = "ajax_addbookmark.php";

    public final static String action_New      = "new";
    public final static String action_Rename   = "rename_topic";
    public final static String action_Moderate = "moderate";

    public static final String LOGIN_RESULT_USERID     = "userid";
    public static final String LOGIN_RESULT_ERROR      = "error";
    public static final String LOGIN_RESULT_SESSION_ID = "hashkey";

    public static final String COOKIE_SESSION_ID = "entr_hash";
    public static final String COOKIE_USER_ID    = "entr_id";

    public API() {
    }

    private void execute(String url, ResponseType type, final ApiResult apiResult) {
        new NetRequestExecutor().execute(url, new Callback() {
            @Override
            public void onProcess(RequestResult result) {
                S.L(url + " " + result.resultStr);
                switch (type) {
                    case topics_list:
                        result.result = JSONProcessor.parseTopics(result.resultStr);
                        break;
                    case topic_info:
                        result.result = JSONProcessor.parseTopicInfo(result.resultStr);
                        break;
                    case messages_list:
                        result.result = JSONProcessor.parseMessages(result.resultStr);
                        break;
                    case user_info:
                        result.result = JSONProcessor.parseUserInfo(result.resultStr);
                        break;
                }
            }

            @Override
            public void onResult(RequestResult result) {
                apiResult.onResult(result.result);
            }
        });
    }

    public void getMessages(long currentTopicId, int from, int to, ApiResult apiResult) {
        String url = MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId + "&from=" + from + "&to=" + to;

        execute(url, ResponseType.messages_list, apiResult);
    }

    public static String getMessages(long currentTopicId, int from) {
        return MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId + "&from=" + from;
    }

    public static String getMessages(long currentTopicId) {
        return MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId;
    }

    public static String getTopics() {
        return MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS_COUNT;
    }

    public void getTopics(String sForum, String sSection, long beforeUTime, final ApiResult apiResult) {
        String url = MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS_COUNT;

        if (sForum != null && !sForum.isEmpty()) {
            url = url + "&forum=" + sForum.toLowerCase();
        }

        if (sSection != null && !sSection.isEmpty()) {
            url = url + "&section_short_name=" + sSection;

        }

        if (beforeUTime != 0) {
            url = url + "&beforeutime=" + beforeUTime;
        }

        execute(url, ResponseType.topics_list, apiResult);
    }

    public static String getLastTopics(String sForum, String sSection, long afterUTime) {
        String URL = MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS_COUNT;

        if (sForum != null && !sForum.isEmpty()) {
            URL = URL + "&forum=" + sForum.toLowerCase();
        }

        if (sSection != null && !sSection.isEmpty()) {
            URL = URL + "&section_short_name=" + sSection;

        }

        if (afterUTime != 0) {
            URL = URL + "&utime=" + afterUTime;
        }

        return URL;

    }

    public void getTopicsWithMe(String userID, long beforeUTime, final ApiResult apiResult) {
        String url = MAIN_URL + AJAX_TOPICS_WITH_ME + userID;

        if (beforeUTime != 0) {
            url = url + "&beforeutime=" + beforeUTime;
        }

        execute(url, ResponseType.topics_list, apiResult);
    }

    public void getMyTopics(String sessionID, String accountUserID, long beforeUTime, final ApiResult apiResult) {
        String url = MAIN_URL + AJAX_GET_TOPICS + AJAX_MYTOPICS;

        if (beforeUTime != 0) {
            url = url + "&beforeutime=" + beforeUTime;
        }

        //return WebIteraction.doServerRequest(urls[0], urls[1], urls[2], urls[3]);
        execute(url, ResponseType.topics_list, apiResult);
    }

    public static String login(String username, String password) {

        String userURLEnc;
        String passURLEnc;

        userURLEnc = StringUtils.mista_URL_Encode(username);
        passURLEnc = StringUtils.mista_URL_Encode(password);

        return MAIN_URL + AJAX_LOGIN + "?username=" + userURLEnc + "&password=" + passURLEnc;

    }

    public void getUser(String username, final ApiResult apiResult) {
        String url = MAIN_URL + AJAX_USER_INFO + "?username=" + StringUtils.mista_URL_Encode(username);

        execute(url, ResponseType.user_info, apiResult);
    }

    public static String getSectionsList() {
        return MAIN_URL + AJAX_GETSECTIONSLIST;
    }

    public static String postNewMessages() {
        return MAIN_URL + AJAX_NEWMESSAGE + "?jq=1";
        // return MAIN_URL + AJAX_NEWMESSAGE;
    }

    public static String addNewTopic() {
        return MAIN_URL + AJAX_NEWTOPIC;
    }

    public void getTopicInfo(long topicId, ApiResult apiResult) {
        String url = MAIN_URL + AJAX_TOPIC_INFO + "?id=" + topicId;

        execute(url, ResponseType.topic_info, apiResult);
    }

    public final static String POST_topic_text     = "topic_text=";
    public final static String POST_message_text   = "message_text=";
    public final static String POST_target_section = "target_section=";
    public final static String POST_target_forum   = "target_forum=";
    public final static String POST_topic_id       = "topic_id=";
    public final static String POST_user_name      = "user_name=";
    public final static String POST_action         = "action=";
    public final static String POST_rnd            = "rnd=";
    public final static String POST_vote           = "voting_select=";
    public final static String POST_last_n         = "last_n=";

    public final static String POST_voting  = "voting=1";          // 0,1
    public final static String POST_select1 = "select1=";
    public final static String POST_select2 = "select2=";
    public final static String POST_select3 = "select3=";
    public final static String POST_select4 = "select4=";
    public final static String POST_select5 = "select5=";

    public final static String POST_private_user_name = "private_user_name=";

    public final static String POST_voting_select = "voting_select=";
    public final static String POST_user_password = "user_password=";
    public final static String POST_as_admin      = "as_admin=";         // "true/false"

    public static String getCookies() {
        return MAIN_URL + AJAX_GET_COOKIE;
    }

}

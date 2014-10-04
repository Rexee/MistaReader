package com.mistareader;

import com.mistareader.TextProcessors.StringProcessor;

public class API {

    // 718342
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=710089";
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=708776"; 0xFFFFFFF
    // http://www.forum.mista.ru/ajax_topic.php?id=719832 - html в заголовке

    final static int           DEFAULT_TOPICS          = 20;
    final static String        MAIN_URL                = "http://www.forum.mista.ru/";
//    final static String MAIN_URL = "http://192.168.1.100/";

    final static String        AJAX_GET_TOPICS         = "ajax_index.php";
    final static String        AJAX_GET_MESSAGES       = "ajax_topic.php";
    final static String        AJAX_LOGIN              = "ajax_login.php";
    final static String        AJAX_GETSECTIONSLIST    = "ajax_getsectionslist.php";
    final static String        AJAX_MYTOPICS           = "?mytopics=1";
    final static String        AJAX_TOPICS_WITH_ME     = "ajax_mytopics.php?user_id=";
    final static String        AJAX_NEWMESSAGE         = "ajax_newmessage.php";
    final static String        AJAX_NEWTOPIC           = "index.php";
    final static String        AJAX_TOPIC_INFO         = "ajax_gettopic.php";

    final static String        AJAX_GET_COOKIE         = "ajax_cookie.php";
    final static String        AJAX_ADD_BOOKMARK       = "ajax_addbookmark.php";

    public final static String action_New              = "new";
    public final static String action_Rename           = "rename_topic";
    public final static String action_Moderate         = "moderate";

    public static final String LOGIN_RESULT_USERID     = "userid";
    public static final String LOGIN_RESULT_ERROR      = "error";
    public static final String LOGIN_RESULT_SESSION_ID = "hashkey";

    public static final String MYTOPICS                = "mMyTopics";
    public static final String TOPICS_WITH_ME          = "mTopicsWithMe";

    public static final String COOKIE_SESSION_ID       = "entr_hash";
    public static final String COOKIE_USER_ID          = "entr_id";

    public static String getMessages(long currentTopicId, int from, int to) {
        return MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId + "&from=" + from + "&to=" + to;
    }

    public static String getMessages(long currentTopicId, int from) {
        return MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId + "&from=" + from;
    }

    public static String getMessages(long currentTopicId) {
        return MAIN_URL + AJAX_GET_MESSAGES + "?id=" + currentTopicId;
    }

    public static String getTopics() {
        return MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS;
    }

    public static String getTopics(String sForum, String sSection, long beforeUTime) {

        String URL = MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS;

        if (sForum != null && !sForum.isEmpty()) {
            URL = URL + "&forum=" + sForum.toLowerCase();
        }

        if (sSection != null && !sSection.isEmpty()) {
            URL = URL + "&section_short_name=" + sSection;

        }

        if (beforeUTime != 0) {
            URL = URL + "&beforeutime=" + beforeUTime;
        }

        return URL;

    }

    public static String getLastTopics(String sForum, String sSection, long afterUTime) {

        String URL = MAIN_URL + AJAX_GET_TOPICS + "?topics=" + DEFAULT_TOPICS;

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

    public static String getTopicsWithMe(String userID, long beforeUTime) {

        String URL = MAIN_URL + AJAX_TOPICS_WITH_ME + userID;

        if (beforeUTime != 0) {
            URL = URL + "&beforeutime=" + beforeUTime;
        }

        return URL;

    }

    public static String getMyTopics(long beforeUTime) {

        String URL = MAIN_URL + AJAX_GET_TOPICS + AJAX_MYTOPICS;

        if (beforeUTime != 0) {
            URL = URL + "&beforeutime=" + beforeUTime;
        }

        return URL;

    }

    public static String Login(String username, String password) {

        String userURLEnc;
        String passURLEnc;

        userURLEnc = StringProcessor.mista_URL_Encode(username);
        passURLEnc = StringProcessor.mista_URL_Encode(password);

        return MAIN_URL + AJAX_LOGIN + "?username=" + userURLEnc + "&password=" + passURLEnc;

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

    public static String getTopicInfo(long topicId) {
        return MAIN_URL + AJAX_TOPIC_INFO + "?id=" + topicId;
    }

    public final static String POST_topic_text        = "topic_text=";
    public final static String POST_message_text      = "message_text=";
    public final static String POST_target_section    = "target_section=";
    public final static String POST_target_forum      = "target_forum=";
    public final static String POST_topic_id          = "topic_id=";
    public final static String POST_user_name         = "user_name=";
    public final static String POST_action            = "action=";
    public final static String POST_rnd               = "rnd=";
    public final static String POST_vote              = "voting_select=";
    public final static String POST_last_n            = "last_n=";

    public final static String POST_voting            = "voting=1";          // 0,1
    public final static String POST_select1           = "select1=";
    public final static String POST_select2           = "select2=";
    public final static String POST_select3           = "select3=";
    public final static String POST_select4           = "select4=";
    public final static String POST_select5           = "select5=";

    public final static String POST_private_user_name = "private_user_name=";

    public final static String POST_voting_select     = "voting_select=";
    public final static String POST_user_password     = "user_password=";
    public final static String POST_as_admin          = "as_admin=";         // "true/false"

    public static String getCookies() {
        return MAIN_URL + AJAX_GET_COOKIE;
    }

}

package com.mistareader.api;

import android.content.Context;

import com.mistareader.model.Login;
import com.mistareader.model.Message;
import com.mistareader.model.Section;
import com.mistareader.model.Topic;
import com.mistareader.model.User;
import com.mistareader.util.Empty;
import com.mistareader.util.textProcessors.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NetProvider {
    private ArrayList<BasicApiCallback> mCallbacks;
    private Context                     mContext;
    private APIService                  mApi;

    public NetProvider(Context context) {
        mContext = context;
        mCallbacks = new ArrayList<>();
        mApi = API.get(context).getApi();
    }

    private <T> BasicApiCallback<T> getCallback(NetResponse<T> netResponse) {
        BasicApiCallback<T> callback = new BasicApiCallback<>(netResponse, mContext);
        mCallbacks.add(callback);
        return callback;
    }

    public void release() {
        mContext = null;
        if (mCallbacks != null) {
            for (BasicApiCallback callback : mCallbacks) {
                callback.release();
            }
        }
    }

    //region [API FUNCTIONS]
    public void getTopicInfo(long topicId, NetResponse<Topic> netResponse) {
        mApi.getTopicInfo(topicId).enqueue(getCallback(netResponse));
    }

    public void getMessages(long topicId, int messagesFrom, int messagesTo, NetResponse<List<Message>> netResponse) {
        mApi.getMessages(topicId, messagesFrom, messagesTo).enqueue(getCallback(netResponse));
    }

    public void getTopics(String forum, String section, Long beforeUTime, Long uTime, Integer isMy, NetResponse<List<Topic>> netResponse) {
        mApi.getTopics(API.DEFAULT_TOPICS_COUNT, forum, section, beforeUTime, uTime, isMy).enqueue(getCallback(netResponse));
    }

    public void getTopics(String forum, String section, long beforeUTime, NetResponse<List<Topic>> netResponse) {
        String paramForum = null;
        String paramSection = null;
        Long paramBeforeUtime = null;
        if (!Empty.is(forum)) {
            paramForum = forum.toLowerCase();
        }
        if (!Empty.is(section)) {
            paramSection = section;
        }
        if (beforeUTime != 0) {
            paramBeforeUtime = beforeUTime;
        }

        getTopics(paramForum, paramSection, paramBeforeUtime, null, null, netResponse);
    }

    public void getLastTopics(String forum, String section, long afterUTime, NetResponse<List<Topic>> netResponse) {
        String paramForum = null;
        String paramSection = null;
        Long utime = null;
        if (!Empty.is(forum)) {
            paramForum = forum.toLowerCase();
        }
        if (!Empty.is(section)) {
            paramSection = section;
        }
        if (afterUTime != 0) {
            utime = afterUTime;
        }

        getTopics(paramForum, paramSection, null, utime, null, netResponse);
    }

    public void getMyTopics(long beforeUTime, NetResponse<List<Topic>> netResponse) {
        Long paramBeforeUtime = null;

        if (beforeUTime != 0) {
            paramBeforeUtime = beforeUTime;
        }

        getTopics(null, null, paramBeforeUtime, null, 1, netResponse);
    }

    public void getSectionsList(NetResponse<List<Section>> netResponse) {
        mApi.getSections().enqueue(getCallback(netResponse));
    }

    public void getTopicsWithMe(String userID, long beforeUTime, final NetResponse<List<Topic>> netResponse) {
        Long paramBeforeUtime = null;
        if (beforeUTime != 0) {
            paramBeforeUtime = beforeUTime;
        }

        mApi.getTopicsWithMe(userID, paramBeforeUtime).enqueue(getCallback(netResponse));
    }

    public void getUser(String username, String userId, NetResponse<User> netResponse) {
        mApi.getUser(username, userId).enqueue(getCallback(netResponse));
    }

    public void login(String username, String password, NetResponse<Login> netResponse) {
        String userURLEnc = StringUtils.mista_URL_Encode(username);
        String passURLEnc = StringUtils.mista_URL_Encode(password);

        mApi.login(userURLEnc, passURLEnc).enqueue(getCallback(netResponse));
    }

    public static String postNewMessages() {
        //        return ROOT_ENDPOINT + AJAX_NEWMESSAGE + "?jq=1";
        // return ROOT_ENDPOINT + AJAX_NEWMESSAGE;
        return null;
    }

    public static String addNewTopic() {
        //        return ROOT_ENDPOINT + AJAX_NEWTOPIC;
        return null;
    }

    public void onLogin(Context context) {
        API.get(context).onLogin();
    }
    //endregion
}

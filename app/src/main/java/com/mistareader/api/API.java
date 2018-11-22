package com.mistareader.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mistareader.BuildConfig;
import com.mistareader.json.LoganSquareConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;

public class API {
    private static volatile API instance;

    // 718342
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=710089";
    // URL = "http://www.forum.mista.ru/ajax_topic.php?id=708776"; 0xFFFFFFF
    // http://www.forum.mista.ru/ajax_topic.php?id=719832 - html в заголовке

    // final static String ROOT_ENDPOINT = "http://192.168.1.100/";

    public final static String MAIN_DOMAIN = "mista.ru";
    public final static String BASE_URL    = "https://www." + MAIN_DOMAIN;

    public final static int DEFAULT_PAGE         = 0;
    public final static int DEFAULT_TOPICS_COUNT = 20;

    public static final String COOKIE_SESSION_HASH_KEY = "entr_hash";
    public static final String COOKIE_USER_ID          = "entr_id";
    public static final String COOKIE_SESSION_KEY      = "entr_key";

    final static String AJAX_NEWMESSAGE = "ajax_newmessage.php";
    final static String AJAX_NEWTOPIC   = "index.php";

    final static String AJAX_GET_COOKIE   = "ajax_cookie.php";
    final static String AJAX_ADD_BOOKMARK = "ajax_addbookmark.php";

    public final static String action_New      = "new";
    public final static String action_Rename   = "rename_topic";
    public final static String action_Moderate = "moderate";

    public static final String LOGIN_RESULT_USERID     = "userid";
    public static final String LOGIN_RESULT_ERROR      = "error";
    public static final String LOGIN_RESULT_SESSION_ID = "hashkey";


    private APIService    api;
    private CookieManager cookieManager;

    public static API get(Context context) {
        if (instance == null) {
            synchronized (API.class) {
                if (instance == null) {
                    instance = new API(context);
                }
            }
        }
        return instance;
    }

    public API(Context context) {
        OkHttpClient.Builder okHttpBuilder = new Builder();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        cookieManager = new CookieManager(prefs);
        okHttpBuilder.cookieJar(cookieManager);

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY));
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(LoganSquareConverterFactory.create())
                .client(okHttpBuilder.build())
                .build();
        api = retrofit.create(APIService.class);
    }

    public APIService getApi() {
        return api;
    }

    public void onLogin() {
        cookieManager.loadSessionCookies();
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
}

package com.mistareader.api;

import com.mistareader.model.Login;
import com.mistareader.model.Message;
import com.mistareader.model.Section;
import com.mistareader.model.Topic;
import com.mistareader.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("ajax_topic.php")
    Call<List<Message>> getMessages(@Query("id") long topicId,
                                    @Query("from") int from,
                                    @Query("to") int to);

    @GET("ajax_gettopic.php")
    Call<Topic> getTopicInfo(@Query("id") long topicId);

    @GET("ajax_index.php")
    Call<List<Topic>> getTopics(@Query("topics") int count,
                                @Query("forum") String forum,
                                @Query("section_short_name") String section_short_name,
                                @Query("beforeutime") Long beforeutime,
                                @Query("utime") Long utime,
                                @Query("mytopics") Integer isMy);

    @GET("ajax_mytopics.php")
    Call<List<Topic>> getTopicsWithMe(@Query("user_id") String userId,
                                      @Query("beforeutime") Long beforeutime);

    @GET("ajax_user.php")
    Call<User> getUser(@Query("username") String username, @Query("user_id") String user_id);

    @GET("ajax_getsectionslist.php")
    Call<List<Section>> getSections();

    @GET("ajax_login.php")
    Call<Login> login(@Query("username") String username,
                      @Query("password") String password);
}

package com.mistareader.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.mistareader.api.API;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class User {
    public String  error;
    public long    id;
    public String  real_name;
    public String  url;
    public String  skype;
    public long    registered_unixtime;
    public boolean is_moderator;
    public boolean light_moderator;
    public String  town;
    public String  country;
    public String  expirience;
    public String  interest;
    public String  profession;
    public int     birthyear;
    public Boolean female;
    public long    last_acted;
    public int     topics;
    public int     messages;
    public String  name;
    public String  photo;

    //    public String expert;
    //    public String moder_blocked;
    //    public String photo_descr;


    public User() {
    }

    @OnJsonParseComplete
    void onParseComplete() {
        registered_unixtime *= 1000;
        last_acted *= 1000;

        if (photo != null) {
            if (photo.equals("/")) {
                photo = null;
            } else if (photo.startsWith("/")) {
                photo = API.BASE_URL + photo;
            }
        }
    }
}


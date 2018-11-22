package com.mistareader.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;

import okhttp3.Cookie;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class MistaCookie {
    public String name;
    public String value;
    public long   expiresAt;
    public String domain;
    public String path;

    public MistaCookie() {
    }

    public MistaCookie(Cookie cookie) {
        name = cookie.name();
        value = cookie.value();
        expiresAt = cookie.expiresAt();
        domain = cookie.domain();
        path = cookie.path();
    }
}

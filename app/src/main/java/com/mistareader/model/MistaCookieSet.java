package com.mistareader.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.mistareader.api.API;
import com.mistareader.util.Empty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class MistaCookieSet {
    public HashMap<String, MistaCookie> cookiesSet;

    public MistaCookieSet() {
        cookiesSet = new HashMap<>();
    }

    public void addTo(ArrayList<Cookie> cookies) {
        if (Empty.is(cookiesSet)) {
            return;
        }
        for (MistaCookie cookie : cookiesSet.values()) {
            cookies.add(new Cookie.Builder()
                    .name(cookie.name)
                    .value(cookie.value)
                    .domain(cookie.domain)
                    .build());
        }
    }

    public boolean addFrom(List<Cookie> cookies) {
        if (Empty.is(cookies)) {
            return false;
        }
        boolean hasChanges = false;
        MistaCookie newCookie;
        for (Cookie cookie : cookies) {
            if (cookie.name().equals(API.COOKIE_SESSION_HASH_KEY) || cookie.name().equals(API.COOKIE_USER_ID)) {
                continue;
            }
            newCookie = new MistaCookie(cookie);
            cookiesSet.put(newCookie.name, newCookie);
            hasChanges = true;
        }
        return hasChanges;
    }
}

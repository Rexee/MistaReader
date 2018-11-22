package com.mistareader.api;

import android.content.SharedPreferences;

import com.bluelinelabs.logansquare.LoganSquare;
import com.mistareader.model.MistaCookieSet;
import com.mistareader.util.Empty;
import com.mistareader.util.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieManager implements CookieJar {
    private ArrayList<Cookie> cookies;
    private MistaCookieSet    otherCookies;
    private SharedPreferences prefs;

    public CookieManager(SharedPreferences prefs) {
        this.prefs = prefs;
        cookies = new ArrayList<>();

        loadSessionCookies();

        String additionalList = prefs.getString(Settings.SETTINGS_COOKIES_LIST, "");
        if (!Empty.is(additionalList)) {
            try {
                otherCookies = LoganSquare.parse(additionalList, MistaCookieSet.class);
                if (otherCookies != null) {
                    otherCookies.addTo(cookies);
                }
            } catch (IOException ignored) {
            }
        }
        if (otherCookies == null) {
            otherCookies = new MistaCookieSet();
        }
    }

    public void loadSessionCookies() {
        String sessionHash = prefs.getString(Settings.SETTINGS_SESSION_HASH_KEY, "");
        String accountId = prefs.getString(Settings.SETTINGS_ACCOUNT_USER_ID, "");
        if (!Empty.is(sessionHash)) {
            cookies.add(new Cookie.Builder().name(API.COOKIE_SESSION_HASH_KEY).domain(API.MAIN_DOMAIN).value(sessionHash).build());
        }
        if (!Empty.is(accountId)) {
            cookies.add(new Cookie.Builder().name(API.COOKIE_USER_ID).domain(API.MAIN_DOMAIN).value(accountId).build());
        }
    }

    @Override public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        //        Set-Cookie: entr_key=abqnp6pg714i74qvl9627smdicgdlm0o; expires=Mon, 10-Dec-2018 17:32:00 GMT; Max-Age=8640000; path=/; domain=mista.ru; HttpOnly
        //        Set-Cookie: entr_id=11350; expires=Sun, 01-Sep-2019 15:23:38 GMT; Max-Age=31536000; domain=.mista.ru
        //        Set-Cookie: entr_hash=3163305577e6ae73a15e5c94e4cf3b0a; expires=Sun, 01-Sep-2019 15:23:38 GMT; Max-Age=31536000; domain=.mista.ru

        if (otherCookies.addFrom(cookies)) {
            try {
                String json = LoganSquare.serialize(otherCookies);
                prefs.edit().putString(Settings.SETTINGS_COOKIES_LIST, json).commit();
            } catch (IOException ignored) {
            }
        }
    }

    @Override public List<Cookie> loadForRequest(HttpUrl url) {
        return cookies;
    }
}

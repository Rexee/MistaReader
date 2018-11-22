package com.mistareader.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    public final static String THEME_DEFAULT     = "1";
    public final static int    THEME_DEFAULT_OLD = 1;

    public final static String SETTINGS_ACCOUNT_NAME     = "accName";
    public final static String SETTINGS_ACCOUNT_PASS     = "accPass";
    public final static String SETTINGS_SESSION_HASH_KEY = "sessionID";
    public final static String SETTINGS_ACCOUNT_USER_ID  = "accUID";
    public final static String SETTINGS_COOKIES_LIST     = "cookies_list";
    public final static String SETTINGS_SECTIONS         = "SECTIONS";
    public final static String SETTINGS_FORUMS           = "FORUMS";
    public final static String SETTINGS_VERSION          = "VERSION";

    /**
     * @deprecated not use this approach anymore
     */
    @Deprecated
    public final static String SETTINGS_COOKIES = "cookies";

    public static final String SETTINGS_RELOAD_SECTIONS = "settingsReloadSections";
    public static final String SETTINGS_ACCOUNT         = "settingsAccont";
    public static final String SETTINGS_THEME           = "settingsThemes";
    public final static String SUBSCRIPTIONS_INTERVAL   = "settingsSubscriptionsInterval";
    public final static String SUBSCRIPTIONS_USE        = "settingsSubscriptionsUse";
    public final static String NOTIFICATIONS_USE        = "settingsNotificationsUse";
    public final static String NOTIFICATIONS_VIBRATE    = "settingsNotificationsVibrate";
    public final static String NOTIFICATIONS_SOUND      = "settingsNotificationsSound";
    public final static String NOTIFICATIONS_LED        = "settingsNotificationsLED";

    public static final int SUBSCRIPTIONS_MAX_COUNT = 10;

    public final static String SETTINGS_VERSION_N              = "1.5.2";
    public static final String SUBSCRIPTIONS_UPDATED_BROADCAST = "com.mistareader.BROADCAST";

    public static String getCurrentTheme(Activity activity) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        try {
            return pref.getString(Settings.SETTINGS_THEME, THEME_DEFAULT);
        } catch (Exception e) {
            String strValue = String.valueOf(pref.getInt(Settings.SETTINGS_THEME, THEME_DEFAULT_OLD));
            pref.edit()
                    .putString(Settings.SETTINGS_THEME, strValue)
                    .apply();
            return strValue;
        }
    }
}

package com.mistareader;

import SettingsFragment.Settings_Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings_Activity extends PreferenceActivity implements Forum.iOnLoggedIn, Forum.iOnThemeChanged {

    final static String        SETTINGS_ACCOUNT_NAME           = "accName";
    final static String        SETTINGS_ACCOUNT_PASS           = "accPass";
    final static String        SETTINGS_SESSION_ID             = "sessionID";
    final static String        SETTINGS_ACCOUNT_USER_ID        = "accUID";
    final static String        SETTINGS_SECTIONS               = "SECTIONS";
    final static String        SETTINGS_FORUMS                 = "FORUMS";
    final static String        SETTINGS_VESION                 = "VERSION";

    public final static String SETTINGS_NOTIFICATIONS_INTERVAL = "settingsNotificationsInterval";
    public final static String SETTINGS_NOTIFICATIONS_USE      = "settingsNotificationsUse";
    public final static String SETTINGS_NOTIFICATIONS_BAR      = "settingsNotificationsShowInBar";

    final static String        SETTINGS_VESION_N               = "1.3";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ThemesManager.onActivityCreateSetTheme(this);

        super.onCreate(savedInstanceState);

        Settings_Fragment frag = new Settings_Fragment();
        frag.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(android.R.id.content, frag).commit();

    }

    @Override
    public void onLoggedIn(boolean isLoggedIn) {

        Topics_Activity.isLoginChanged = true;

    }

    @Override
    public void onThemeChanged(int newTheme) {

        ThemesManager.CurrentTheme = newTheme;

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);

        Forum forum = Forum.getInstance();
        forum.saveSettings(sPref);

        ThemesManager.changeTheme(Settings_Activity.this, newTheme);

        Topics_Activity.isThemeChanged = true;

    }

}

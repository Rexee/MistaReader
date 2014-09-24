package com.mistareader;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class Settings_Fragment extends PreferenceFragment {

    Forum forum;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        forum = Forum.getInstance();

        Preference settingsAccont = findPreference("settingsAccont");

        settingsAccont.setSummary(R.string.sNotAuthorized);
        
        final Activity activity = getActivity();

        settingsAccont.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                forum.setupAccount(activity);
                return false;
            }
        });

        Preference settingsTheme = findPreference("settingsThemes");
        settingsTheme.setSummary(ThemesManager.getCurrentThemeName(activity));
        settingsTheme.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                forum.selectTheme(activity);
                return false;
            }
        });

        final ListPreference settingsNotifInterval = (ListPreference) findPreference(Settings.SUBSCRIPTIONS_INTERVAL);
        String currentValue = settingsNotifInterval.getValue();
        if (currentValue == null) {
            settingsNotifInterval.setValue((String) settingsNotifInterval.getEntryValues()[0]);
            currentValue = settingsNotifInterval.getValue();
        }

        updateDescription(settingsNotifInterval, currentValue);

        settingsNotifInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateDescription(settingsNotifInterval, newValue.toString());
                return true;
            }
        });
        
//        ((CheckBoxPreference) findPreference(Settings.NOTIFICATIONS_USE)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
//
//                Notifications.refreshNotificationsShedule((boolean) newValue, getActivity());
//
//                return true;
//            }
//        });
      
    }

    private void updateDescription(ListPreference backgroundTimeout, String value) {
        int index = backgroundTimeout.findIndexOfValue(value);
        backgroundTimeout.setSummary(backgroundTimeout.getEntries()[index]);
    }

    
    public void updateAccountDescription(boolean isLoggedIn) {

        Preference settingsAccont = findPreference("settingsAccont");
        if (isLoggedIn) {
            settingsAccont.setSummary(forum.accountName);
        }
        else
        {
            settingsAccont.setSummary(R.string.sNotAuthorized);
        }
        
    }

}

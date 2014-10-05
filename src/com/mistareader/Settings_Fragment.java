package com.mistareader;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings_Fragment extends PreferenceFragment {

    private static final String SETTINGS_RELOAD_SECTIONS = "settingsReloadSections";
    private static final String SETTINGS_THEMES          = "settingsThemes";
    private static final String SETTINGS_ACCOUNT         = "settingsAccont";

    Forum                       forum;

    public interface iOnSubsChange {
        void onSubscriptionsSettingsChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ThemesManager.colorBg_message_body);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        forum = Forum.getInstance();

        Preference settingsAccont = findPreference(SETTINGS_ACCOUNT);

        if (forum.sessionID.isEmpty())
            settingsAccont.setSummary(R.string.sNotAuthorized);
        else
            settingsAccont.setSummary(forum.accountName);

        final Activity activity = getActivity();

        settingsAccont.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                forum.setupAccount(activity);
                return false;
            }
        });

        Preference settingsTheme = findPreference(SETTINGS_THEMES);
        settingsTheme.setSummary(ThemesManager.getCurrentThemeName(activity));
        settingsTheme.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                forum.selectTheme(activity);
                return false;
            }
        });

        // SUBSCRIPTIONS***********************
        final iOnSubsChange mCallbackOnIntervalChanged;
        mCallbackOnIntervalChanged = (iOnSubsChange) activity;

        CheckBoxPreference settingsSubsUse = (CheckBoxPreference) findPreference(Settings.SUBSCRIPTIONS_USE);
        settingsSubsUse.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mCallbackOnIntervalChanged.onSubscriptionsSettingsChanged();
                return true;
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
                mCallbackOnIntervalChanged.onSubscriptionsSettingsChanged();
                updateDescription(settingsNotifInterval, newValue.toString());
                return true;
            }
        });

        // ((CheckBoxPreference) findPreference(Settings.NOTIFICATIONS_USE)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        // @Override
        // public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        //
        // Notifications.refreshNotificationsShedule((boolean) newValue, getActivity());
        //
        // return true;
        // }
        // });

        Preference settingsReloadSections = (Preference) findPreference(SETTINGS_RELOAD_SECTIONS);
        settingsReloadSections.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                forum.reloadSections(getActivity());
                return false;
            }
        });

    }

    private void updateDescription(ListPreference backgroundTimeout, String value) {
        int index = backgroundTimeout.findIndexOfValue(value);
        backgroundTimeout.setSummary(backgroundTimeout.getEntries()[index]);
    }

    public void updateAccountDescription(boolean isLoggedIn) {

        Preference settingsAccont = findPreference(SETTINGS_ACCOUNT);
        if (isLoggedIn) {
            settingsAccont.setSummary(forum.accountName);
        }
        else {
            settingsAccont.setSummary(R.string.sNotAuthorized);
        }

    }

    public void updateThemeDescription() {
        Preference settingsTheme = findPreference(SETTINGS_THEMES);
        settingsTheme.setSummary(ThemesManager.getCurrentThemeName(getActivity()));
    }

}

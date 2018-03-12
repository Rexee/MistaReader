package com.mistareader.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mistareader.R;
import com.mistareader.model.Forum;
import com.mistareader.util.Settings;

public class SettingsFragment extends PreferenceFragment {
    public interface SettingsCallbacks {
        void onThemeChanged();
    }

    private Forum             forum;
    private SettingsCallbacks callbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsCallbacks) {
            callbacks = (SettingsCallbacks) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forum = Forum.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference settingsAccount = findPreference(Settings.SETTINGS_ACCOUNT);
        settingsAccount.setOnPreferenceClickListener(preference -> {
            forum.setupAccount(getActivity());
            return false;
        });
        updateAccountDescription(forum.isLoggedIn());

        Preference settingsReloadSections = findPreference(Settings.SETTINGS_RELOAD_SECTIONS);
        settingsReloadSections.setOnPreferenceClickListener(preference -> {
            forum.reloadSections(getActivity());
            return false;
        });

        bindValueToSummary(Settings.SETTINGS_THEME);
        bindValueToSummary(Settings.SUBSCRIPTIONS_INTERVAL);

        return view;
    }

    private void bindValueToSummary(String key) {
        Preference pref = findPreference(key);
        pref.setOnPreferenceChangeListener((preference, value) -> {
            if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                if (preference.getKey().equals(Settings.SETTINGS_THEME)) {
                    if (callbacks != null) {
                        callbacks.onThemeChanged();
                    }
                }
            }
            return true;
        });

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

    public void updateAccountDescription(boolean isLoggedIn) {
        Preference settingsAccount = findPreference(Settings.SETTINGS_ACCOUNT);
        if (isLoggedIn) {
            settingsAccount.setSummary(forum.accountName);
        } else {
            settingsAccount.setSummary(R.string.sNotAuthorized);
        }
    }
}

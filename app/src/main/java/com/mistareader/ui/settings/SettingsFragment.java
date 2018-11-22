package com.mistareader.ui.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.mistareader.Forum;
import com.mistareader.R;
import com.mistareader.util.Settings;
import com.mistareader.util.ThemesManager;

public class SettingsFragment extends PreferenceFragment {
    public interface SettingsCallbacks {
        void onThemeChanged();

        void onLogin(String username, String password);

        void onReloadSections();
    }

    private Forum             forum;
    private SettingsCallbacks mCallback;
    private Preference        settingsAccount;
    private Preference        settingsTheme;
    private TextInputEditText usernameBox;
    private TextInputEditText passwordBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forum = Forum.getInstance();
        if (getActivity() instanceof SettingsCallbacks) {
            mCallback = (SettingsCallbacks) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        settingsAccount = findPreference(Settings.SETTINGS_ACCOUNT);
        settingsTheme = findPreference(Settings.SETTINGS_THEME);
        int tintColor = ThemesManager.getColorByAttr(getActivity(), R.attr.toolbarIconsTint);
        settingsAccount.setIcon(ThemesManager.tint(getActivity(), R.drawable.ic_account, tintColor));
        settingsTheme.setIcon(ThemesManager.tint(getActivity(), R.drawable.ic_theme, tintColor));

        settingsAccount.setOnPreferenceClickListener(preference -> {
            onLoginClick();
            return false;
        });
        updateAccountDescription(forum.isLoggedIn, forum.accountName);

        Preference settingsReloadSections = findPreference(Settings.SETTINGS_RELOAD_SECTIONS);
        settingsReloadSections.setOnPreferenceClickListener(preference -> {
            mCallback.onReloadSections();
            return false;
        });

        bindValueToSummary(Settings.SETTINGS_THEME);
//        bindValueToSummary(Settings.SUBSCRIPTIONS_INTERVAL);

        return view;
    }

    private void onLoginClick() {
        int dialogTheme = ThemesManager.getAttributeReferenceResourceId(getActivity(), R.attr.alertDialogTheme);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);
        builder.setView(view);
        builder.setCancelable(true);

        usernameBox = view.findViewById(R.id.login_username);
        usernameBox.setText(forum.accountName);

        passwordBox = view.findViewById(R.id.login_password);
        passwordBox.setText(forum.getPassword());

        usernameBox.requestFocus();
        usernameBox.requestFocusFromTouch();

        builder.setPositiveButton(R.string.sDoLogin, (dialogInterface, i) -> login());
        builder.setNegativeButton(R.string.sCancel, null);

        passwordBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                login();
                return true;
            }
            return false;
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }

    private void login() {
        String username = usernameBox.getText().toString().trim();
        String password = passwordBox.getText().toString();
        mCallback.onLogin(username, password);
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
                    if (mCallback != null) {
                        mCallback.onThemeChanged();
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

    public void updateAccountDescription(boolean isLoggedIn, String username) {
        Preference settingsAccount = findPreference(Settings.SETTINGS_ACCOUNT);
        if (isLoggedIn) {
            settingsAccount.setSummary(username);
        } else {
            settingsAccount.setSummary(R.string.sNotAuthorized);
        }
    }
}

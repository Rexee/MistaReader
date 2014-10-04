package com.mistareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings_Activity extends PreferenceActivity implements Forum.iOnLoggedIn, Forum.iOnThemeChanged, Settings_Fragment.iOnSubsChange {

    Settings_Fragment settingsFragment;
    boolean           isLoginChanged                = false;
    boolean           isThemeChanged                = false;
    boolean           isSubscriptionChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ThemesManager.onActivityCreateSetTheme(this);

        super.onCreate(savedInstanceState);

        settingsFragment = new Settings_Fragment();
        settingsFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(android.R.id.content, settingsFragment).commit();

    }

    @Override
    public void onLoggedIn(boolean isLoggedIn) {

        isLoginChanged = true;
        settingsFragment.updateAccountDescription(isLoggedIn);

        updateResult();
    }

    @Override
    public void onThemeChanged(int newTheme) {

        ThemesManager.CurrentTheme = newTheme;

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);

        Forum forum = Forum.getInstance();
        forum.saveSettings(sPref);

        settingsFragment.updateThemeDescription();

        isThemeChanged = true;

        updateResult();

    }

    private void updateResult() {
        Intent intent = new Intent();
        intent.putExtra("isLoginChanged", isLoginChanged);
        intent.putExtra("isThemeChanged", isThemeChanged);
        intent.putExtra("isSubscriptionChanged", isSubscriptionChanged);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onSubscriptionsSettingsChanged() {
        isSubscriptionChanged = true;
        updateResult();        
    }

}

package com.mistareader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings_Activity extends PreferenceActivity implements Forum.iOnLoggedIn, Forum.iOnThemeChanged {

    Settings_Fragment settingsFragment;
    
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

        Topics_Activity.isLoginChanged = true;
        settingsFragment.updateAccountDescription(isLoggedIn);

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

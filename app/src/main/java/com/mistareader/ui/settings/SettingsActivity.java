package com.mistareader.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import com.mistareader.Forum;
import com.mistareader.R;
import com.mistareader.ui.BaseNetworkActivity;
import com.mistareader.ui.settings.SettingsFragment.SettingsCallbacks;
import com.mistareader.util.Empty;

public class SettingsActivity extends BaseNetworkActivity implements SettingsCallbacks {
    private boolean          isLoginChanged        = false;
    private boolean          isThemeChanged        = false;
    private boolean          isSubscriptionChanged = false;
    private SettingsFragment settingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        showArrowBack();

        settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(getIntent().getExtras());

        getFragmentManager().beginTransaction().replace(R.id.content, settingsFragment).commit();
    }

    private void updateResult() {
        Intent intent = new Intent();
        intent.putExtra("isLoginChanged", isLoginChanged);
        intent.putExtra("isThemeChanged", isThemeChanged);
        intent.putExtra("isSubscriptionChanged", isSubscriptionChanged);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onThemeChanged() {
        recreate();
    }

    @Override public void onLogin(String username, String password) {
        if (Empty.is(username) || Empty.is(password)) {
            return;
        }

        netProvider.login(username, password, result -> {
            isLoginChanged = true;
            if (result != null) {
                Forum forum = Forum.getInstance();
                forum.onLoginFinished(username, password, result);
            }
            netProvider.onLogin(this);
            settingsFragment.updateAccountDescription(result != null, username);
            updateResult();
        });
    }

    @Override public void onReloadSections() {
        netProvider.getSectionsList(result -> Forum.getInstance().setSections(result));
    }
}

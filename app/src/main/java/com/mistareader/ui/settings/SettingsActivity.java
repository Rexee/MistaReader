package com.mistareader.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import com.mistareader.R;
import com.mistareader.ui.BaseActivity;
import com.mistareader.ui.settings.SettingsFragment.SettingsCallbacks;

public class SettingsActivity extends BaseActivity implements SettingsCallbacks {
    private boolean isLoginChanged        = false;
    private boolean isThemeChanged        = false;
    private boolean isSubscriptionChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        showArrowBack();

        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(getIntent().getExtras());

        getFragmentManager().beginTransaction().replace(R.id.content, settingsFragment).commit();
    }



    //    @Override
    //    public void onLoggedIn(boolean isLoggedIn) {
    //        isLoginChanged = true;
    //        settingsFragment.updateAccountDescription(isLoggedIn);
    //
    //        updateResult();
    //    }
    //
    //    @Override
    //    public void onThemeChanged(int newTheme) {
    //        ThemesManager.currentTheme = newTheme;
    //
    //        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
    //
    //        Forum forum = Forum.getInstance();
    //        forum.saveSettings(sPref);
    //
    ////        settingsFragment.updateThemeDescription();
    //
    //        isThemeChanged = true;
    //
    //        updateResult();
    //    }

    private void updateResult() {
        Intent intent = new Intent();
        intent.putExtra("isLoginChanged", isLoginChanged);
        intent.putExtra("isThemeChanged", isThemeChanged);
        intent.putExtra("isSubscriptionChanged", isSubscriptionChanged);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onThemeChanged() {
//        Palette.Swatch swatch = new Swatch(Color.rgb());
//        List<Swatch> list = new ArrayList<>();
//        list.add(swatch);
//        Palette.from(list).generate(new PaletteAsyncListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onGenerated(Palette palette) {
//                Swatch vibrant = palette.getVibrantSwatch();
//                if (vibrant == null) {
//                    vibrant = palette.getDarkVibrantSwatch();
//                }
//                if (vibrant == null) return;
//
//                Window window = getWindow();
//                window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(vibrant.getRgb());
//                toolbar.setBackgroundColor(palette.getMutedColor(bgColor));
//            }
//        });

        recreate();
    }
}

package com.mistareader;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.util.TypedValue;

public class ThemesManager {
    public static int          CurrentTheme;

    public final static int    THEME_DEFAULT  = 0;
    public final static String SETTINGS_THEME = "THEME";

    private final static int   THEME_LIGHT    = 1;
    private final static int   THEME_GRAY     = 2;
    private final static int   THEME_BLACK    = 3;

    public static int          iconArrowUp;
    public static int          iconArrowDown;
    public static int          iconAccount;
    public static int          iconThemes;
    public static int          iconAbout;
    public static int          iconForum;
    public static int          iconReplies;
    public static int          iconNewItem;
    public static int          iconSend;
    public static int          iconSettings;

    public static int          colorBg_message_body;

    public static void changeTheme(Activity activity, int theme) {

        CurrentTheme = theme;

        activity.recreate();

    }

    public static String getCurrentThemeName(Activity activity) {
        switch (CurrentTheme) {
            case THEME_LIGHT:
                return activity.getString(R.string.sThemeLight);
            case THEME_BLACK:
                return activity.getString(R.string.sThemePureBlack);
        }
        return activity.getString(R.string.sThemeGray);
    }

    public static void onActivityCreateSetTheme(Activity activity) {

        if (CurrentTheme == THEME_DEFAULT) {
            CurrentTheme = THEME_GRAY;
        }

        switch (CurrentTheme) {
            case THEME_GRAY:
                activity.setTheme(R.style.Theme_Gray);
                break;
            case THEME_LIGHT:
                activity.setTheme(R.style.Theme_Light);
                break;
            case THEME_BLACK:
                activity.setTheme(R.style.Theme_Black);
                break;
            default:

        }

        initIcons(activity);
    }

    public static void initIcons(Activity activity) {

        TypedValue typedValue = new TypedValue();
        Theme theme = activity.getTheme();

        theme.resolveAttribute(R.attr.bg_message_body, typedValue, true);
        colorBg_message_body = typedValue.data;

        theme.resolveAttribute(R.attr.iconArrowUp, typedValue, true);
        iconArrowUp = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconArrowDown, typedValue, true);
        iconArrowDown = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconAccount, typedValue, true);
        iconAccount = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconThemes, typedValue, true);
        iconThemes = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconAbout, typedValue, true);
        iconAbout = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconForum, typedValue, true);
        iconForum = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconReplies, typedValue, true);
        iconReplies = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconNewItem, typedValue, true);
        iconNewItem = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconSend, typedValue, true);
        iconSend = typedValue.resourceId;

        theme.resolveAttribute(R.attr.iconSettings, typedValue, true);
        iconSettings = typedValue.resourceId;

    }
}

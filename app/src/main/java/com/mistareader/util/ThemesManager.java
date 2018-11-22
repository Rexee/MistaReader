package com.mistareader.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;

import com.mistareader.R;

import java.util.HashMap;

public class ThemesManager {
    private final static String THEME_LIGHT = "1";
    private final static String THEME_GRAY  = "2";
    private final static String THEME_BLACK = "3";

    private static final HashMap<String, Integer> THEMES = new HashMap<>();

    static {
        THEMES.put(THEME_LIGHT, R.style.AppTheme_Light);
        THEMES.put(THEME_GRAY, R.style.AppTheme_Gray);
        THEMES.put(THEME_BLACK, R.style.AppTheme_Black);
    }

    public static int iconNewItem;
    public static int iconSend;

    public static void onActivityCreateSetTheme(Activity activity) {
        String currentTheme = Settings.getCurrentTheme(activity);
        activity.setTheme(THEMES.containsKey(currentTheme) ? THEMES.get(currentTheme) : R.style.AppTheme_Gray);
    }

    public static int getAttributeReferenceResourceId(Context context, @AttrRes int attrResId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, outValue, true);
        return outValue.resourceId;
    }

    public static int getColorByAttr(Context context, @AttrRes int attrResId) {
        return getColorByAttr(context.getTheme(), new TypedValue(), attrResId);
        //        return ContextCompat.getColor(context, getAttributeReferenceResourceId(context, attrResId));
    }

    public static int getColorByAttr(Resources.Theme theme, TypedValue typedValue, @AttrRes int attrResId) {
        theme.resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
    }

    public static Drawable tintAttr(Context context, int drawableRes, @AttrRes int attr) {
        int tintColor = ThemesManager.getColorByAttr(context, attr);
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) {
            return null;
        }
        Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapped, tintColor);
        return drawable;
    }

    public static void tintAttr(Context context, Drawable drawable, @AttrRes int attr) {
        int tintColor = ThemesManager.getColorByAttr(context, attr);
        tint(drawable, tintColor);
    }

    public static void tint(Context context, Drawable drawable, @ColorRes int color) {
        int tintColor = ContextCompat.getColor(context, color);
        tint(drawable, tintColor);
    }

    public static void tint(Drawable drawable, @ColorInt int tintColor) {
        Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapped, tintColor);
    }

    @Nullable
    public static Drawable tint(@NonNull Context context, @DrawableRes int drawableRes, @ColorInt int tintColor) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) {
            return null;
        }
        Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapped, tintColor);
        return wrapped;
    }

    public static void tintMenu(int menuResId, Context context, MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(menuResId, menu);

        int color = getColorByAttr(context, R.attr.toolbarIconsTint);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) {
                continue;
            }
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, color);
        }
    }

}

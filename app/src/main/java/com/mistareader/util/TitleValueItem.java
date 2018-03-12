package com.mistareader.util;

import android.content.Context;

public class TitleValueItem {
    public String title;
    public int    value;
    public String valueStr;

    public TitleValueItem(Context context, int resId) {
        title = context.getString(resId);
        value = resId;
    }

    public TitleValueItem(String title, String valueStr) {
        this.title = title;
        this.valueStr = valueStr;
    }

    @Override
    public String toString() {
        return title;
    }
}

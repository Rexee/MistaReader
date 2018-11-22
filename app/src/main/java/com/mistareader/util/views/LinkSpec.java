package com.mistareader.util.views;

import android.text.style.URLSpan;

public class LinkSpec {
    public final URLSpan span;
    public final int     start, end;

    public LinkSpec(URLSpan urlSpan, int spanStart, int spanEnd) {
        span = urlSpan;
        start = spanStart;
        end = spanEnd;
    }
}

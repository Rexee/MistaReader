package com.mistareader.util;

import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;

public class S {
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static class ResultContainer {
        public boolean result;
        public String  resultStr;
        public String  userID;
        public String  resultSessionID;
        public String  cookie;
    }

    public static void L(Object object) {

        Log.d("mylog", "" + object);
    }

    public static void L(String string, Exception e) {
        Log.d("mylog", "" + string + " " + Log.getStackTraceString(e));
    }

    public static Spannable linkifyHTML(String text) {
        Spanned message = S.fromHtml(text);
        Spannable s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);

//        URLSpan[] old = s.getSpans(0, s.length(), URLSpan.class);
//        LinkSpec oldLinks[] = new LinkSpec[old.length];
//
//        for (int i = 0; i < old.length; i++) {
//            oldLinks[i] = new LinkSpec(old[i], s.getSpanStart(old[i]), s.getSpanEnd(old[i]));
//        }
//        for (LinkSpec span : oldLinks) {
//            s.setSpan(span.span, span.start, span.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        return s;
    }
}

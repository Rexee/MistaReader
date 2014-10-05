package com.mistareader.TextProcessors;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.util.Log;

public class StringProcessor {

    public static String mista_URL_EncodePlus(String str) {
        String ret = str;
        try {

            str.replace("+", "___plus___");

            ret = URLEncoder.encode(str, "windows-1251").replace("+", "%20");

        }
        catch (UnsupportedEncodingException e) {

            S.L("mista_URL_Encode: " + Log.getStackTraceString(e));

        }

        return ret;
    }

    public static String mista_URL_Encode(String str) {
        String ret = str;
        try {

            ret = URLEncoder.encode(str, "windows-1251").replace("+", "%20");

        }
        catch (UnsupportedEncodingException e) {

            S.L("mista_URL_Encode: " + Log.getStackTraceString(e));

        }

        return ret;
    }

    public static String unescapeSimple(String input) {

        final int MIN_ESCAPE = 2;
        final int MAX_ESCAPE = 6;

        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) {
            // look for '&'
            while (i < len && input.charAt(i - 1) != '&')
                i++;
            if (i >= len)
                break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
                j++;
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // named escape
            CharSequence value = lookupMap.get(input.substring(i, j));
            if (value == null) {
                i++;
                continue;
            }

            if (writer == null)
                writer = new StringWriter(input.length());
            writer.append(input.substring(st, i - 1));

            writer.append(value);

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;

    }

    private static final String[][]                    
ESCAPES = { { "\"", "quot" }, // " - double-quote
            { "&", "amp" }, // & - ampersand
            { "<", "lt" }, // < - less-than
            { ">", "gt" }, // > - greater-than
                                                               };

    private static final HashMap<String, CharSequence> lookupMap;
    static {
        lookupMap = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES)
            lookupMap.put(seq[1].toString(), seq[0]);
    }

}

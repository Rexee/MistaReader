package com.mistareader.util;

import android.util.Log;

import java.util.Collection;

public class S {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static boolean isEmpty(Collection mItems) {
        return mItems == null || mItems.isEmpty();
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



}

package com.mistareader.util;

import android.util.SparseArray;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Empty {

    public static boolean is(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is(Map map) {
        if (map == null || map.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is(String str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static boolean is(Number str) {
        if (str == null || str.equals(0))
            return true;
        else
            return false;
    }

    public static boolean is(Date date) {
        return date == null || date.getTime() == 0;
    }

    public static boolean is(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean is(SparseArray<Object> array) {
        return array == null || array.size() == 0;
    }
}

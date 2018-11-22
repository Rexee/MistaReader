package com.mistareader.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.DimenRes;
import android.util.DisplayMetrics;

public class SystemUtils {

    public static final String SKYPE_PACKAGE_ID = "com.skype.raider";


    public static int dpToPixel(Context context, int dp) {
        return dpToPixel(context.getResources(), dp);
    }

    public static int dpToPixel(Resources res, int dp) {
        return dpToPixel(res.getDisplayMetrics(), dp);
    }

    public static int dpToPixel(DisplayMetrics dm, int dp) {
        float px = (float) dp * (dm.densityDpi /  DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

    public static int getDimen(Context context, @DimenRes int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public static void callSkype(Activity activity, String skypeId) {
        if (Empty.is(skypeId)) {
            return;
        }

        // Make sure the Skype for Android client is installed.
        if (!isSkypeClientInstalled(activity)) {
            goToMarket(activity);
            return;
        }

        // Create the Intent from our Skype URI.
        Uri skypeUri = Uri.parse("skype:" + skypeId + "?call");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
        activity.startActivity(myIntent);
    }

    /**
     * Install the Skype client through the market: URI scheme.
     */
    private static void goToMarket(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=" + SKYPE_PACKAGE_ID);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);
    }

    /**
     * Determine whether the Skype for Android client is installed on this device.
     */
    private static boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo(SKYPE_PACKAGE_ID, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}

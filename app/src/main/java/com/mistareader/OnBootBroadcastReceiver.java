package com.mistareader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mistareader.util.Subscriptions;

public class OnBootBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Subscriptions.updateNotifications(context);

        }

    }
}

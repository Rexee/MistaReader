package com.mistareader;

import com.mistareader.TextProcessors.S;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReciever extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        S.L("Forum_BroadcastReciever.onReceive");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            
            S.L("Forum_BroadcastReciever.BOOT_COMPLETED");
            
            Subscriptions.refreshNotificationsShedule(context);
 
        }

    }
}

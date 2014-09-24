package com.mistareader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mistareader.TextProcessors.S;

public class BroadcastReciever extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        S.L("Forum_BroadcastReciever.onReceive");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            
            S.L("Forum_BroadcastReciever.BOOT_COMPLETED");
            
            Subscriptions_Service.refreshNotificationsShedule(context);
 
        }

    }
}

package com.mistareader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.mistareader.TextProcessors.S;

public class Forum_BroadcastReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {

        S.L("Forum_BroadcastReciever.onReceive");

//        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
//        int minutes = prefs.getInt(Settings.SETTINGS_BG_CHECK_PERIOD, 0);
                
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Forum_Notifications.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.cancel(pi);
       
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, 10 * 1000, pi);
//        if (minutes > 0) {
//            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes * 60 * 1000, minutes * 60 * 1000, pi);
//        }
        

//        ComponentName receiver = new ComponentName(this, Forum_BroadcastReciever.class);
//        PackageManager pm = getPackageManager();
//        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        
//        ComponentName receiver = new ComponentName(context, Forum_BroadcastReciever.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);

    }
}

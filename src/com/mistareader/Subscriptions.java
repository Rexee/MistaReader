package com.mistareader;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.mistareader.TextProcessors.JSONProcessor;

public class Subscriptions extends IntentService {

    DB                    mainDB;
    LocalBroadcastManager BMG;

    final static int      NOTIFICATION_INTERVAL_MULTIPLER = 1000 * 60;

    public Subscriptions() {
        super("Subscriptions");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        if (!WebIteraction.isInternetAvailable(this)) {
            stopSelf();
            return;
        }
        
        showNotification("sample text",0);
        showNotification("second text",0);

        mainDB = new DB(this);

        ArrayList<Topic> topicsList = mainDB.getSubscriptions();

        if (topicsList.size() <= 0) {
            mainDB.close();
            return;
        }

        BMG = LocalBroadcastManager.getInstance(this);

        for (int i = 0; i < topicsList.size(); i++) {
            Topic curTopic = topicsList.get(i);
            String URL = API.getTopicInfo(curTopic.id);

            String result = WebIteraction.getServerResponse(URL);

            Topic newSubscription = JSONProcessor.getTopicAnsw(result);
            if (newSubscription == null)
                continue;

            int newAnsw = newSubscription.answ - curTopic.answ;

            if (newAnsw > 0) {
                newSubscription.newAnsw = curTopic.newAnsw + newAnsw;
                newSubscription.id = curTopic.id;
                mainDB.updateTopicInSubscriptions(newSubscription);

                Intent intent = new Intent(Settings.SUBSCRIPTIONS_UPDATED_BROADCAST);
                BMG.sendBroadcast(intent);

                showNotification(curTopic.text, curTopic.id);
            }

        }

        mainDB.close();

    }

    private void showNotification(String text, long curTopicId) {

        int mId = 0;
//
//        Bundle inpBundle = new Bundle();
//        inpBundle.putLong("topicId", curTopicId);
//        
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
//        Uri geoUri = Uri.parse("geo:0,0?q=");
//        mapIntent.setData(geoUri);
//        PendingIntent mapPendingIntent =
//                PendingIntent.getActivity(this, 0, mapIntent, 0);
        
        Intent resultIntent = new Intent(this, Topics_Activity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Topics_Activity.class);
        stackBuilder.addNextIntent(resultIntent);
        resultIntent.putExtra("topicId", curTopicId);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.mr);
        mBuilder.setContentTitle(getText(R.string.sNewMessages));
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setContentText("text").setNumber(5);
        mBuilder.setAutoCancel(true);
//        mBuilder.addAction(R.drawable.ic_action_about, "title", mapPendingIntent);
        
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    // ******************************************************************************
    public static void updateNotifications(Context context) {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsUse = sPref.getBoolean(Settings.SUBSCRIPTIONS_USE, false);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Subscriptions.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.cancel(pi);

        if (notificationsUse) {

            enableOnBootHandler(context);

            String sNotificationsIntervalMinutes = sPref.getString(Settings.SUBSCRIPTIONS_INTERVAL, "0");
            int notificationsIntervalMinutes = Integer.parseInt(sNotificationsIntervalMinutes);
            long millis = notificationsIntervalMinutes * NOTIFICATION_INTERVAL_MULTIPLER;

            if (notificationsIntervalMinutes > 0) {
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + millis, millis, pi);
            }

        }
        else
            disableOnBootHandler(context);

    }

    public static void enableOnBootHandler(Context context) {
        ComponentName receiver = new ComponentName(context, OnBootBroadcastReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableOnBootHandler(Context context) {
        ComponentName receiver = new ComponentName(context, OnBootBroadcastReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

}

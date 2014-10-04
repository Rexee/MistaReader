package com.mistareader;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.S;

public class Subscriptions extends IntentService {

    DB                    mainDB;
    LocalBroadcastManager BMG;

    final static int      NOTIFICATION_INTERVAL_MULTIPLER = 1000 * 60;

    public Subscriptions() {
        super("Subscriptions");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        S.L("SERV");

        if (!WebIteraction.isInternetAvailable(this)) {
            stopSelf();
            return;
        }

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
            }

        }

        mainDB.close();

        // Notification notification = new Notification(R.drawable.ic_action_expand, getText(R.string.sSubscriptions), System.currentTimeMillis());
        // Intent notificationIntent = new Intent(this, ExampleActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // notification.setLatestEventInfo(this, getText(R.string.notification_title), getText(R.string.notification_message), pendingIntent);
        // startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    // ******************************************************************************
    public static void refreshNotificationsShedule(Context context) {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsUse = sPref.getBoolean(Settings.SUBSCRIPTIONS_USE, false);

        if (notificationsUse) {

            enableOnBootHandler(context);

            Intent checkIntent = new Intent(context, Subscriptions.class);
            PendingIntent pi = PendingIntent.getService(context, 0, checkIntent, PendingIntent.FLAG_NO_CREATE);
            if (pi == null) {

                String sNotificationsIntervalMinutes = sPref.getString(Settings.SUBSCRIPTIONS_INTERVAL, "0");
                int notificationsIntervalMinutes = Integer.parseInt(sNotificationsIntervalMinutes);
                long millis = notificationsIntervalMinutes * NOTIFICATION_INTERVAL_MULTIPLER;

                if (notificationsIntervalMinutes > 0) {
                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context, Subscriptions.class);
                    pi = PendingIntent.getService(context, 0, i, 0);

                    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + millis, millis, pi);
                }

            }

        }
        else {
            disableOnBootHandler(context);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, Subscriptions.class);
            PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            am.cancel(pi);
        }

    }

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

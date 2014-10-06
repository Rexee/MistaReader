package com.mistareader;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.StringProcessor;

public class Subscriptions extends IntentService {

    DB                         mainDB;
    LocalBroadcastManager      BMG;
    final static int           NOTIFICATION_INTERVAL_MULTIPLER = 1000 * 60;

    static final int           NOTIFICATIONS_UNIQUE_ID         = 0;
    static final String        NOTIFICATIONS_EXTRA_ID          = "NOTIFICATIONS_EXTRA_ID";
    static final String        NOTIFICATIONS_EXTRA_IS_MULTIPLE = "IS_MULTIPLE";
    static final String        NOTIFICATIONS_EXTRA_TOPIC_ID    = "TOPIC_ID";
    static final String        NOTIFICATIONS_EXTRA_TOPIC_ANSW  = "TOPIC_ANSW";
    public static final String NOTIFICATIONS_EXTRA_RELOAD_MODE = "RELOAD_MODE";

    private class cNewSubscriptions {
        long       topicId;
        String     text;
        public int newAnsw;
        public int answ;
    }

    public Subscriptions() {
        super("Subscriptions");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        if (!WebIteraction.isInternetAvailable(this)) {
            return;
        }

        mainDB = new DB(this);

        ArrayList<Topic> topicsList = mainDB.getSubscriptions();

        if (topicsList.size() <= 0) {
            mainDB.close();
            return;
        }

        BMG = LocalBroadcastManager.getInstance(this);
        boolean isNewSubs = false;

        for (int i = 0; i < topicsList.size(); i++) {
            Topic curTopic = topicsList.get(i);
            String URL = API.getTopicInfo(curTopic.id);

            String result = WebIteraction.getServerResponse(URL);

            Topic newSubscription = JSONProcessor.getTopicAnsw(result);
            if (newSubscription == null)
                continue;

            int newAnsw = newSubscription.answ - curTopic.answ;

            if (newAnsw > 0) {

                isNewSubs = true;

                curTopic.newAnsw = curTopic.newAnsw + newAnsw;
                newSubscription.newAnsw = curTopic.newAnsw;
                newSubscription.id = curTopic.id;
                mainDB.updateTopicInSubscriptions(newSubscription);

                Intent intent = new Intent(Settings.SUBSCRIPTIONS_UPDATED_BROADCAST);
                BMG.sendBroadcast(intent);

            }

        }

        mainDB.close();

        if (workIntent.hasExtra(NOTIFICATIONS_EXTRA_RELOAD_MODE)) {
            return;
        }

        if (isNewSubs) {

            ArrayList<cNewSubscriptions> newSubsList = new ArrayList<cNewSubscriptions>();

            for (int i = 0; i < topicsList.size(); i++) {
                Topic curTopic = topicsList.get(i);
                if (curTopic.newAnsw == 0) {
                    continue;
                }

                cNewSubscriptions newSub = new cNewSubscriptions();

                newSub.topicId = curTopic.id;
                newSub.text = curTopic.text;
                newSub.answ = curTopic.answ;
                newSub.newAnsw = curTopic.newAnsw;

                newSubsList.add(newSub);
            }

            showNotification(newSubsList);
        }

    }

    private void showNotification(ArrayList<cNewSubscriptions> newSubsList) {

        if (newSubsList.isEmpty()) {
            return;
        }

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationsUse = sPref.getBoolean(Settings.NOTIFICATIONS_USE, false);
        if (!notificationsUse) {
            return;
        }

        boolean notificationsVibrate = sPref.getBoolean(Settings.NOTIFICATIONS_VIBRATE, false);
        boolean notificationsSound = sPref.getBoolean(Settings.NOTIFICATIONS_SOUND, false);
        boolean notificationsLED = sPref.getBoolean(Settings.NOTIFICATIONS_LED, false);

        Intent resultIntent = new Intent(this, Topics_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Topics_Activity.class);
        stackBuilder.addNextIntent(resultIntent);

        resultIntent.putExtra(NOTIFICATIONS_EXTRA_ID, true);

        boolean newSubsMultiple = true;
        cNewSubscriptions newSub = null;

        if (newSubsList.size() == 1) {
            newSub = newSubsList.get(0);
            resultIntent.putExtra(NOTIFICATIONS_EXTRA_TOPIC_ID, newSub.topicId);
            resultIntent.putExtra(NOTIFICATIONS_EXTRA_TOPIC_ANSW, newSub.answ);
            newSubsMultiple = false;
        }

        resultIntent.putExtra(NOTIFICATIONS_EXTRA_IS_MULTIPLE, newSubsMultiple);
        String sNewMessages = getString(R.string.sNewMessages);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        // mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        int defaults = 0;
        if (notificationsVibrate) {
            defaults = defaults | NotificationCompat.DEFAULT_VIBRATE;
        }
        if (notificationsSound) {
            defaults = defaults | NotificationCompat.DEFAULT_SOUND;
        }
        if (notificationsLED) {
            defaults = defaults | NotificationCompat.DEFAULT_LIGHTS;
        }

        mBuilder.setDefaults(defaults);
        mBuilder.setSmallIcon(R.drawable.ic_mr_bw);
        mBuilder.setLargeIcon((((BitmapDrawable) getResources().getDrawable(R.drawable.mr)).getBitmap()));

        mBuilder.setTicker(getString(R.string.sSubscriptionsNew));

        if (!newSubsMultiple) {
            mBuilder.setContentTitle(sNewMessages);
            mBuilder.setNumber(newSub.newAnsw);

            String unescapedText = StringProcessor.unescapeSimple(newSub.text);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(unescapedText));
            mBuilder.setContentText(unescapedText);

        }
        else {
            mBuilder.setContentTitle(sNewMessages);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(sNewMessages);
            inboxStyle.setSummaryText(getString(R.string.sTotalNew));

            int totalNewMess = 0;
            for (int i = 0; i < newSubsList.size(); i++) {
                newSub = newSubsList.get(i);
                totalNewMess = totalNewMess + newSub.newAnsw;

                String unescapedText = StringProcessor.unescapeSimple(newSub.text);

                inboxStyle.addLine("* " + unescapedText + ": " + newSub.newAnsw);
                if (i == 0)
                    mBuilder.setContentText(unescapedText);
            }
            mBuilder.setNumber(totalNewMess);
            mBuilder.setStyle(inboxStyle);
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATIONS_UNIQUE_ID, mBuilder.build());

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

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
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.S;

public class Subscriptions extends IntentService {

    DB                    mainDB;
    LocalBroadcastManager BMG;

    public Subscriptions() {
        super("Subscriptions");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        if (!WebIteraction.isInternetAvailable(this)) {
            stopSelf();
            return;
        }

        mainDB = new DB(this);

        // mainDB.L("handleIntent1");

        ArrayList<Topic> topicsList = mainDB.getSubscriptions();

        if (topicsList.size() <= 0) {
            mainDB.close();
            return;
        }

        // mainDB.L("handleIntent2");

        Handler handler = new Handler();

        BMG = LocalBroadcastManager.getInstance(this);

        for (int i = 0; i < topicsList.size(); i++) {
            Topic curTopic = topicsList.get(i);
            String URL = API.getTopicInfo(curTopic.id);

            String result= WebIteraction.getServerResponse(URL);
            
          Topic newSubscription = JSONProcessor.getTopicAnsw(result);
          int newAnsw = newSubscription.answ - curTopic.answ;

          if (newAnsw > 0) {
              newSubscription.newAnsw = curTopic.newAnsw + newAnsw;
              newSubscription.id = curTopic.id;
              mainDB.updateTopicInSubscriptions(newSubscription);

              // mainDB.L("sendBroadcast");

              Intent intent = new Intent(Settings.SUBSCRIPTIONS_UPDATED_BROADCAST);
              BMG.sendBroadcast(intent);
          }

          mainDB.close();

        }

        // Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text), System.currentTimeMillis());
        // Intent notificationIntent = new Intent(this, ExampleActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // notification.setLatestEventInfo(this, getText(R.string.notification_title), getText(R.string.notification_message), pendingIntent);
        // startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    // // private WakeLock mWakeLock;
    // // private final static String WL_TAG = "mista_wakelog";
    //
    // @Override
    // public IBinder onBind(Intent intent) {
    // return null;
    // }
    //
    // private void handleIntent(Intent intent) {
    // // PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    // // mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WL_TAG);
    // // mWakeLock.acquire();
    //

    // }
    //
    // public class requestAsyncDelayed implements Runnable {
    // private String mURL;
    // private int mAnsw;
    // private int mNewAnsw;
    // private long mtopicId;
    //
    // public requestAsyncDelayed(String _URL, long _topicId, int _answ, int newAnsw) {
    // this.mURL = _URL;
    // this.mAnsw = _answ;
    // this.mNewAnsw = newAnsw;
    // this.mtopicId = _topicId;
    // }
    //
    // public void run() {
    // new requestAsyncGetTopicsData(mAnsw, mtopicId, mNewAnsw).execute(mURL);
    // }
    // }
    //
    // private class requestAsyncGetTopicsData extends AsyncTask<String, Void, String> {
    //
    // int mPrevTopicAnsw;
    // int mNewAnsw;
    // long mtopicId;
    //
    // public requestAsyncGetTopicsData(int answ, long _topicId, int newAnsw) {
    // mPrevTopicAnsw = answ;
    // mNewAnsw = newAnsw;
    // mtopicId = _topicId;
    // }
    //
    // @Override
    // protected String doInBackground(String... params) {
    // // S.L("Service doInBackground ");
    // return WebIteraction.getServerResponse(params[0]);
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    //
    // Topic newSubscription = JSONProcessor.getTopicAnsw(result);
    // int lNewAnsw = newSubscription.answ - mPrevTopicAnsw;
    //
    // if (lNewAnsw > 0) {
    // newSubscription.newAnsw = mNewAnsw + lNewAnsw;
    // newSubscription.id = mtopicId;
    // mainDB.updateTopicInSubscriptions(newSubscription);
    //
    // // mainDB.L("sendBroadcast");
    //
    // Intent intent = new Intent(Settings.SUBSCRIPTIONS_UPDATED_BROADCAST);
    // BMG.sendBroadcast(intent);
    // }
    //
    // mainDB.close();
    //
    // S.L("STOP SELF");
    // stopSelf();
    // }
    // }
    //
    // @Override
    // public void onStart(Intent intent, int startId) {
    // // S.L("onStart");
    // handleIntent(intent);
    // }
    //
    // @Override
    // public int onStartCommand(Intent intent, int flags, int startId) {
    //
    // // mainDB = new DB(this);
    //
    // // mainDB.L("onStartCommand");
    //
    // handleIntent(intent);
    // return START_NOT_STICKY;
    // }
    //
    // public void onDestroy() {
    // super.onDestroy();
    // // mWakeLock.release();
    // }

    // ******************************************************************************
    public static void refreshNotificationsShedule(Context context) {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsUse = sPref.getBoolean(Settings.SUBSCRIPTIONS_USE, false);

        // S.L("notificationsUse " + notificationsUse);

        if (notificationsUse) {

            enableOnBootHandler(context);

            Intent checkIntent = new Intent(context, Subscriptions.class);
            PendingIntent pi = PendingIntent.getService(context, 0, checkIntent, PendingIntent.FLAG_NO_CREATE);
            if (pi == null) {

                // S.L("Set up subs!");

                String sNotificationsIntervalMinutes = sPref.getString(Settings.SUBSCRIPTIONS_INTERVAL, "0");
                int notificationsIntervalMinutes = Integer.parseInt(sNotificationsIntervalMinutes);

                S.L("notificationsIntervalMinutes " + notificationsIntervalMinutes);
                // long millis = notificationsIntervalMinutes * 60 * 1000;
                long millis = notificationsIntervalMinutes * 1000;

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

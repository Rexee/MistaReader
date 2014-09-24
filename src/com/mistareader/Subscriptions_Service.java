package com.mistareader;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.S;

public class Subscriptions_Service extends Service {

    public static class subscription {
        long topicId;
        int  answ;
        int  newMessages;
    }

    DB mainDB;
    
    // private WakeLock mWakeLock;
    // private final static String WL_TAG = "mista_wakelog";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {
        // PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        // mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WL_TAG);
        // mWakeLock.acquire();

        if (!WebIteraction.isInternetAvailable(this)) {
            stopSelf();
            return;
        }

        mainDB = new DB(this);
        mainDB.printAllSubscriptions();
        
        ArrayList<subscription> topicsList = mainDB.getSubscriptions();

        if (topicsList.size() <= 0) {
            return;
        }

        Handler handler = new Handler();

        for (int i = 0; i < topicsList.size(); i++) {
            subscription locTopic = topicsList.get(i);
            String URL = API.getTopicInfo(locTopic.topicId);

            requestAsyncDelayed req = new requestAsyncDelayed(URL, locTopic.topicId, locTopic.answ);
            handler.postDelayed(req, i*100);

        }

        // Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text), System.currentTimeMillis());
        // Intent notificationIntent = new Intent(this, ExampleActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // notification.setLatestEventInfo(this, getText(R.string.notification_title), getText(R.string.notification_message), pendingIntent);
        // startForeground(ONGOING_NOTIFICATION_ID, notification);
    }
    
    public class requestAsyncDelayed implements Runnable {
        private String mURL;
        private int mAnsw;
        private long mtopicId;
        
        public requestAsyncDelayed(String _URL, long _topicId, int _answ) {
            this.mURL = _URL;
            this.mAnsw = _answ;
            this.mtopicId = _topicId;
        }

        public void run() {
            new requestAsyncGetTopicsData(mAnsw, mtopicId).execute(mURL);
        }
      }


    private class requestAsyncGetTopicsData extends AsyncTask<String, Void, String> {

        int mPrevTopicAnsw;
        long mtopicId;

        public requestAsyncGetTopicsData(int answ, long _topicId) {
            mPrevTopicAnsw = answ;
            mtopicId = _topicId;
        }

        @Override
        protected String doInBackground(String... params) {
//            S.L("Service doInBackground ");
            return WebIteraction.getServerResponse(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            int answ = JSONProcessor.getTopicAnsw(result);
            int newMess = answ - mPrevTopicAnsw;
            if (newMess > 0) {
                mainDB.updateTopicInSubscriptions(mtopicId, newMess);
            }
            stopSelf();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        S.L("onStartCommand");
        
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        // mWakeLock.release();
    }

    // ******************************************************************************
    public static void refreshNotificationsShedule(Context context) {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsUse = sPref.getBoolean(Settings.SUBSCRIPTIONS_USE, false);

        S.L("notificationsUse " + notificationsUse);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Subscriptions_Service.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.cancel(pi);

        if (notificationsUse) {

            enableOnBootHandler(context);

            String sNotificationsIntervalMinutes = sPref.getString(Settings.SUBSCRIPTIONS_INTERVAL, "0");
            int notificationsIntervalMinutes = Integer.parseInt(sNotificationsIntervalMinutes);

            S.L("notificationsIntervalMinutes " + notificationsIntervalMinutes);
            // long millis = notificationsIntervalMinutes * 60 * 1000;
            long millis = notificationsIntervalMinutes * 1000;

            if (notificationsIntervalMinutes > 0) {
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + millis, millis, pi);
            }

        }
        else
            disableOnBootHandler(context);

    }

    public static void enableOnBootHandler(Context context) {
        ComponentName receiver = new ComponentName(context, BroadcastReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableOnBootHandler(Context context) {
        ComponentName receiver = new ComponentName(context, BroadcastReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

}

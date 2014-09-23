package com.mistareader;

import com.mistareader.TextProcessors.S;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;

public class Forum_Notifications extends Service {

//    private WakeLock            mWakeLock;
//    private final static String WL_TAG = "mista_wakelog";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WL_TAG);
//        mWakeLock.acquire();

        S.L("Service handleIntent1");
        
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        S.L("Service handleIntent2");
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        S.L("Service handleIntent3");
        
        if (!isConnected) {
            stopSelf();
            return;
        }

        new requestAsyncGetTopicsData().execute();
    }

    private class requestAsyncGetTopicsData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            S.L("Service doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            stopSelf();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
//        mWakeLock.release();
    }
}

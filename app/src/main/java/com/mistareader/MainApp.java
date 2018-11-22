package com.mistareader;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.fabric.sdk.android.Fabric;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build());

        initImageLoader();
    }

    private void initImageLoader() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .defaultDisplayImageOptions(displayImageOptions)
                //.writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }
}

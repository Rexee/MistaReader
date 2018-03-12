package com.mistareader;

import android.app.Application;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.concurrent.Executor;

public class MainApp extends Application {
    private static Executor executor;

    @Override
    public void onCreate() {
        super.onCreate();

        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
//        Set<RequestListener> requestListeners = new HashSet<>();
//        requestListeners.add(new RequestLoggingListener());
//        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
//                .setRequestListeners(requestListeners)
//                .build();
        Fresco.initialize(this);

//        com.mistareader.util.views.SimpleDraweeView.initialize(Fresco.getDraweeControllerBuilderSupplier());
//        com.mistareader.util.views.SimpleDraweeView.initialize(Fresco.getDraweeControllerBuilderSupplier());



//        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_person)
//                .showImageForEmptyUri(R.drawable.ic_person)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration
//                .Builder(this)
//                .defaultDisplayImageOptions(displayImageOptions)
//                //.writeDebugLogs()
//                .build();
//
//        ImageLoader.getInstance().init(config);


    }
}

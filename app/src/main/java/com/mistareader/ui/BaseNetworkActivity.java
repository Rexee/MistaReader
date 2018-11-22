package com.mistareader.ui;

import android.os.Bundle;

import com.mistareader.api.NetProvider;

public abstract class BaseNetworkActivity extends BaseActivity {
    protected NetProvider netProvider;

    public NetProvider getNetProvider() {
        return netProvider;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netProvider = new NetProvider(this);
    }

    @Override protected void onDestroy() {
        if (netProvider != null) {
            netProvider.release();
        }
        super.onDestroy();
    }
}

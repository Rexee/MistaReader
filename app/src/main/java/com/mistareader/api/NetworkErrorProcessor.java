package com.mistareader.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mistareader.util.Empty;

import java.io.IOException;

import retrofit2.Call;

public class NetworkErrorProcessor {
    private Context mContext;

    public NetworkErrorProcessor(Context context) {
        mContext = context;
    }

    public void onError(Call call, Throwable t, String error) {
        if (!Empty.is(error)) {
            Log.e("DBG", "onError: " + error);
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
        } else {
            if (t != null) {
                t.printStackTrace();
                if (t instanceof IOException) {

                } else {

                }
                Log.d("DBG", "onFailure: " + t);

                Toast.makeText(mContext, "Net error: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void release() {
        mContext = null;
    }
}

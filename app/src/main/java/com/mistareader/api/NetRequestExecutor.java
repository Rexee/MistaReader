package com.mistareader.api;

import android.os.AsyncTask;

import com.mistareader.api.WebIteraction.RequestResult;
import com.mistareader.model.Forum;
import com.mistareader.util.S;

public class NetRequestExecutor extends AsyncTask<String, Integer, RequestResult> {
    public interface Callback<T> {
        void onProcess(RequestResult result);

        void onResult(RequestResult<T> result);
    }

    private String   mSessionCookies;
    private Callback mCallback;

    public NetRequestExecutor() {
        mSessionCookies = Forum.getInstance().sessionCookies;
    }

    public void execute(String request, Callback callback) {
        mCallback = callback;
        execute(request);
    }

    protected RequestResult doInBackground(String... params) {
        RequestResult result = WebIteraction.doServerRequest(params[0], mSessionCookies);
        mCallback.onProcess(result);
        return result;
    }

    protected void onPostExecute(RequestResult result) {
        if (!S.isEmpty(result.cookie)) {
            mSessionCookies = result.cookie;
        }
        mCallback.onResult(result);
    }
}
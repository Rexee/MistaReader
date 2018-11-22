package com.mistareader.api;

import android.content.Context;

import com.mistareader.model.BaseResponse;
import com.mistareader.util.Empty;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicApiCallback<T> implements Callback<T> {
    private NetworkErrorProcessor mErrorProcessor;
    private NetResponse<T>        mNetResponse;

    protected BasicApiCallback(NetResponse<T> netResponse, Context context) {
        mNetResponse = netResponse;
        mErrorProcessor = new NetworkErrorProcessor(context);
    }

    @Override public void onResponse(Call<T> call, Response<T> response) {
        if (mNetResponse == null) {
            return;
        }
        if (response.isSuccessful()) {
            T result = response.body();
            if (result == null) {
                mErrorProcessor.onError(call, null, "Пустой ответ сервера");
                return;
            }
            if (result instanceof BaseResponse) {
                if (!Empty.is(((BaseResponse) result).error)) {
                    mErrorProcessor.onError(call, null, ((BaseResponse) result).error);
                    return;
                }
            }
            mNetResponse.onResult(result);
        } else {
            mErrorProcessor.onError(call, null, "Сетевая ошибка: " + response.code());
        }
    }

    @Override public void onFailure(Call<T> call, Throwable t) {
        if (mErrorProcessor != null) {
            mErrorProcessor.onError(call, t, "");
        }
    }

    void release() {
        mNetResponse = null;
        mErrorProcessor.release();
        mErrorProcessor = null;
    }
}

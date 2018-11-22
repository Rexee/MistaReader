package com.mistareader.api;

public interface NetResponse<T> {
    void onResult(T result);
}

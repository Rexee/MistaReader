package com.mistareader.json;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class LoganSquareResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final LoganSquareTypesReader reader;

    LoganSquareResponseBodyConverter(LoganSquareTypesReader reader) {
        this.reader = reader;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return reader.readValue(value.byteStream());
        } finally {
            value.close();
        }
    }
}
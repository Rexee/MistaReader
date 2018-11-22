package com.mistareader.json;


import com.bluelinelabs.logansquare.ConverterUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static com.mistareader.json.LoganSquareTypesReader.TYPE_CLASS;
import static com.mistareader.json.LoganSquareTypesReader.TYPE_GENERIC;
import static com.mistareader.json.LoganSquareTypesReader.TYPE_LIST;
import static com.mistareader.json.LoganSquareTypesReader.TYPE_MAP;

public class LoganSquareConverterFactory extends Converter.Factory {

    public static LoganSquareConverterFactory create() {
        return new LoganSquareConverterFactory();
    }

    private LoganSquareConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (!ConverterUtils.isSupported(type)) {
            return null;
        }
        LoganSquareTypesReader reader = new LoganSquareTypesReader();

        if (type instanceof Class) {
            reader.setType(TYPE_CLASS, (Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            Type rawType = parameterizedType.getRawType();
            if (rawType == Map.class) {
                reader.setType(TYPE_MAP, (Class<?>) typeArguments[1]);
            } else if (rawType == List.class) {
                reader.setType(TYPE_LIST, (Class<?>) typeArguments[0]);
            } else {
                reader.setParameterizedType(TYPE_GENERIC, ConverterUtils.parameterizedTypeOf(type));
            }
        }

        return new LoganSquareResponseBodyConverter<>(reader);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (ConverterUtils.isSupported(type)) {
            return new LoganSquareRequestBodyConverter(type);
        } else {
            return null;
        }
    }
}
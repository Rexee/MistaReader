package com.mistareader.json;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;

public class LoganSquareTypesReader {
    static final int TYPE_CLASS   = 1;
    static final int TYPE_MAP     = 2;
    static final int TYPE_LIST    = 3;
    static final int TYPE_GENERIC = 4;

    private int                                            type;
    private Class<?>                                       aClass;
    private com.bluelinelabs.logansquare.ParameterizedType parameterizedType;

    @SuppressWarnings("unchecked")
    public <T> T readValue(InputStream inputStream) throws IOException {
        switch (type) {
            case TYPE_CLASS:
                return (T) LoganSquare.parse(inputStream, aClass);
            case TYPE_MAP:
                return (T) LoganSquare.parseMap(inputStream, aClass);
            case TYPE_LIST:
                return (T) LoganSquare.parseList(inputStream, aClass);
            case TYPE_GENERIC:
                return (T) LoganSquare.parse(inputStream, parameterizedType);
        }

        return null;
    }

    public void setType(int type, Class<?> aClass) {
        this.type = type;
        this.aClass = aClass;
    }

    public void setParameterizedType(int type, com.bluelinelabs.logansquare.ParameterizedType parameterizedType) {
        this.type = type;
        this.parameterizedType = parameterizedType;
    }
}

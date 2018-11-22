package com.mistareader.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Login extends BaseResponse{
    public String userid;
    public String username;
    public String hashkey;
}

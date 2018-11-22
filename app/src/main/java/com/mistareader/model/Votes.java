package com.mistareader.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Votes {
    @JsonField(name = "select")
    public String voteName;
    @JsonField(name = "result")
    public int    voteCount;
}

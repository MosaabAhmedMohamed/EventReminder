package com.example.eventreminder.refactoring.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SysEntity {
    @Expose
    @SerializedName("pod")
    private String pod;

    public String getPod() {
        return pod;
    }
}

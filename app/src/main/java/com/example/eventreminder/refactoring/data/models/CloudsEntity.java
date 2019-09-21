package com.example.eventreminder.refactoring.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CloudsEntity {
    @Expose
    @SerializedName("all")
    private int all;

    public int getAll() {
        return all;
    }
}

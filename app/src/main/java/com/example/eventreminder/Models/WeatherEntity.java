package com.example.eventreminder.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherEntity {
    @Expose
    @SerializedName("icon")
    private String icon;
    @Expose
    @SerializedName("description")
    private String description;
    @Expose
    @SerializedName("main")
    private String main;
    @Expose
    @SerializedName("id")
    private int id;

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getMain() {
        return main;
    }

    public int getId() {
        return id;
    }
}

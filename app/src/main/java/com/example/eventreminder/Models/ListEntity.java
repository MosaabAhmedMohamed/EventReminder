package com.example.eventreminder.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListEntity {
    @Expose
    @SerializedName("dt_txt")
    private String dt_txt;
    @Expose
    @SerializedName("sys")
    private SysEntity sys;
    @Expose
    @SerializedName("wind")
    private WindEntity wind;
    @Expose
    @SerializedName("clouds")
    private CloudsEntity clouds;
    @Expose
    @SerializedName("weather")
    private List<WeatherEntity> weather;
    @Expose
    @SerializedName("main")
    private MainEntity main;
    @Expose
    @SerializedName("dt")
    private int dt;

    public String getDt_txt() {
        return dt_txt;
    }

    public SysEntity getSys() {
        return sys;
    }

    public WindEntity getWind() {
        return wind;
    }

    public CloudsEntity getClouds() {
        return clouds;
    }

    public List<WeatherEntity> getWeather() {
        return weather;
    }

    public MainEntity getMain() {
        return main;
    }

    public int getDt() {
        return dt;
    }
}

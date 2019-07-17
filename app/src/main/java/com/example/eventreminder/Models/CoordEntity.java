package com.example.eventreminder.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordEntity {
    @Expose
    @SerializedName("lon")
    private double lon;
    @Expose
    @SerializedName("lat")
    private double lat;

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}

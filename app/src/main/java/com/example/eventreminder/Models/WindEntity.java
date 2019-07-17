package com.example.eventreminder.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WindEntity {
    @Expose
    @SerializedName("deg")
    private double deg;
    @Expose
    @SerializedName("speed")
    private double speed;

    public double getDeg() {
        return deg;
    }

    public double getSpeed() {
        return speed;
    }
}

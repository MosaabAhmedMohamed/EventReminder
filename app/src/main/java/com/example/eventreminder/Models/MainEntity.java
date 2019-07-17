package com.example.eventreminder.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainEntity {
    @Expose
    @SerializedName("temp_kf")
    private double temp_kf;
    @Expose
    @SerializedName("humidity")
    private int humidity;
    @Expose
    @SerializedName("grnd_level")
    private double grnd_level;
    @Expose
    @SerializedName("sea_level")
    private double sea_level;
    @Expose
    @SerializedName("pressure")
    private double pressure;
    @Expose
    @SerializedName("temp_max")
    private double temp_max;
    @Expose
    @SerializedName("temp_min")
    private double temp_min;
    @Expose
    @SerializedName("temp")
    private double temp;

    public double getTemp_kf() {
        return temp_kf;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getGrnd_level() {
        return grnd_level;
    }

    public double getSea_level() {
        return sea_level;
    }

    public double getPressure() {
        return pressure;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp() {
        return temp;
    }
}

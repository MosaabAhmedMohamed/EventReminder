package com.example.eventreminder.refactoring.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityEntity {
    @Expose
    @SerializedName("timezone")
    private int timezone;
    @Expose
    @SerializedName("population")
    private int population;
    @Expose
    @SerializedName("country")
    private String country;
    @Expose
    @SerializedName("coord")
    private CoordEntity coord;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("id")
    private int id;

    public int getTimezone() {
        return timezone;
    }

    public int getPopulation() {
        return population;
    }

    public String getCountry() {
        return country;
    }

    public CoordEntity getCoord() {
        return coord;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}

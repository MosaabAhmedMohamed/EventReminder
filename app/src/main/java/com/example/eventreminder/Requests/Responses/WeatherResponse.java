package com.example.eventreminder.Requests.Responses;

import com.example.eventreminder.Models.CityEntity;
import com.example.eventreminder.Models.ListEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {


    @Expose
    @SerializedName("city")
    private CityEntity city;
    @Expose
    @SerializedName("list")
    private List<ListEntity> list;
    @Expose
    @SerializedName("cnt")
    private int cnt;
    @Expose
    @SerializedName("message")
    private double message;
    @Expose
    @SerializedName("cod")
    private String cod;

    public CityEntity getCity() {
        return city;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public int getCnt() {
        return cnt;
    }

    public double getMessage() {
        return message;
    }

    public String getCod() {
        return cod;
    }
}

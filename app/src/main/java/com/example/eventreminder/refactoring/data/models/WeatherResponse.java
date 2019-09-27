package com.example.eventreminder.refactoring.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    private int id;
    private String networkMessage;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNetworkMessage() {
        return networkMessage;
    }

    public void setNetworkMessage(String networkMessage) {
        this.networkMessage = networkMessage;
    }
}

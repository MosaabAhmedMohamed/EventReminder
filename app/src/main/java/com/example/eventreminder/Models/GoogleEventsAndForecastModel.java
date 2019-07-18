package com.example.eventreminder.Models;

import com.google.api.services.calendar.model.Event;

import java.util.HashMap;
import java.util.List;

public class GoogleEventsAndForecastModel {

    private HashMap<String,ListEntity> forecastModelsMap;

    private List<Event> eventsModels;


    public HashMap<String, ListEntity> getForecastModels() {
        return forecastModelsMap;
    }

    public void setForecastModels(HashMap<String, ListEntity> forecastModels) {
        this.forecastModelsMap = forecastModels;
    }

    public List<Event> getEventsModels() {
        return eventsModels;
    }

    public void setEventsModels(List<Event> eventsModels) {
        this.eventsModels = eventsModels;
    }
}

package com.example.eventreminder.refactoring.data.models;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoogleEventsAndForecastModel {

    private HashMap<Integer, ListEntity> forecastModelsMap;

    private List<Event> eventsModels;
    private ArrayList<EventDateTimeModel> eventDateTimeModels;

    public GoogleEventsAndForecastModel() {
        eventDateTimeModels = new ArrayList<>();
    }

    public HashMap<Integer, ListEntity> getForecastModels() {
        return forecastModelsMap;
    }

    public void setForecastModels(HashMap<Integer, ListEntity> forecastModels) {
        this.forecastModelsMap = forecastModels;
    }

    public List<Event> getEventsModels() {
        return eventsModels;
    }

    public void setEventsModels(List<Event> eventsModels) {
        this.eventsModels = eventsModels;
    }

    public ArrayList<EventDateTimeModel> getEventDateTimeModels() {
        return eventDateTimeModels;
    }

}

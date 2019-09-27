package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils;

import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
public interface OnEventActionLIstner {

    void onDeleteEvent(String id);
    void onAcceptEvent(int position);
    void onEventOverlapped(EventDateTimeModel firstEvent, EventDateTimeModel secondEvent, int position);
}

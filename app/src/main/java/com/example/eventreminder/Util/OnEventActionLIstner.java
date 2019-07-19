package com.example.eventreminder.Util;

import com.example.eventreminder.Models.EventDateTimeModel;
public interface OnEventActionLIstner {

    void onDeleteEvent(String id);
    void onAcceptEvent(int position);
    void onEventOverlapped(EventDateTimeModel firstEvent, EventDateTimeModel secondEvent, int position);
}

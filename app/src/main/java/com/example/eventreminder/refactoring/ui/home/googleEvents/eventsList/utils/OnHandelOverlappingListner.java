package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils;

import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;

public interface OnHandelOverlappingListner {

    void onHandel(boolean status, EventDateTimeModel selectedEventToReschedule, int positionOfEventInList,int eventSelectedFromDialog);
}

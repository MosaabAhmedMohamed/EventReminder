package com.example.eventreminder.Util;

import com.example.eventreminder.Models.EventDateTimeModel;

public interface OnHandelOverlappingListner {

    void onHandel(boolean status, EventDateTimeModel selectedEventToReschedule, int position);
}

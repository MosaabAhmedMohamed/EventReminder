package com.example.eventreminder.refactoring.data.models;

import com.google.api.services.calendar.model.EventAttendee;

import java.util.List;

public class AcceptEventCheckModel {

    private boolean acceptedBefor;
    private List<EventAttendee> attendees;


    public boolean isAcceptedBefor() {
        return acceptedBefor;
    }

    public void setAcceptedBefor(boolean acceptedBefor) {
        this.acceptedBefor = acceptedBefor;
    }

    public List<EventAttendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<EventAttendee> attendees) {
        this.attendees = attendees;
    }
}

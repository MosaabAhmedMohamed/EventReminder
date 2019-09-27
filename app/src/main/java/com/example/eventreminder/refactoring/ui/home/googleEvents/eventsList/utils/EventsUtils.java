package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils;

import com.example.eventreminder.refactoring.data.models.AcceptEventCheckModel;
import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.refactoring.data.models.ListEntity;
import com.example.eventreminder.refactoring.util.DateTimeUtils;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsUtils {
    private static EventsUtils instance;

    private EventsUtils() {
    }

    public static EventsUtils getInstance() {
        if (instance == null) {
            instance = new EventsUtils();
        }
        return instance;
    }

    public static HashMap<Integer, ListEntity> applyKeyValuePairForDateAndModel(List<ListEntity> weatherList) {
        HashMap<Integer, ListEntity> forecastModelsMap = new HashMap<>();
        for (int i = 0; i < weatherList.size(); i++) {
            forecastModelsMap.put(weatherList.get(i).getDt(), weatherList.get(i));
        }
        return forecastModelsMap;
    }

    public static ArrayList<EventDateTimeModel> validateEventDateStartEndTime(List<Event> events) throws IOException {
        if (events != null) {
            ArrayList<EventDateTimeModel> eventDateTimeModels = new ArrayList<>();
            for (Event event : events) {
                if (event.getStart().getDateTime() != null &&
                        event.getStart().getDateTime().getValue() != 0 &&
                        event.getEnd().getDateTime().getValue() != 0) {

                    String date = DateTimeUtils.getFormattedDate(event.getStart().getDateTime().getValue());
                    int eventStart = DateTimeUtils.convertUnixToSeconds(event.getStart().getDateTime().getValue());
                    int eventEnd = DateTimeUtils.convertUnixToSeconds(event.getEnd().getDateTime().getValue());
                    eventDateTimeModels.add(new EventDateTimeModel(date, eventStart, eventEnd, event));
                }
            }
            return eventDateTimeModels;
        }
        return null;
    }


    public static List<Event> getDataFromApi(Calendar googleCalendar) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = googleCalendar.events()
                .list("primary")
                .setMaxResults(30)
                .setTimeMin(now)
                .setOrderBy("startTime").setSingleEvents(true).execute();
        //  Log.d(TAG, "getDataFromApi: " + events.getItems().toString());
        return events.getItems();
    }


    public AcceptEventCheckModel checkAcceptance(List<EventAttendee> attendees, String currUserMail) {
        AcceptEventCheckModel acceptEventCheckModel = new AcceptEventCheckModel();
        for (int i = 0; i < attendees.size(); i++) {
            if (attendees.get(i).getEmail().equals(currUserMail)) {
                if (attendees.get(i).getResponseStatus().equals("accepted")) {
                    acceptEventCheckModel.setAcceptedBefor(true);
                } else {
                    //showProgressBar(true);
                    attendees.get(i).setResponseStatus("accepted");
                    acceptEventCheckModel.setAttendees(attendees);
                }
                break;
            }
        }
        return acceptEventCheckModel;
    }


    public boolean cheekForFreeTimeForEvent(int eventStartTimeInSeconds, String date, ArrayList<EventDateTimeModel> eventDateTimeModels) {
        for (EventDateTimeModel eventDateTimeModel : eventDateTimeModels) {
            if (date.equals(eventDateTimeModel.getDay())) {
                if (eventStartTimeInSeconds < eventDateTimeModel.getStartTime() || eventStartTimeInSeconds > eventDateTimeModel.getEndTime()) {
                    return true;
                }
            } else if (!date.equals(eventDateTimeModel.getDay())) {
                if (eventStartTimeInSeconds < eventDateTimeModel.getStartTime() || eventStartTimeInSeconds > eventDateTimeModel.getEndTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean cheekForEventOverlapping(String eventFormattedDate, Event event, int position, ArrayList<EventDateTimeModel> eventDateTimeModels, OnEventActionLIstner lIstner) {
        for (EventDateTimeModel eventDateTimeModel : eventDateTimeModels) {
            if (eventDateTimeModel.getDay().equals(eventFormattedDate) &&
                    !eventDateTimeModel.getEvent().getId().equals(event.getId()) &&
                    !eventDateTimeModel.isDiscard()) {

                int startTimeEventInList = DateTimeUtils.convertUnixToSeconds(event.getStart().getDateTime().getValue());
                int endTimeEventInList = DateTimeUtils.convertUnixToSeconds(event.getEnd().getDateTime().getValue());
                if (startTimeEventInList <= eventDateTimeModel.getStartTime()) {
                    if (startTimeEventInList <= eventDateTimeModel.getEndTime() || startTimeEventInList >= eventDateTimeModel.getEndTime()) {
                        lIstner.onEventOverlapped(new EventDateTimeModel(eventFormattedDate,
                                startTimeEventInList, endTimeEventInList, event), eventDateTimeModel, position);
                        eventDateTimeModel.setDiscard(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

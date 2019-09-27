package com.example.eventreminder.refactoring.util;

import java.util.ArrayList;
import java.util.Set;

public class Constants {
    private static Constants constants;

    private Constants() {
    }

    public static Constants getInstance() {
        if (constants == null) {
            constants = new Constants();
        }
        return constants;
    }

    public static final String PREF_NAME = "mydagger1thexampel_pref";
    public static final String IS_USER_LOGGED_IN_KEY = "LOGGED_IN_KEY";
    public static final String USER_DATA_KEY = "USER_DATA";

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    public static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    public static final int READ_TIMEOUT = 10; // 2 seconds
    public static final int WRITE_TIMEOUT = 10; // 2 seconds

    public static final String BASE_URL = "http://api.openweathermap.org";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";


    public static final int RC_SIGN_IN = 9001;
    public static final int RC_RECOVERABLE = 9002;

    public static final int SELECTED_EVENT_TO_RESCHDULE = 1;
    public static final int SELECTED_SECOUND_EVENT = 2;

    public static final int UPDATE_EVENTS = 10;
    public static final int INIT_EVENTS = 9;
    public static final int PAGING_EVENTS = 11;
    public static final int EVENTS_ERROR = -1;

    public static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";
    public static final String EVENTS_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    public static final String GOOGLE_USER = "GOOGLE_SUER";
    public static final String FACEBOOK_SUER = "FACEBOOK_USER";
    public static final String EVENT_ONE = "EVENT_ONE";
    public static final String EVENT_SELECTED_TO_EDIT = "EVENT_SELECTED_TO_EDIT";
    public static final String EVENTS_MODEL = "EVENTS_MODEL";


    public String openWeatherMapAPIKey = "af7b1830541171b1ec00a7f31168e3d7";
    public String OpenWeatherMapSotrageUrl = "http://openweathermap.org/img/w/";


    public int getClosestTimeUnix(Set<Integer> keys, long eventDateTime) {
        ArrayList<Integer> keysList = new ArrayList<>(keys);


        int min = Integer.MAX_VALUE;
        int closest = (int) eventDateTime;

        for (int v : keysList) {
            final int diff = (int) Math.abs(v - eventDateTime);
            if (diff < min) {
                min = diff;
                closest = v;
            }
        }
        return closest;
    }
}

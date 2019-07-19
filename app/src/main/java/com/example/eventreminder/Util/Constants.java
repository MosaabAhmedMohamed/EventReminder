package com.example.eventreminder.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ScrollView;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public static final int RC_SIGN_IN = 9001;
    public static final int RC_RECOVERABLE = 9002;

    public static final int SELECTED_EVENTFROM_LIST = 1;
    public static final int SELECTED_SECOUND_EVENT = 2;

    public static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";
    public static final String EVENTS_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    public static final String GOOGLE_USER = "GOOGLE_SUER";
    public static final String FACEBOOK_SUER = "FACEBOOK_USER";
    public static final String EVENT_ONE = "EVENT_ONE";
    public static final String EVENT_SELECTED_TO_EDIT = "EVENT_SELECTED_TO_EDIT";
    public static final String EVENTS_MODEL = "EVENTS_MODEL";


    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public String openWeatherMapAPIKey = "af7b1830541171b1ec00a7f31168e3d7";
    public String OpenWeatherMapSotrageUrl = "http://openweathermap.org/img/w/";
    public String BaseUrl = "http://api.openweathermap.org";

    public String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE dd MM YYYY");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public String convertUnixToDay(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String formatted = sdf.format(date);
        return formatted;
    }

    public String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }

    public int convertUnixToSeconds(long dt) {
        int seconds, hour, minute;
        String hoursAndSeconds = convertUnixToHour(dt);
        String[] hAndm = new String[2];
        hAndm = hoursAndSeconds.split(":");
        hour = Integer.parseInt(hAndm[0]);
        minute = Integer.parseInt(hAndm[1]);
        return seconds = (minute * 60) + (hour *3600);
    }


    public String getFormattedTime(long dt) {

        //creating Date from millisecond
        Date currentDate = new Date(dt);

        //printing value of Date
        System.out.println("current Date: " + currentDate);

        DateFormat df = new SimpleDateFormat("HH:mm");

        //formatted value of current Date
        System.out.println("Milliseconds to Date: " + df.format(currentDate));

        //Converting milliseconds to Date using Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dt);

        System.out.println("Milliseconds to Date using Calendar:"
                + df.format(cal.getTime()));
/*
        //copying one Date's value into another Date in Java
        Date now = new Date();
        Date copiedDate = new Date(now.getTime());

        System.out.println("original Date: " + df.format(now));
        System.out.println("copied Date: " + df.format(copiedDate));*/
        return df.format(cal.getTime().getTime());
    }

    public String getFormattedDate(long dt) {
        //creating Date from millisecond
        Date currentDate = new Date(dt);
        //printing value of Date
        System.out.println("current Date: " + currentDate);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        //formatted value of current Date
        System.out.println("Milliseconds to Date: " + df.format(currentDate));
        //Converting milliseconds to Date using Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dt);
        return df.format(cal.getTime().getTime());
    }

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

    public boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}


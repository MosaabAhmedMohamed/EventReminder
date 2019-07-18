package com.example.eventreminder.Util;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ScrollView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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


    public static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";
    public static final String EVENTS_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    public static final String GOOGLE_USER = "GOOGLE_SUER";
    public static final String FACEBOOK_SUER = "FACEBOOK_USER";

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

    public static String convertUnixToDay(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
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

}


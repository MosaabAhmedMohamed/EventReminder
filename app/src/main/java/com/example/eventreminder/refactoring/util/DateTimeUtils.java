package com.example.eventreminder.refactoring.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    private static DateTimeUtils instance;

    private DateTimeUtils() {
    }

    public static DateTimeUtils getInstance() {
        if (instance == null)
            instance = new DateTimeUtils();
        return instance;
    }

    public static String convertUnixToDate(long dt) {
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

    public static int convertUnixToSeconds(long dt) {
        int seconds, hour, minute;
        String hoursAndSeconds = convertUnixToHour(dt);
        String[] hAndm = new String[2];
        hAndm = hoursAndSeconds.split(":");
        hour = Integer.parseInt(hAndm[0]);
        minute = Integer.parseInt(hAndm[1]);
        return seconds = (minute * 60) + (hour * 3600);
    }


    public static String getFormattedTime(long dt) {

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

    public static String getFormattedDate(long dt) {
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

    public int convertTimeToSeconds(int minute, int hour) {
        return (minute * 60) + (hour * 3600);
    }
}

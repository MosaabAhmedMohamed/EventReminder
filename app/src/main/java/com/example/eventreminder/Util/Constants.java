package com.example.eventreminder.Util;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ScrollView;

public class Constants {
    private static Constants constants;
    private Constants(){}

    public static Constants getInstance()
    {
        if (constants == null)
        {
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
    public String OpenWeatherMapSotrageUrl = "http://openweathermap.org/img/w/10d.png";
    public String BaseUrl = "http://api.openweathermap.org";

}


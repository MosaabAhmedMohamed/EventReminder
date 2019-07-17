package com.example.eventreminder.Util;

import android.text.TextUtils;
import android.util.Patterns;

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


    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public String BaseUrl = "http://invent.solutions/thamarat/";

}


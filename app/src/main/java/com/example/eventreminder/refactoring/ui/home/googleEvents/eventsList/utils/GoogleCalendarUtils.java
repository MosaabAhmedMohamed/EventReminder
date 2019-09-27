package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.eventreminder.refactoring.data.models.AcceptEventCheckModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.Arrays;
import java.util.List;

public class GoogleCalendarUtils {

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static Calendar googleCalendar;

    private GoogleCalendarUtils() {
    }

    public static GoogleAccountCredential getAccountCredential(GoogleSignInAccount acc, Context context) throws NullPointerException {
        if (acc != null && acc.getAccount() != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(acc.getAccount().name);
            credential.setSelectedAccount(acc.getAccount());
            return credential;
        }
        return null;
    }

    public static Calendar getCalendarInstance(GoogleAccountCredential credential) {
        if (googleCalendar == null) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            googleCalendar = new Calendar.Builder(transport, jsonFactory, credential).setApplicationName("Event Reminder").build();
        }
        return googleCalendar;
    }

}

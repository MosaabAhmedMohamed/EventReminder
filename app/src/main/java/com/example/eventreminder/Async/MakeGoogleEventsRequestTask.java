package com.example.eventreminder.Async;

import android.os.AsyncTask;
import android.util.Log;
import com.example.eventreminder.Views.Fragments.GoogleEventsList;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.util.List;

public class MakeGoogleEventsRequestTask extends AsyncTask<Void, Void, List<Event>> {
    private static final String TAG = "MakeGoogleEventsRequest";

    private Calendar googleCalendar;
    private Exception mLastError = null;

    private GoogleEventsList googleEventsList;
    public MakeGoogleEventsRequestTask(GoogleEventsList googleEventsList, Calendar calendar) {
        this.googleEventsList = googleEventsList;
        googleCalendar = calendar;
    }

    @Override
    protected List<Event> doInBackground(Void... params) {
        try {

            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        googleEventsList.showProgressBar(true);
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        super.onPostExecute(events);
        googleEventsList.showProgressBar(false);
        if (events == null || events.size() == 0) {
            try {
                googleEventsList.getEvents(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                googleEventsList.getEvents(events);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        googleEventsList.showProgressBar(false);
        if (mLastError != null) {
            if (mLastError instanceof UserRecoverableAuthIOException) {
                googleEventsList.onRecoverableAuthException((UserRecoverableAuthIOException) mLastError);
            } else {
                Log.d(TAG, "onCancelled: " + mLastError.getMessage());
                googleEventsList.onErrorResponse(mLastError.getMessage());
            }
        } else {
            Log.d(TAG, "onCancelled: " + "canceled");
        }
    }

    private List<Event> getDataFromApi() throws IOException {


        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = googleCalendar.events()
                .list("primary")
                .setMaxResults(30)
                .setTimeMin(now)
                .setOrderBy("startTime").setSingleEvents(true).execute();

        return events.getItems();
    }
}

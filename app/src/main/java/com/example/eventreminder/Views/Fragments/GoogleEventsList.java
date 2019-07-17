package com.example.eventreminder.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.eventreminder.Async.MakeGoogleEventsRequestTask;
import com.example.eventreminder.BaseViews.BaseFragment;
import com.example.eventreminder.R;
import com.example.eventreminder.Requests.Responses.WeatherResponse;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.ViewModels.GoogleListViewModel;
import com.example.eventreminder.Views.Activites.Home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.eventreminder.Util.Constants.RC_RECOVERABLE;

public class GoogleEventsList extends BaseFragment {
    private static final String TAG = "GoogleEventsList";
    private View view;


    private GoogleSignInAccount acc;
    private MakeGoogleEventsRequestTask makeGoogleEventsRequestTask;
    private GoogleListViewModel googleListViewModel;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public static GoogleEventsList newInstance() {
        return new GoogleEventsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.events_list, container, false);
            init();

        }
        return view;
    }

    private void init() {
        googleListViewModel = ViewModelProviders.of(this).get(GoogleListViewModel.class);
        acc = new Gson().fromJson(sharedPreferences.getString(Constants.GOOGLE_USER, " "), GoogleSignInAccount.class);
        getUserData(acc);
        subScribeToObserver();
    }

    private void subScribeToObserver() {
        if (googleListViewModel.getWeatherResponseMutableLiveData() == null)
        {
            googleListViewModel.getForCastData("cairo",Constants.getInstance().openWeatherMapAPIKey,"16").observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {
                    if (weatherResponse.getCod().equals("200"))
                    {
                        Log.d(TAG, "onChanged: 1"+weatherResponse.getList().size());
                    }else
                    {
                        Toast.makeText(getActivity(), "errorr", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            googleListViewModel.getWeatherResponseMutableLiveData().observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {
                    Log.d(TAG, "onChanged: 2"+weatherResponse.getList().size());

                }
            });
        }
    }

    private void getUserData(GoogleSignInAccount acc) {
        if (acc != null && acc.getAccount() != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(),
                    Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(acc.getAccount().name);
            getEventsFromApi(credential);
        } else {
          /*  startActivity(new Intent(this, Login.class));
            finish();*/
        }
    }

    private void getEventsFromApi(GoogleAccountCredential credential) {
        makeGoogleEventsRequestTask = new MakeGoogleEventsRequestTask(this, credential);
        makeGoogleEventsRequestTask.execute();
    }

    public void onRecoverableAuthException(UserRecoverableAuthIOException recoverableException) {
        Log.w(TAG, "onRecoverableAuthException", recoverableException);
        startActivityForResult(recoverableException.getIntent(), RC_RECOVERABLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_RECOVERABLE) {
            if (resultCode == RESULT_OK) {
                if (getActivity() != null && isAdded()) {
                    acc = GoogleSignIn.getLastSignedInAccount(getActivity());
                    getUserData(acc);
                }

            } else {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getEvents(List<Event> events) throws IOException {
        if (events != null) {
            for (Event event : events) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                Log.d(TAG, "getDataFromApi:   2 " + event.toPrettyString());
                Log.d(TAG, "getDataFromApi: " + String.format("%s (%s)", event.getSummary(), start));
            }
        }
    }

    public void showProgressBar(boolean visibility) {
        if (getActivity() != null && isAdded())
            ((Home) getActivity()).showProgressBar(visibility);
    }

}

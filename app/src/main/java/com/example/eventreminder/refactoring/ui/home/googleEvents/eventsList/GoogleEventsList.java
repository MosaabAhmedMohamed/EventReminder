package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventreminder.refactoring.data.models.AcceptEventCheckModel;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.Async.AcceptGoogleEventsTask;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.Async.DeleteGoogleEventTask;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.Async.MakeGoogleEventsRequestTask;
import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.refactoring.data.models.GoogleEventsAndForecastModel;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.WeatherResponse;
import com.example.eventreminder.refactoring.network.Resource;
import com.example.eventreminder.refactoring.ui.auth.AuthActivity;
import com.example.eventreminder.refactoring.ui.base.BaseFragment;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.GoogleCalendarUtils;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.OnEventActionLIstner;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.OnHandelOverlappingListner;
import com.example.eventreminder.refactoring.util.Constants;
import com.example.eventreminder.refactoring.util.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils.applyKeyValuePairForDateAndModel;
import static com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils.validateEventDateStartEndTime;
import static com.example.eventreminder.refactoring.util.Constants.EVENTS_ERROR;
import static com.example.eventreminder.refactoring.util.Constants.INIT_EVENTS;
import static com.example.eventreminder.refactoring.util.Constants.PAGING_EVENTS;
import static com.example.eventreminder.refactoring.util.Constants.RC_RECOVERABLE;
import static com.example.eventreminder.refactoring.util.Constants.UPDATE_EVENTS;

public class GoogleEventsList extends BaseFragment implements OnEventActionLIstner, SwipeRefreshLayout.OnRefreshListener, OnHandelOverlappingListner {
    private static final String TAG = "GoogleEventsList";

    @BindView(R.id.events_recycler)
    RecyclerView eventsRecycler;
    @BindView(R.id.list_view_status_tv)
    TextView listViewStatusTv;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    Bundle bundle;
    @Inject
    GoogleEventsListAdapter googleEventsListAdapter;
    @Inject
    ViewModelProviderFactory providerFactory;
    private GoogleListViewModel googleListViewModel;


    private AcceptGoogleEventsTask acceptGoogleEventsTask;
    private DeleteGoogleEventTask deleteGoogleEventTask;
    private MakeGoogleEventsRequestTask makeGoogleEventsRequestTask;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;
    private Calendar googleCalendar;

    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler();
    private boolean isevery30SecondsRuning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getBaseActivity()).setTitleTv("Google calendar events");
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        googleListViewModel = ViewModelProviders.of(this, providerFactory).get(GoogleListViewModel.class);
        initEventsList();
        weatherObserver();
        initRefreshLayout();
    }

    private void initEventsList() {
        eventsRecycler.setHasFixedSize(true);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        googleEventsListAdapter.setOnEventActionLIstner(this);
        googleEventsAndForecastModel = new GoogleEventsAndForecastModel();
        googleEventsListAdapter.setLoggedInUserEmail(googleListViewModel.getAuthAccount().getEmail());
    }

    private void initRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.black,
                android.R.color.holo_red_light,
                android.R.color.darker_gray);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void weatherObserver() {
        googleListViewModel.observeWeather().removeObservers(getViewLifecycleOwner());
        googleListViewModel.observeWeather().observe(getViewLifecycleOwner(), new Observer<Resource<WeatherResponse>>() {
            @Override
            public void onChanged(Resource<WeatherResponse> weatherResponseResource) {
                if (weatherResponseResource != null) {
                    switch (weatherResponseResource.status) {
                        case LOADING: {
                            //  Log.d(TAG, "onChanged:loading ");
                            setLoadingStatus(true);
                            break;
                        }
                        case SUCCESS: {
                            // Log.d(TAG, "onChanged: get posts" + weatherResponseResource.data.getList().toString());
                            setLoadingStatus(false);
                            googleEventsAndForecastModel.setForecastModels(applyKeyValuePairForDateAndModel(weatherResponseResource.data.getList()));
                            initCalendarAndGetEventsFromApi();
                            break;
                        }
                        case ERROR: {
                            setLoadingStatus(false);
                            showSnackBar(weatherResponseResource.message);
                            break;
                        }
                    }
                }
            }
        });
    }


    private void initCalendarAndGetEventsFromApi() {
        try {
            googleCalendar = GoogleCalendarUtils.getCalendarInstance(GoogleCalendarUtils.getAccountCredential(googleListViewModel.getAuthAccount(), getBaseActivity()));
            reInitGoogleEventsTask();
        } catch (NullPointerException e) {
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getBaseActivity().finish();
        }
    }

    public void onRecoverableAuthException(UserRecoverableAuthIOException recoverableException) {
        startActivityForResult(recoverableException.getIntent(), RC_RECOVERABLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_RECOVERABLE) {
            if (resultCode == RESULT_OK) {
                if (getActivity() != null && isAdded()) {
                    googleListViewModel.setGoogleAuthAccount(GoogleSignIn.getLastSignedInAccount(getBaseActivity()));
                    initCalendarAndGetEventsFromApi();
                }
            } else {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getEvents(List<Event> events, int type) throws IOException {
        swipeRefreshLayout.setRefreshing(false);
        if (type != EVENTS_ERROR) {
            ArrayList<EventDateTimeModel> eventDateTimeModels = validateEventDateStartEndTime(events);
            if (eventDateTimeModels != null) {
                googleEventsAndForecastModel.setEventDateTimeModels(eventDateTimeModels);
                Log.d(TAG, "getEvents: " + googleEventsAndForecastModel.getEventDateTimeModels().size());
            }
            callEventsListAdapter(events, type);
        }
    }


    private void callEventsListAdapter(List<Event> events, int type) {
        if (events == null || events.size() == 0 && type != PAGING_EVENTS) {
            listViewStatusTv.setVisibility(View.VISIBLE);
            listViewStatusTv.setText("You don't have any events yet");
        } else {
            listViewStatusTv.setVisibility(View.GONE);
            eventsRecycler.setVisibility(View.VISIBLE);
            if (type == INIT_EVENTS) {
                googleEventsAndForecastModel.setEventsModels(events);
                googleEventsListAdapter.setGoogleEventsAndForecastModel(googleEventsAndForecastModel);
                eventsRecycler.setAdapter(googleEventsListAdapter);
            } else if (type == UPDATE_EVENTS) {
                googleEventsListAdapter.setUpdatedEvents(events);
            }
        }
        if (!isevery30SecondsRuning)
            runEvery30Second();
    }

    @Override
    public void onDeleteEvent(String id) {
        if (deleteGoogleEventTask != null)
            deleteGoogleEventTask.cancel(true);

        deleteGoogleEventTask = new DeleteGoogleEventTask(this, googleCalendar, id);
        deleteGoogleEventTask.execute();
    }

    @Override
    public void onAcceptEvent(int position) {
        if (googleEventsAndForecastModel.getEventsModels().get(position).getCreator().getEmail().equals(googleListViewModel.getAuthAccount().getEmail())) {
            Toast.makeText(getActivity(), "You can't accept event that you already created", Toast.LENGTH_SHORT).show();
        } else if (googleEventsAndForecastModel.getEventsModels().get(position).getCreator() == null)
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
        else {
            List<EventAttendee> attendees = googleEventsAndForecastModel.getEventsModels().get(position).getAttendees();
            AcceptEventCheckModel eventCheckModel = EventsUtils.getInstance().checkAcceptance(attendees, googleListViewModel.getAuthAccount().getEmail());
            if (eventCheckModel.isAcceptedBefor()) {
                Toast.makeText(getActivity(), "You already accepted the invention", Toast.LENGTH_SHORT).show();
            } else {
                //showProgressBar(true);
                googleEventsAndForecastModel.getEventsModels().get(position).setAttendees(eventCheckModel.getAttendees());
                acceptGoogleEvent(position);
            }
        }
    }

    private void acceptGoogleEvent(int position) {
        if (acceptGoogleEventsTask != null)
            acceptGoogleEventsTask.cancel(true);

        acceptGoogleEventsTask = new AcceptGoogleEventsTask(this, googleCalendar, googleEventsAndForecastModel.getEventsModels().get(position));
        acceptGoogleEventsTask.execute();
    }

    @Override
    public void onEventOverlapped(EventDateTimeModel firstEvent, EventDateTimeModel secondEvent, int position) {
        ArrayList<EventDateTimeModel> eventDateTimeModels = new ArrayList<>();
        eventDateTimeModels.add(firstEvent);
        eventDateTimeModels.add(secondEvent);
        OverlappingDialog overlappingDialog = OverlappingDialog.newInstance(eventDateTimeModels, position, googleListViewModel.getAuthAccount().getEmail());
        overlappingDialog.setTargetFragment(GoogleEventsList.this, 1);
        overlappingDialog.show(getParentFragmentManager(), "selectDialog");
    }


    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkConnected(getBaseActivity())) {
            if (googleEventsAndForecastModel.getEventsModels() != null)
                googleEventsAndForecastModel.getEventsModels().clear();
            swipeRefreshLayout.setRefreshing(true);
            reInitGoogleEventsTask();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void reInitGoogleEventsTask() {
        if (makeGoogleEventsRequestTask != null)
            makeGoogleEventsRequestTask.cancel(true);
        makeGoogleEventsRequestTask = new MakeGoogleEventsRequestTask(this, googleCalendar);
        makeGoogleEventsRequestTask.execute();
    }

    private void runEvery30Second() {
        isevery30SecondsRuning = true;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        reInitGoogleEventsTask();
                    }
                });
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        acceptGoogleEventsTask = null;
        deleteGoogleEventTask = null;
        makeGoogleEventsRequestTask = null;
        scheduledExecutorService = null;
        handler = null;
    }

    @Override
    public void onHandel(boolean status, EventDateTimeModel selectedEventToReschedule, int positionOfEventInList, int eventSelectedFromDialog) {
        if (status && eventSelectedFromDialog == Constants.SELECTED_EVENT_TO_RESCHDULE) {
            onDeleteEvent(selectedEventToReschedule.getEvent().getId());
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
            bundle.putParcelableArrayList(Constants.EVENTS_MODEL, googleEventsAndForecastModel.getEventDateTimeModels());
            bundle.putParcelable(Constants.EVENT_SELECTED_TO_EDIT, selectedEventToReschedule);
            //  newInstance(googleEventsAndForecastModel.getEventDateTimeModels(), selectedEventToReschedule));
            Navigation.findNavController(getBaseActivity(), R.id.nav_host_fragment).navigate(R.id.RescheduleOverlappedEvent, bundle);
        } else if (!status && eventSelectedFromDialog == 0) {
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
        } else if (!status) {
            onDeleteEvent(selectedEventToReschedule.getEvent().getId());
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
        }
    }

    public void onErrorResponse(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
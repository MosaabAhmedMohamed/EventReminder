package com.example.eventreminder.refactoring.di.home;


import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.GoogleEventsList;
import com.example.eventreminder.refactoring.ui.home.googleEvents.reschduleEvent.RescheduleOverlappedEvent;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class HomeFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract GoogleEventsList googleEventsList();

    @ContributesAndroidInjector
    abstract RescheduleOverlappedEvent rescheduleOverlappedEvent();

}

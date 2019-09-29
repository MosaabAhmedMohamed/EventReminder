package com.example.eventreminder.refactoring.di.home;

import com.example.eventreminder.refactoring.ui.home.city.EventCityDialog;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DialogBuilderModule {

    @ContributesAndroidInjector
    abstract EventCityDialog eventCityDialog();
}

package com.example.eventreminder.refactoring.di.home;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.GoogleListViewModel;
import com.example.eventreminder.refactoring.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class HomeViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GoogleListViewModel.class)
    public abstract ViewModel bindGoogleListVM(GoogleListViewModel googleListViewModel);
}

package com.example.eventreminder.refactoring.di.home;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.ui.home.HomeVM;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.GoogleListViewModel;
import com.example.eventreminder.refactoring.di.ViewModelKey;
import com.example.eventreminder.refactoring.ui.home.profile.ProfileVM;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class HomeViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GoogleListViewModel.class)
    public abstract ViewModel bindGoogleListVM(GoogleListViewModel googleListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileVM.class)
    public abstract ViewModel bindProfileVM(ProfileVM profileVM);

    @Binds
    @IntoMap
    @ViewModelKey(HomeVM.class)
    public abstract ViewModel bindHomeVM(HomeVM homeVM);
}

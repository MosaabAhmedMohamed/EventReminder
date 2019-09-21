package com.example.eventreminder.refactoring.di;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {


    @Binds
    public abstract ViewModelProvider.Factory bindsViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);
}

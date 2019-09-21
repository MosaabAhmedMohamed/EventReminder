package com.example.eventreminder.refactoring.di.splash;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.di.ViewModelKey;
import com.example.eventreminder.refactoring.ui.splash.SplashVM;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class SplashVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashVM.class)
    public abstract ViewModel bindSplashVM(SplashVM splashVM);

}

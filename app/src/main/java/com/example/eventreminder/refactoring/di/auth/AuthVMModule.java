package com.example.eventreminder.refactoring.di.auth;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.di.ViewModelKey;
import com.example.eventreminder.refactoring.ui.auth.AuthVM;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AuthVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthVM.class)
    public abstract ViewModel bindAuthViewModel(AuthVM authVM);
}

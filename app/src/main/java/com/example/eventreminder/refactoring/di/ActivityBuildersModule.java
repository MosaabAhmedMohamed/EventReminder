package com.example.eventreminder.refactoring.di;

import com.example.eventreminder.refactoring.di.auth.AuthScope;
import com.example.eventreminder.refactoring.di.auth.AuthVMModule;
import com.example.eventreminder.refactoring.di.home.HomeFragmentsBuilderModule;
import com.example.eventreminder.refactoring.di.home.HomeModule;
import com.example.eventreminder.refactoring.di.home.HomeScope;
import com.example.eventreminder.refactoring.di.home.HomeViewModelsModule;
import com.example.eventreminder.refactoring.di.splash.SplashScope;
import com.example.eventreminder.refactoring.di.splash.SplashVMModule;
import com.example.eventreminder.refactoring.ui.auth.AuthActivity;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.ui.splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {


    @SplashScope
    @ContributesAndroidInjector(modules = {SplashVMModule.class})
    abstract SplashActivity contrbuteSplashActivity();

    @AuthScope
    @ContributesAndroidInjector(modules = {AuthVMModule.class})
    abstract AuthActivity contrbuteLoginActivity();

    @HomeScope
    @ContributesAndroidInjector(modules = {HomeFragmentsBuilderModule.class, HomeModule.class, HomeViewModelsModule.class})
    abstract HomeActivity contrbuteHomeActivity();
}

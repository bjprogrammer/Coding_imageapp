package com.bobby.coding.deps;

import com.bobby.coding.main.MainActivity;
import com.bobby.coding.model.PrefManager;
import com.bobby.coding.splash.SplashActivity;
import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.utils.DatabaseScope;

import javax.inject.Singleton;

import dagger.Component;

//Dependency injection Component class
@DatabaseScope
@Singleton
@Component(modules = {PrefManager.class,SettingDatabaseHelper.class})
public interface Deps {
    void maininject(MainActivity mainActivity);
    void splashinject(SplashActivity splashActivity);
}
package com.bobby.coding.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.bobby.coding.utils.Constants;
import javax.inject.Named;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

//Shared Preference handling
@Module
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(Constants.PREF_NAME, 0);
        editor = pref.edit();
    }

    @Provides
    @Singleton
    @Named("splash")
    SharedPreferences getpref() {
        return pref;
    }

    @Provides
    @Singleton
    @Named("splash")
    SharedPreferences.Editor getEditor() {
        return editor;
    }
}

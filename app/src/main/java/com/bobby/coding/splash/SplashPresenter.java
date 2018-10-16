package com.bobby.coding.splash;

import android.content.SharedPreferences;

import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.utils.Constants;


public class SplashPresenter {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    public SettingDatabaseHelper dh2;

    public SplashPresenter(SharedPreferences pref, SharedPreferences.Editor editor, SettingDatabaseHelper dh2) {
        this.editor = editor;
        this.pref = pref;
        this.dh2=dh2;
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Constants.IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Constants.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setDefault(){
        dh2.insertdata("5");
    }
}

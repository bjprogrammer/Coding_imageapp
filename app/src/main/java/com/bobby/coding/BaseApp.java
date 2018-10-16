package com.bobby.coding;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import com.bobby.coding.deps.Deps;
import com.bobby.coding.model.PrefManager;
import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.utils.Constants;


public class BaseApp extends Application {
    Deps deps;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        assetManager();
        deps = com.bobby.coding.deps.DaggerDeps.builder()
                .settingDatabaseHelper(new SettingDatabaseHelper(this))
                .prefManager(new PrefManager(this))
                .build();
        }

    public Deps getDeps() {
        return deps;
    }

    private void assetManager(){
        AssetManager assetManager = this.getApplicationContext().getAssets();
        Constants.montserrat_regular = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf");
        Constants.montserrat_semiBold = Typeface.createFromAsset(assetManager, "fonts/Montserrat-SemiBold.ttf");
        Constants.robotoslab_regular = Typeface.createFromAsset(assetManager, "fonts/RobotoSlab-Regular.ttf");
    }

}

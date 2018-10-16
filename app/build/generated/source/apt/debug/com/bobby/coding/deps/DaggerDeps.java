package com.bobby.coding.deps;

import android.content.Context;
import android.content.SharedPreferences;
import com.bobby.coding.main.MainActivity;
import com.bobby.coding.main.MainActivity_MembersInjector;
import com.bobby.coding.model.PrefManager;
import com.bobby.coding.model.PrefManager_GetEditorFactory;
import com.bobby.coding.model.PrefManager_GetprefFactory;
import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.model.SettingDatabaseHelper_GetContextFactory;
import com.bobby.coding.model.SettingDatabaseHelper_ProvideDatabaseFactory;
import com.bobby.coding.splash.SplashActivity;
import com.bobby.coding.splash.SplashActivity_MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerDeps implements Deps {
  private Provider<Context> getContextProvider;

  private Provider<SettingDatabaseHelper> provideDatabaseProvider;

  private Provider<SharedPreferences> getprefProvider;

  private Provider<SharedPreferences.Editor> getEditorProvider;

  private DaggerDeps(Builder builder) {
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {
    this.getContextProvider =
        DoubleCheck.provider(
            SettingDatabaseHelper_GetContextFactory.create(builder.settingDatabaseHelper));
    this.provideDatabaseProvider =
        DoubleCheck.provider(
            SettingDatabaseHelper_ProvideDatabaseFactory.create(
                builder.settingDatabaseHelper, getContextProvider));
    this.getprefProvider =
        DoubleCheck.provider(PrefManager_GetprefFactory.create(builder.prefManager));
    this.getEditorProvider =
        DoubleCheck.provider(PrefManager_GetEditorFactory.create(builder.prefManager));
  }

  @Override
  public void maininject(MainActivity mainActivity) {
    injectMainActivity(mainActivity);
  }

  @Override
  public void splashinject(SplashActivity splashActivity) {
    injectSplashActivity(splashActivity);
  }

  private MainActivity injectMainActivity(MainActivity instance) {
    MainActivity_MembersInjector.injectDh(instance, provideDatabaseProvider.get());
    return instance;
  }

  private SplashActivity injectSplashActivity(SplashActivity instance) {
    SplashActivity_MembersInjector.injectPref(instance, getprefProvider.get());
    SplashActivity_MembersInjector.injectEditor(instance, getEditorProvider.get());
    SplashActivity_MembersInjector.injectDh2(instance, provideDatabaseProvider.get());
    return instance;
  }

  public static final class Builder {
    private SettingDatabaseHelper settingDatabaseHelper;

    private PrefManager prefManager;

    private Builder() {}

    public Deps build() {
      if (settingDatabaseHelper == null) {
        throw new IllegalStateException(
            SettingDatabaseHelper.class.getCanonicalName() + " must be set");
      }
      if (prefManager == null) {
        throw new IllegalStateException(PrefManager.class.getCanonicalName() + " must be set");
      }
      return new DaggerDeps(this);
    }

    public Builder prefManager(PrefManager prefManager) {
      this.prefManager = Preconditions.checkNotNull(prefManager);
      return this;
    }

    public Builder settingDatabaseHelper(SettingDatabaseHelper settingDatabaseHelper) {
      this.settingDatabaseHelper = Preconditions.checkNotNull(settingDatabaseHelper);
      return this;
    }
  }
}

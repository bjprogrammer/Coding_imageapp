package com.bobby.coding.splash;

import android.content.SharedPreferences;
import com.bobby.coding.model.SettingDatabaseHelper;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class SplashActivity_MembersInjector implements MembersInjector<SplashActivity> {
  private final Provider<SharedPreferences> prefProvider;

  private final Provider<SharedPreferences.Editor> editorProvider;

  private final Provider<SettingDatabaseHelper> dh2Provider;

  public SplashActivity_MembersInjector(
      Provider<SharedPreferences> prefProvider,
      Provider<SharedPreferences.Editor> editorProvider,
      Provider<SettingDatabaseHelper> dh2Provider) {
    this.prefProvider = prefProvider;
    this.editorProvider = editorProvider;
    this.dh2Provider = dh2Provider;
  }

  public static MembersInjector<SplashActivity> create(
      Provider<SharedPreferences> prefProvider,
      Provider<SharedPreferences.Editor> editorProvider,
      Provider<SettingDatabaseHelper> dh2Provider) {
    return new SplashActivity_MembersInjector(prefProvider, editorProvider, dh2Provider);
  }

  @Override
  public void injectMembers(SplashActivity instance) {
    injectPref(instance, prefProvider.get());
    injectEditor(instance, editorProvider.get());
    injectDh2(instance, dh2Provider.get());
  }

  public static void injectPref(SplashActivity instance, SharedPreferences pref) {
    instance.pref = pref;
  }

  public static void injectEditor(SplashActivity instance, SharedPreferences.Editor editor) {
    instance.editor = editor;
  }

  public static void injectDh2(SplashActivity instance, SettingDatabaseHelper dh2) {
    instance.dh2 = dh2;
  }
}

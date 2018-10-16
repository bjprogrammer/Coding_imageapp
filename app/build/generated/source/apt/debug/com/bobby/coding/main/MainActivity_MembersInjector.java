package com.bobby.coding.main;

import com.bobby.coding.model.SettingDatabaseHelper;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingDatabaseHelper> dhProvider;

  public MainActivity_MembersInjector(Provider<SettingDatabaseHelper> dhProvider) {
    this.dhProvider = dhProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<SettingDatabaseHelper> dhProvider) {
    return new MainActivity_MembersInjector(dhProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectDh(instance, dhProvider.get());
  }

  public static void injectDh(MainActivity instance, SettingDatabaseHelper dh) {
    instance.dh = dh;
  }
}

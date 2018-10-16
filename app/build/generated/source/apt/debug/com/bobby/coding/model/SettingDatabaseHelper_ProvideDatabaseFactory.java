package com.bobby.coding.model;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class SettingDatabaseHelper_ProvideDatabaseFactory
    implements Factory<SettingDatabaseHelper> {
  private final SettingDatabaseHelper module;

  private final Provider<Context> contextProvider;

  public SettingDatabaseHelper_ProvideDatabaseFactory(
      SettingDatabaseHelper module, Provider<Context> contextProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingDatabaseHelper get() {
    return Preconditions.checkNotNull(
        module.provideDatabase(contextProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static SettingDatabaseHelper_ProvideDatabaseFactory create(
      SettingDatabaseHelper module, Provider<Context> contextProvider) {
    return new SettingDatabaseHelper_ProvideDatabaseFactory(module, contextProvider);
  }

  public static SettingDatabaseHelper proxyProvideDatabase(
      SettingDatabaseHelper instance, Context context) {
    return Preconditions.checkNotNull(
        instance.provideDatabase(context),
        "Cannot return null from a non-@Nullable @Provides method");
  }
}

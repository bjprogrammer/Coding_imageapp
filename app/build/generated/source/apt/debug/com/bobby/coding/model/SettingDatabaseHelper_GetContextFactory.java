package com.bobby.coding.model;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class SettingDatabaseHelper_GetContextFactory implements Factory<Context> {
  private final SettingDatabaseHelper module;

  public SettingDatabaseHelper_GetContextFactory(SettingDatabaseHelper module) {
    this.module = module;
  }

  @Override
  public Context get() {
    return Preconditions.checkNotNull(
        module.getContext(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static SettingDatabaseHelper_GetContextFactory create(SettingDatabaseHelper module) {
    return new SettingDatabaseHelper_GetContextFactory(module);
  }

  public static Context proxyGetContext(SettingDatabaseHelper instance) {
    return Preconditions.checkNotNull(
        instance.getContext(), "Cannot return null from a non-@Nullable @Provides method");
  }
}

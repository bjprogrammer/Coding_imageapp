package com.bobby.coding.model;

import android.content.SharedPreferences;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PrefManager_GetprefFactory implements Factory<SharedPreferences> {
  private final PrefManager module;

  public PrefManager_GetprefFactory(PrefManager module) {
    this.module = module;
  }

  @Override
  public SharedPreferences get() {
    return Preconditions.checkNotNull(
        module.getpref(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static PrefManager_GetprefFactory create(PrefManager module) {
    return new PrefManager_GetprefFactory(module);
  }

  public static SharedPreferences proxyGetpref(PrefManager instance) {
    return Preconditions.checkNotNull(
        instance.getpref(), "Cannot return null from a non-@Nullable @Provides method");
  }
}

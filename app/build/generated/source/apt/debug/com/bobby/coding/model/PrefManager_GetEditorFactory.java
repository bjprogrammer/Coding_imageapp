package com.bobby.coding.model;

import android.content.SharedPreferences;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PrefManager_GetEditorFactory implements Factory<SharedPreferences.Editor> {
  private final PrefManager module;

  public PrefManager_GetEditorFactory(PrefManager module) {
    this.module = module;
  }

  @Override
  public SharedPreferences.Editor get() {
    return Preconditions.checkNotNull(
        module.getEditor(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static PrefManager_GetEditorFactory create(PrefManager module) {
    return new PrefManager_GetEditorFactory(module);
  }

  public static SharedPreferences.Editor proxyGetEditor(PrefManager instance) {
    return Preconditions.checkNotNull(
        instance.getEditor(), "Cannot return null from a non-@Nullable @Provides method");
  }
}

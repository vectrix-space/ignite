package com.mineteria.implant.launcher.launch;

import cpw.mods.modlauncher.api.TypesafeMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.List;

public final class ImplantBlackboard {
  private static final TypesafeMap BLACKBOARD = new TypesafeMap();

  public static TypesafeMap.Key<List> LAUNCH_ARGUMENTS = key("mineteria.launch.arguments", List.class);
  public static TypesafeMap.Key<String> LAUNCH_TARGET  = key("mineteria.launch.target", String.class);

  public static TypesafeMap.Key<Path> MOD_TARGET_PATH = key("mineteria.mod.target", Path.class);
  public static TypesafeMap.Key<Path> MOD_CONFIG_PATH = key("mineteria.mod.config", Path.class);

  public static ImplantBlackboard INSTANCE;

  public static <T> TypesafeMap.Key<T> key(final @NonNull String key, final @NonNull Class<T> type) {
    return TypesafeMap.Key.getOrCreate(ImplantBlackboard.BLACKBOARD, key, type);
  }

  /* package */ ImplantBlackboard() {
    ImplantBlackboard.INSTANCE = this;
  }

  public <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key) {
    return this.getProperty(key, null);
  }

  public <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T defaultValue) {
    return ImplantBlackboard.BLACKBOARD.get(key).orElse(defaultValue);
  }

  public <T> @Nullable T setProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T value) {
    return ImplantBlackboard.BLACKBOARD.computeIfAbsent(key, k -> value);
  }
}

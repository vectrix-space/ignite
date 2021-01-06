package com.mineteria.implant.launch;

import com.google.common.reflect.TypeToken;
import cpw.mods.modlauncher.api.TypesafeMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class ImplantBlackboard {
  private static final TypesafeMap BLACKBOARD = new TypesafeMap();

  public static final TypesafeMap.Key<List<String>> LAUNCH_ARGUMENTS = key("implant.launch.arguments", new TypeToken<List<String>>() {});
  public static final TypesafeMap.Key<Path>         LAUNCH_JAR       = key("implant.launch.jar", TypeToken.of(Path.class));
  public static final TypesafeMap.Key<String>       LAUNCH_TARGET    = key("implant.launch.target", TypeToken.of(String.class));

  public static final TypesafeMap.Key<Path> MOD_DIRECTORY_PATH = key("implant.mod.directory", TypeToken.of(Path.class));
  public static final TypesafeMap.Key<Path> MOD_CONFIG_PATH    = key("implant.mod.config", TypeToken.of(Path.class));

  public static <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key) {
    return ImplantBlackboard.getProperty(key, null);
  }

  public static <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T defaultValue) {
    return ImplantBlackboard.BLACKBOARD.get(key).orElse(defaultValue);
  }

  public static <T> void setProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T value) {
    ImplantBlackboard.BLACKBOARD.computeIfAbsent(key, k -> value);
  }

  private static <T> TypesafeMap.Key<T> key(final @NonNull String key, final @NonNull TypeToken<T> type) {
    return TypesafeMap.Key.getOrCreate(ImplantBlackboard.BLACKBOARD, key, type.getRawType());
  }
}

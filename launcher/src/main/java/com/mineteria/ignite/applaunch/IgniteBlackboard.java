/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) Mineteria <https://mineteria.com/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mineteria.ignite.applaunch;

import com.google.common.reflect.TypeToken;
import cpw.mods.modlauncher.api.TypesafeMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class IgniteBlackboard {
  private static final TypesafeMap BLACKBOARD = new TypesafeMap();

  public static final TypesafeMap.@NonNull Key<List<String>> LAUNCH_ARGUMENTS = key("ignite.launch.arguments", new TypeToken<List<String>>() {});
  public static final TypesafeMap.@NonNull Key<Path>         LAUNCH_JAR       = key("ignite.launch.jar", TypeToken.of(Path.class));
  public static final TypesafeMap.@NonNull Key<String>       LAUNCH_TARGET    = key("ignite.launch.target", TypeToken.of(String.class));

  public static final TypesafeMap.@NonNull Key<Path> MOD_DIRECTORY_PATH = key("ignite.mod.directory", TypeToken.of(Path.class));
  public static final TypesafeMap.@NonNull Key<Path> CONFIG_DIRECTORY_PATH = key("ignite.config.directory", TypeToken.of(Path.class));

  public static <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key) {
    return IgniteBlackboard.getProperty(key, null);
  }

  public static <T> @Nullable T getProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T defaultValue) {
    return IgniteBlackboard.BLACKBOARD.get(key).orElse(defaultValue);
  }

  public static <T> void setProperty(final TypesafeMap.@NonNull Key<T> key, final @Nullable T value) {
    IgniteBlackboard.BLACKBOARD.computeIfAbsent(key, k -> value);
  }

  private static <T> TypesafeMap.@NonNull Key<T> key(final @NonNull String key, final @NonNull TypeToken<T> type) {
    return TypesafeMap.Key.getOrCreate(IgniteBlackboard.BLACKBOARD, key, type.getRawType());
  }
}

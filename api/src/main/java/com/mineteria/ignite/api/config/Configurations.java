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
package com.mineteria.ignite.api.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class Configurations {
  /**
   * Provides a function to make a general purpose {@link HoconConfigurationLoader}
   * using the input {@link ConfigurationKey}.
   */
  public static final @NonNull Function<ConfigurationKey, ConfigurationLoader<CommentedConfigurationNode>> HOCON_LOADER = key -> {
    final Path path = key.getPath();
    if (path == null) return null;

    try {
      Files.createDirectories(path.getParent());

      return HoconConfigurationLoader.builder()
        .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
        .setSink(() -> Files.newBufferedWriter(path,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE,
          StandardOpenOption.DSYNC)
        )
        .setDefaultOptions(ConfigurationOptions.defaults())
        .build();
    } catch (IOException exception) {
      throw new AssertionError("Unable to create configuration directory.", exception);
    }
  };

  private static final ConcurrentMap<ConfigurationKey, Configuration<?, ?>> CONFIGURATIONS = new ConcurrentHashMap<>();

  /**
   * Loads a new {@link Configuration} with the specified {@link ConfigurationLoader}, {@link Path}
   * and {@code T} instance.
   *
   * @param loaderSupplier The loader supplier
   * @param key The configuration key
   * @param instance The instance
   * @param <T> The instance type
   * @param <N> The node type
   * @return The configuration
   */
  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> load(final @NonNull Function<ConfigurationKey, ConfigurationLoader<N>> loaderSupplier, final ConfigurationKey key, final @NonNull T instance) {
    return Configurations.load(loaderSupplier.apply(key), key, instance);
  }

  /**
   * Loads a new {@link Configuration} with the specified {@link ConfigurationLoader}, {@link Path}
   * and {@code T} instance.
   *
   * @param loader The loader supplier
   * @param key The configuration key
   * @param instance The instance
   * @param <T> The instance type
   * @param <N> The node type
   * @return The configuration
   */
  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> load(final @Nullable ConfigurationLoader<N> loader, final @NonNull ConfigurationKey key, final @NonNull T instance) {
    return (Configuration<T, N>) Configurations.CONFIGURATIONS.computeIfAbsent(key, ignored -> {
      try {
        final Configuration<T, N> configuration = new Configuration<>(key, instance, loader);
        configuration.load();
        return configuration;
      } catch (final IOException | ObjectMappingException exception) {
        throw new AssertionError("Unable to load configuration.", exception);
      }
    });
  }
}

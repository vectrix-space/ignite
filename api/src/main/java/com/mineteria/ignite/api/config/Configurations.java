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
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class Configurations {
  /**
   * Provides a function to make a general purpose {@link GsonConfigurationLoader}
   * using the input {@link ConfigurationKey}.
   */
  public static final @NonNull Function<ConfigurationKey, ConfigurationLoader<ConfigurationNode>> GSON_LOADER = key -> Configurations.createLoader(key, path -> GsonConfigurationLoader.builder()
    .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
    .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, Configurations.SINK_OPTIONS))
    .setDefaultOptions(ConfigurationOptions.defaults())
    .build()
  );

  /**
   * Provides a function to make a general purpose {@link HoconConfigurationLoader}
   * using the input {@link ConfigurationKey}.
   */
  public static final @NonNull Function<ConfigurationKey, ConfigurationLoader<CommentedConfigurationNode>> HOCON_LOADER = key -> Configurations.createLoader(key, path -> HoconConfigurationLoader.builder()
    .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
    .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, Configurations.SINK_OPTIONS))
    .setDefaultOptions(ConfigurationOptions.defaults())
    .build()
  );

  /**
   * Provides a function to make a general purpose {@link YAMLConfigurationLoader}
   * using the input {@link ConfigurationKey}.
   */
  public static final @NonNull Function<ConfigurationKey, ConfigurationLoader<ConfigurationNode>> YAML_LOADER = key -> Configurations.createLoader(key, path -> YAMLConfigurationLoader.builder()
    .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
    .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, Configurations.SINK_OPTIONS))
    .setDefaultOptions(ConfigurationOptions.defaults())
    .build()
  );

  private static final ConcurrentMap<ConfigurationKey, Configuration<?, ?>> CONFIGURATIONS = new ConcurrentHashMap<>();
  private static final OpenOption[] SINK_OPTIONS = new OpenOption[] {
    StandardOpenOption.CREATE,
    StandardOpenOption.TRUNCATE_EXISTING,
    StandardOpenOption.WRITE,
    StandardOpenOption.DSYNC
  };

  /**
   * Creates a new virtual {@link Configuration} with the specified {@link ConfigurationKey}
   * and {@link Class} instance type.
   *
   * @param key The configuration key
   * @param instanceType The instance class
   * @param <T> The instance type
   * @param <N> The node type
   * @return The configuration
   */
  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> createVirtual(final @NonNull ConfigurationKey key, final @NonNull Class<T> instanceType) {
    return new Configuration<>(key, instanceType);
  }

  /**
   * Gets or creates a new {@link Configuration} with the specified {@link ConfigurationLoader},
   * {@link ConfigurationKey} and {@link Class} instance type.
   *
   * @param loader The loader supplier
   * @param key The configuration key
   * @param instanceType The instance class
   * @param <T> The instance type
   * @param <N> The node type
   * @return The configuration
   */
  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> getOrCreate(final @Nullable ConfigurationLoader<N> loader, final @NonNull ConfigurationKey key,
                                                                                          final @NonNull Class<T> instanceType) {
    return Configurations.loadConfiguration(ignored -> loader, key, instanceType);
  }

  /**
   * Gets or creates a new {@link Configuration} with the specified {@link ConfigurationLoader},
   * {@link ConfigurationKey} and {@link Class} instance type.
   *
   * @param loaderSupplier The loader supplier
   * @param key The configuration key
   * @param instanceType The instance class
   * @param <T> The instance type
   * @param <N> The node type
   * @return The configuration
   */
  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> getOrCreate(final @NonNull Function<ConfigurationKey, ConfigurationLoader<N>> loaderSupplier,
                                                                                          final @NonNull ConfigurationKey key, final @NonNull Class<T> instanceType) {
    return Configurations.loadConfiguration(loaderSupplier, key, instanceType);
  }

  private static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> loadConfiguration(final @NonNull Function<ConfigurationKey, ConfigurationLoader<N>> loaderSupplier,
                                                                                                 final @NonNull ConfigurationKey key, final @NonNull Class<T> instanceType) {
    return (Configuration<T, N>) Configurations.CONFIGURATIONS.computeIfAbsent(key, ignored -> {
      try {
        final Configuration<T, N> configuration = new Configuration<>(key, instanceType, loaderSupplier.apply(key));
        configuration.load();
        return configuration;
      } catch (final IOException | ObjectMappingException exception) {
        throw new AssertionError("Unable to load configuration.", exception);
      }
    });
  }

  private static <N extends ConfigurationNode, L extends AbstractConfigurationLoader<N>> L createLoader(final @NonNull ConfigurationKey key, final @NonNull Function<Path, L> loader) {
    final Path path = key.getPath();
    if (path == null) return null;

    try {
      Files.createDirectories(path.getParent());

      return loader.apply(path);
    } catch (final IOException exception) {
      throw new AssertionError("Unable to create configuration directory.", exception);
    }
  }
}

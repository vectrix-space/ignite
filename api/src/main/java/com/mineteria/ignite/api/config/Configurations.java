package com.mineteria.ignite.api.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Configurations {
  public static <T> @NonNull Configuration<T, CommentedConfigurationNode> loadHocon(final @NonNull Path path, final @NonNull ConfigurationOptions options, final @NonNull T instance) {
    try {
      Files.createDirectories(path.getParent());

      final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
        .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
        .setSink(() -> Files.newBufferedWriter(path,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE,
          StandardOpenOption.DSYNC)
        )
        .setDefaultOptions(options)
        .build();

      return Configurations.load(loader, instance);
    } catch (IOException exception) {
      throw new AssertionError("Unable to create configuration directory.", exception);
    }
  }

  public static <T, N extends ConfigurationNode> @NonNull Configuration<T, N> load(final @NonNull ConfigurationLoader<N> loader, final @NonNull T instance) {
    try {
      final Configuration<T, N> configuration = new Configuration<>(instance, loader);
      configuration.load();
      return configuration;
    } catch (final IOException | ObjectMappingException exception) {
      throw new AssertionError("Unable to load configuration.", exception);
    }
  }
}

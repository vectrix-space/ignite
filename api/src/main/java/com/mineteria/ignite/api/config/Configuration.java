package com.mineteria.ignite.api.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;

public final class Configuration<T, N extends ConfigurationNode> {
  private static <T> ObjectMapper<T> createMapper(final T instance) {
    try {
      return DefaultObjectMapperFactory.getInstance().getMapper((Class<T>) instance.getClass());
    } catch (final ObjectMappingException exception) {
      throw new AssertionError(exception);
    }
  }

  private final @Nullable ConfigurationLoader<N> loader;
  private final ObjectMapper<T> mapper;
  private final T instance;

  private @MonotonicNonNull N node;

  /* package */ Configuration(final @NonNull T instance) {
    this(instance, null);
  }

  /* package */ Configuration(final @NonNull T instance, final @Nullable ConfigurationLoader<N> loader) {
    this.mapper = Configuration.createMapper(instance);
    this.instance = instance;
    this.loader = loader;
  }

  public void load() throws IOException, ObjectMappingException {
    if (this.loader == null) return;

    this.node = this.loader.load();
    this.mapper.bind(this.instance).populate(this.node);
    this.save();
  }

  public void save() throws IOException, ObjectMappingException {
    if (this.loader == null) return;

    if (this.node == null) this.node = this.loader.createEmptyNode();

    this.mapper.bind(this.instance).serialize(this.node);
    this.loader.save(this.node);
  }

  public @NonNull T getInstance() {
    return this.instance;
  }

  public @MonotonicNonNull N getNode() {
    return this.node;
  }
}

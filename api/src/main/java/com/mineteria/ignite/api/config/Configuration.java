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
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * A configuration wrapper to assist in managing the configuration
 * loader and object mapper instance.
 *
 * @param <T> The object mapper instance type
 * @param <N> The configuration node type
 */
@SuppressWarnings("unchecked")
public final class Configuration<T, N extends ConfigurationNode> {
  private static <T> ObjectMapper<T> createMapper(final T instance) {
    try {
      return DefaultObjectMapperFactory.getInstance().getMapper((Class<T>) instance.getClass());
    } catch (final ObjectMappingException exception) {
      throw new AssertionError(exception);
    }
  }

  private final ConfigurationKey key;
  private final @Nullable ConfigurationLoader<N> loader;
  private final ObjectMapper<T> mapper;
  private final T instance;

  private @MonotonicNonNull N node;

  /* package */ Configuration(final @NonNull ConfigurationKey key, final @NonNull T instance) {
    this(key, instance, null);
  }

  /* package */ Configuration(final @NonNull ConfigurationKey key, final @NonNull T instance, final @Nullable ConfigurationLoader<N> loader) {
    this.mapper = Configuration.createMapper(instance);
    this.key = key;
    this.instance = instance;
    this.loader = loader;
  }

  /**
   * Loads the configuration from the {@link ConfigurationLoader} if
   * it exists.
   *
   * @throws IOException If the directory or file could not be created
   * @throws ObjectMappingException If the instance could not be populated
   */
  public void load() throws IOException, ObjectMappingException {
    if (this.loader == null) return;

    this.node = this.loader.load();
    this.mapper.bind(this.instance).populate(this.node);
    this.save();
  }

  /**
   * Saves the configuration to the {@link ConfigurationLoader} if
   * it exists.
   *
   * @throws IOException If the directory or file could not be created
   * @throws ObjectMappingException If the instance could not be serialized to
   */
  public void save() throws IOException, ObjectMappingException {
    if (this.loader == null) return;
    if (this.node == null) this.node = this.loader.createEmptyNode();

    this.mapper.bind(this.instance).serialize(this.node);
    this.loader.save(this.node);
  }

  /**
   * Returns the configuration key.
   *
   * @return The configuration key
   */
  public ConfigurationKey getKey() {
    return this.key;
  }

  /**
   * Returns the object mapper instance.
   *
   * @return The object mapper instance
   */
  public @NonNull T getInstance() {
    return this.instance;
  }

  /**
   * Returns the configuration node.
   *
   * @return The configuration node
   */
  public @MonotonicNonNull N getNode() {
    return this.node;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.loader, this.mapper, this.instance, this.node);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof Configuration)) return false;
    final Configuration<?, ?> that = (Configuration<?, ?>) other;
    return Objects.equals(this.loader, that.loader)
      && Objects.equals(this.mapper, that.mapper)
      && Objects.equals(this.instance, that.instance)
      && Objects.equals(this.node, that.node);
  }

  @Override
  public String toString() {
    return "Configuration{loader=" + this.loader +
      ", mapper=" + this.mapper +
      ", instance=" + this.instance +
      ", node=" + this.node +
      "}";
  }
}

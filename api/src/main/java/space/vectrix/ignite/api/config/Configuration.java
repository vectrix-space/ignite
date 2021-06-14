/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
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
package space.vectrix.ignite.api.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * A configuration wrapper to assist in managing the configuration loader and
 * object mapper instance.
 *
 * @param <T> the object mapper instance type
 * @param <N> the configuration node type
 * @since 0.5.0
 */
public final class Configuration<T, N extends ConfigurationNode> {
  private static <T> ObjectMapper<T> createMapper(final Class<T> instanceType) {
    try {
      return ObjectMapper.forClass(instanceType);
    } catch (final ObjectMappingException exception) {
      throw new AssertionError(exception);
    }
  }

  private final @Nullable ConfigurationLoader<N> loader;
  private final ObjectMapper<T> mapper;
  private final ConfigurationKey key;

  private ObjectMapper<T>.@MonotonicNonNull BoundInstance instance;
  private @MonotonicNonNull N node;

  /* package */ Configuration(final @NonNull ConfigurationKey key, final @NonNull Class<T> instanceType) {
    this(key, instanceType, null);
  }

  /* package */ Configuration(final @NonNull ConfigurationKey key, final @NonNull Class<T> instanceType, final @Nullable ConfigurationLoader<N> loader) {
    this.mapper = Configuration.createMapper(instanceType);
    this.key = key;
    this.loader = loader;
  }

  /**
   * Loads the configuration from the {@link ConfigurationLoader} if
   * it exists.
   *
   * @throws IOException if the directory or file could not be created
   * @throws ObjectMappingException if the instance could not be populated
   * @since 0.5.0
   */
  public void load() throws IOException, ObjectMappingException {
    if (this.loader == null) return;
    if (this.instance == null) this.instance = this.mapper.bindToNew();

    this.node = this.loader.load();
    this.instance.populate(this.node);
    this.save();
  }

  /**
   * Saves the configuration to the {@link ConfigurationLoader} if
   * it exists.
   *
   * @throws IOException if the directory or file could not be created
   * @throws ObjectMappingException if the instance could not be serialized to
   * @since 0.5.0
   */
  public void save() throws IOException, ObjectMappingException {
    if (this.loader == null) return;
    if (this.node == null) this.node = this.loader.createEmptyNode();
    if (this.instance != null) {
      this.instance.serialize(this.node);
    }

    this.loader.save(this.node);
  }

  /**
   * Returns the configuration key.
   *
   * @return the configuration key
   * @since 0.5.0
   */
  public @NonNull ConfigurationKey getKey() {
    return this.key;
  }

  /**
   * Returns the object mapper instance.
   *
   * @return the object mapper instance
   * @since 0.5.0
   */
  public @MonotonicNonNull T getInstance() {
    return this.instance != null ? this.instance.getInstance() : null;
  }

  /**
   * Returns the configuration node.
   *
   * @return the configuration node
   * @since 0.5.0
   */
  public @MonotonicNonNull N getNode() {
    return this.node;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.loader, this.mapper, this.key, this.instance, this.node);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof Configuration)) return false;
    final Configuration<?, ?> that = (Configuration<?, ?>) other;
    return Objects.equals(this.loader, that.loader)
      && Objects.equals(this.mapper, that.mapper)
      && Objects.equals(this.key, that.key)
      && Objects.equals(this.instance, that.instance)
      && Objects.equals(this.node, that.node);
  }

  @Override
  public String toString() {
    return "Configuration{loader=" + this.loader +
      ", mapper=" + this.mapper +
      ", key=" + this.key +
      ", instance=" + this.instance +
      ", node=" + this.node +
      "}";
  }
}

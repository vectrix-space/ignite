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

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A key to identify a specific {@link Configuration}.
 */
public final class ConfigurationKey {
  public static @NonNull ConfigurationKey key(final @NonNull String id, final @NonNull Path path) {
    return new ConfigurationKey(id, path);
  }

  private final String id;
  private final Path path;

  /* package */ ConfigurationKey(final @NonNull String id, final @Nullable Path path) {
    this.id = id;
    this.path = path;
  }

  /**
   * The configuration identifier.
   *
   * @return The configuration identifier
   */
  public @NonNull String getId() {
    return this.id;
  }

  /**
   * The configuration path.
   *
   * @return The configuration path
   */
  public @MonotonicNonNull Path getPath() {
    return this.path;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.path);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof ConfigurationKey)) return false;
    final ConfigurationKey that = (ConfigurationKey) other;
    return Objects.equals(this.id, that.id)
      && Objects.equals(this.path, that.path);
  }

  @Override
  public String toString() {
    return "ConfigurationKey{id=" + this.id +
      ", path=" + this.path +
      "}";
  }
}

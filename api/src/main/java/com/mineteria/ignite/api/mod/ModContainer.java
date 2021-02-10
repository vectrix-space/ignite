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
package com.mineteria.ignite.api.mod;

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class ModContainer {
  private final Logger logger;
  private final ModResource resource;
  private final ModConfig config;

  public ModContainer(final @NonNull Logger logger,
                      final @NonNull ModResource resource,
                      final @NonNull ModConfig config) {
    this.logger = logger;
    this.resource = resource;
    this.config = config;
  }

  public @NonNull Logger getLogger() {
    return this.logger;
  }

  public @NonNull String getId() {
    return this.config.getId();
  }

  public @NonNull String getVersion() {
    return this.config.getVersion();
  }

  public @NonNull ModResource getResource() {
    return this.resource;
  }

  public @NonNull ModConfig getConfig() {
    return this.config;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.resource, this.config);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof ModContainer)) return false;
    final ModContainer that = (ModContainer) other;
    return Objects.equals(this.resource, that.resource)
      && Objects.equals(this.config, that.config);
  }

  @Override
  public @NonNull String toString() {
    return this.getId() + "@" + this.getVersion();
  }
}

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
package space.vectrix.ignite.mod;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.TaggedLogger;

/**
 * Represents a mod container.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class ModContainerImpl implements ModContainer {
  private final TaggedLogger logger;
  private final ModResource resource;
  private final ModConfig config;

  /* package */ ModContainerImpl(final @NotNull TaggedLogger logger,
                                 final @NotNull ModResource resource,
                                 final @NotNull ModConfig config) {
    this.logger = logger;
    this.resource = resource;
    this.config = config;
  }

  @Override
  public @NotNull TaggedLogger logger() {
    return this.logger;
  }

  @Override
  public @NotNull String id() {
    return this.config.id();
  }

  @Override
  public @NotNull String version() {
    return this.config.version();
  }

  @Override
  public @NotNull ModResource resource() {
    return this.resource;
  }

  /**
   * Returns the mod config.
   *
   * @return the config
   * @since 1.0.0
   */
  public @NotNull ModConfig config() {
    return this.config;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.resource, this.config);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof ModContainerImpl)) return false;
    final ModContainerImpl that = (ModContainerImpl) other;
    return Objects.equals(this.resource, that.resource)
      && Objects.equals(this.config, that.config);
  }

  @Override
  public String toString() {
    return "ModContainerImpl(id=" + this.id() + ", version=" + this.version() + ", resource=" + this.resource() + ", config=" + this.config() + ")";
  }
}

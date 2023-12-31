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

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a mod config.
 *
 * @since 1.0.0
 */
public final class ModConfig {
  private @SerializedName("id") String id;
  private @SerializedName("version") String version;
  private @SerializedName("mixins") List<String> mixins;
  private @SerializedName("wideners") List<String> wideners;

  /**
   * Creates a new mod config.
   *
   * @since 1.0.0
   */
  public ModConfig() {
  }

  /**
   * Creates a new mod config.
   *
   * @param id the id
   * @param version the version
   * @since 1.0.0
   */
  public ModConfig(final @NotNull String id,
                   final @NotNull String version) {
    this.id = id;
    this.version = version;
  }

  /**
   * Creates a new mod config.
   *
   * @param id the id
   * @param version the version
   * @param mixins the mixins
   * @param wideners the wideners
   * @since 1.0.0
   */
  public ModConfig(final @NotNull String id,
                   final @NotNull String version,
                   final @NotNull List<String> mixins,
                   final @NotNull List<String> wideners) {
    this.id = id;
    this.version = version;
    this.mixins = mixins;
    this.wideners = wideners;
  }

  /**
   * Returns the mod id.
   *
   * @return the id
   * @since 1.0.0
   */
  public @NotNull String id() {
    return this.id;
  }

  /**
   * Returns the mod version.
   *
   * @return the version
   * @since 1.0.0
   */
  public @NotNull String version() {
    return this.version;
  }

  /**
   * Returns the list of mod mixins.
   *
   * @return the mod mixins
   * @since 1.0.0
   */
  public @Nullable List<String> mixins() {
    return this.mixins;
  }

  /**
   * Returns the list of mod wideners.
   *
   * @return the mod wideners
   * @since 1.0.0
   */
  public @Nullable List<String> wideners() {
    return this.wideners;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.version, this.mixins);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof ModConfig)) return false;
    final ModConfig that = (ModConfig) other;
    return Objects.equals(this.id, that.id)
      && Objects.equals(this.version, that.version)
      && Objects.equals(this.mixins, that.mixins)
      && Objects.equals(this.wideners, that.wideners);
  }

  @Override
  public String toString() {
    return "ModConfig(" +
      "id=" + this.id + ", " +
      "version=" + this.version + ", " +
      "mixins=" + Arrays.toString(this.mixins.toArray(new String[0])) + ", " +
      "wideners=" + Arrays.toString(this.wideners.toArray(new String[0])) + ")";
  }
}

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

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.Manifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a mod resource that may not be resolved.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class ModResourceImpl implements ModResource {
  private final String locator;
  private final Path path;
  private final Manifest manifest;

  private FileSystem fileSystem;

  /* package */ ModResourceImpl(final @NotNull String locator,
                                final @NotNull Path path,
                                final @UnknownNullability Manifest manifest) {
    this.locator = locator;
    this.path = path;
    this.manifest = manifest;
  }

  @Override
  public @NotNull String locator() {
    return this.locator;
  }

  @Override
  public @NotNull Path path() {
    return this.path;
  }

  @Override
  public @UnknownNullability Manifest manifest() {
    return this.manifest;
  }

  @Override
  public @NotNull FileSystem fileSystem() {
    if(this.fileSystem == null) {
      try {
        this.fileSystem = FileSystems.newFileSystem(this.path(), this.getClass().getClassLoader());
      } catch(final IOException exception) {
        throw new RuntimeException(exception);
      }
    }

    return this.fileSystem;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.locator, this.path, this.manifest);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof ModResourceImpl)) return false;
    final ModResourceImpl that = (ModResourceImpl) other;
    return Objects.equals(this.locator, that.locator)
      && Objects.equals(this.path, that.path)
      && Objects.equals(this.manifest, that.manifest);
  }

  @Override
  public String toString() {
    return "ModResourceImpl{locator='" + this.locator + ", path=" + this.path + ", manifest=" + this.manifest + "}";
  }
}

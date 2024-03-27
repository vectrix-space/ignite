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

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.Manifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a mod resource that may not be resolved.
 *
 * @author Flowey
 * @since 1.0.2
 */
public final class ModClassPathResourceImpl implements ModResource {
  private final String locator;

  /* package */ ModClassPathResourceImpl(final @NotNull String locator) {
    this.locator = locator;
  }

  @Override
  public @NotNull String locator() {
    return this.locator;
  }

  @Override
  public @Nullable Path path() {
    return null;
  }

  @Override
  public @Nullable Manifest manifest() {
    return null;
  }

  @Override
  public @Nullable InputStream loadResource(final String path) {
    return ClassLoader.getSystemClassLoader().getResourceAsStream(path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.locator);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof ModClassPathResourceImpl)) return false;
    final ModClassPathResourceImpl that = (ModClassPathResourceImpl) other;
    return Objects.equals(this.locator, that.locator);
  }

  @Override
  public String toString() {
    return "ModClassPathResourceImpl{locator='" + this.locator + "}";
  }
}

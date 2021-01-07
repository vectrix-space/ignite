/*
 * This file is part of Implant, licensed under the MIT License (MIT).
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
package com.mineteria.implant.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Optional;

public final class ClassLoaderUtil {
  public static @NonNull Optional<URL> toUrl(final @NonNull Path path) {
    try {
      return Optional.of(path.toUri().toURL());
    } catch (final MalformedURLException exception) {
      return Optional.empty();
    }
  }

  /**
   * This is an ugly hack, I know.
   */
  public static void loadJar(final @NonNull ClassLoader classLoader, final @NonNull URL url) {
    try {
      try {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
        return;
      } catch (final NoSuchMethodException ignored) {
        // No-op
      }

      try {
        Method method = ClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
        return;
      } catch (final NoSuchMethodException ignored) {
        // No-op
      }

      try {
        Method method = ClassLoader.class.getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
        method.setAccessible(true);
        method.invoke(classLoader, url.getPath());
        return;
      } catch (final NoSuchMethodException ignored) {
        // No-op
      }
    } catch (final Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}

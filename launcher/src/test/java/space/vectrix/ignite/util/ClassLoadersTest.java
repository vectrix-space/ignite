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
package space.vectrix.ignite.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClassLoadersTest {
  @Test
  void systemClassPathsMatchesJavaClassPathProperty() {
    final URL[] expected = this.classPathPropertyUrls();
    final URL[] actual = ClassLoaders.systemClassPaths();

    assertArrayEquals(expected, actual);
  }

  @Test
  void systemClassPathsMatchesLegacyUnsafeLookupOnJdkInternalClassLoader() throws Exception {
    final ClassLoader classLoader = ClassLoaders.class.getClassLoader();
    if(classLoader instanceof URLClassLoader || !classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
      return;
    }

    final URL[] expected = this.legacyUnsafeSystemClassPaths(classLoader);
    final URL[] actual = ClassLoaders.systemClassPaths();

    assertArrayEquals(expected, actual);
  }

  @Test
  void javaClassPathPropertyExpansionIsStable() {
    final String classPath = System.getProperty("java.class.path", "");
    final String separator = System.getProperty("path.separator", File.pathSeparator);

    final long nonEmptyEntries = classPath.isEmpty()
      ? 0
      : java.util.regex.Pattern.compile(java.util.regex.Pattern.quote(separator))
        .splitAsStream(classPath)
        .filter(entry -> !entry.isEmpty())
        .count();

    assertEquals(nonEmptyEntries, ClassLoaders.systemClassPaths().length);
  }

  private URL[] classPathPropertyUrls() {
    final String classPath = System.getProperty("java.class.path", "");
    if(classPath.isEmpty()) {
      return new URL[0];
    }

    final String separator = System.getProperty("path.separator", File.pathSeparator);
    final List<URL> urls = new ArrayList<>();
    for(final String entry : classPath.split(java.util.regex.Pattern.quote(separator))) {
      if(entry.isEmpty()) {
        continue;
      }

      final Path path = Paths.get(entry);
      try {
        urls.add(path.toUri().toURL());
      } catch(final Exception exception) {
        throw new AssertionError("Failed to convert class path entry to URL: " + entry, exception);
      }
    }

    return urls.toArray(new URL[0]);
  }

  @SuppressWarnings({"restriction", "unchecked"})
  private URL[] legacyUnsafeSystemClassPaths(final ClassLoader classLoader) throws Exception {
    final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
    final Method objectFieldOffsetMethod = unsafeClass.getDeclaredMethod("objectFieldOffset", Field.class);
    final Method getObjectMethod = unsafeClass.getDeclaredMethod("getObject", Object.class, long.class);

    final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
    unsafeField.setAccessible(true);
    final Object unsafe = unsafeField.get(null);

    Field ucpField;
    try {
      ucpField = classLoader.getClass().getDeclaredField("ucp");
    } catch(final NoSuchFieldException | SecurityException exception) {
      ucpField = classLoader.getClass().getSuperclass().getDeclaredField("ucp");
    }

    final long ucpFieldOffset = (long) objectFieldOffsetMethod.invoke(unsafe, ucpField);
    final Object ucpObject = getObjectMethod.invoke(unsafe, classLoader, ucpFieldOffset);

    final Field pathField = ucpField.getType().getDeclaredField("path");
    final long pathFieldOffset = (long) objectFieldOffsetMethod.invoke(unsafe, pathField);
    final ArrayList<URL> path = (ArrayList<URL>) getObjectMethod.invoke(unsafe, ucpObject, pathFieldOffset);
    return path.toArray(new URL[0]);
  }
}

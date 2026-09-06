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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

/**
 * Taken from <a href="https://github.com/cpw/grossjava9hacks/blob/1.3/src/main/java/cpw/mods/gross/Java9ClassLoaderUtil.java">grossjava9hacks</a>.
 *
 * @author cpw
 * @since 1.0.0
 */
public final class ClassLoaders {
  /**
   * Returns the system class path {@link URL}s.
   *
   * @return the system class path urls
   * @since 1.0.0
   */
  public static URL@NotNull [] systemClassPaths() {
    final ClassLoader classLoader = ClassLoaders.class.getClassLoader();
    if(classLoader instanceof URLClassLoader) {
      return ((URLClassLoader) classLoader).getURLs();
    }

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

      try {
        final Path path = Paths.get(entry);
        urls.add(path.toUri().toURL());
      } catch(final MalformedURLException exception) {
        Logger.warn(exception, "Skipping malformed class path entry: {}", entry);
      }
    }

    return urls.toArray(new URL[0]);
  }

  private ClassLoaders() {
  }
}

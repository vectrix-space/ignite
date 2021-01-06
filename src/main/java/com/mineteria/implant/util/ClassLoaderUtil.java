package com.mineteria.implant.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Optional;

public final class ClassLoaderUtil {
  public static Optional<URL> toUrl(final Path path) {
    try {
      return Optional.of(path.toUri().toURL());
    } catch (final MalformedURLException exception) {
      return Optional.empty();
    }
  }

  public static void loadJar(final ClassLoader classLoader, final URL target) {
    URLClassLoader.newInstance(new URL[] { target }, classLoader);
  }
}

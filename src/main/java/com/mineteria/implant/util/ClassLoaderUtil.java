package com.mineteria.implant.util;

import org.checkerframework.checker.nullness.qual.NonNull;

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

  public static void loadJar(final @NonNull ClassLoader classLoader, final @NonNull URL target) {
    URLClassLoader.newInstance(new URL[] { target }, classLoader);
  }
}

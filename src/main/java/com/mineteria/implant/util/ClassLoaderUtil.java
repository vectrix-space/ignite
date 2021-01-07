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

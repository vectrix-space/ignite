package com.mineteria.ignite.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.applaunch.IgniteBootstrap;
import com.mineteria.ignite.applaunch.mod.ModEngine;
import com.mineteria.ignite.launch.inject.IgniteModule;
import com.mineteria.ignite.launch.mod.IgniteModManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Files;

public final class IgniteLaunch {
  private static IgniteLaunch instance;

  public static IgniteLaunch getInstance() {
    return IgniteLaunch.instance;
  }

  public static void main(final @NonNull ModEngine engine, final @NonNull String[] args) {
    new IgniteLaunch().launch(engine, args);
  }

  private final Injector injector;

  public IgniteLaunch() {
    IgniteLaunch.instance = this;
    this.injector = Guice.createInjector(new IgniteModule());
  }

  public void launch(final @NonNull ModEngine engine, final @NonNull String[] args) {
    // Platform
    final IgnitePlatform platform = (IgnitePlatform) this.injector.getInstance(Platform.class);
    ((IgniteModManager) platform.getModManager()).loadPlugins(engine);

    // Launch
    try {
      if (!Files.exists(IgniteBootstrap.LAUNCH_JAR)) {
        throw new IllegalStateException("No launch jar was found!");
      } else {
        // Invoke the main method on the provided ClassLoader.
        Class.forName(IgniteBootstrap.LAUNCH_TARGET, true, IgniteLaunch.class.getClassLoader())
          .getMethod("main", String[].class)
          .invoke(null, (Object) args);
      }
    } catch (final Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  public Injector getInjector() {
    return this.injector;
  }
}

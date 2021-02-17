package com.mineteria.example;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.path.ConfigPath;
import com.mineteria.ignite.api.event.Subscribe;
import com.mineteria.ignite.api.event.platform.PlatformInitializeEvent;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public final class ExampleMod {
  private final Logger logger;
  private final Platform platform;
  private final Path configurationPath;

  @Inject
  public ExampleMod(final Logger logger,
                    final Platform platform,
                    final @ConfigPath Path configurationPath) {
    this.logger = logger;
    this.platform = platform;
    this.configurationPath = configurationPath;
  }

  @Subscribe
  public void onInitialize(final @NonNull PlatformInitializeEvent event) {
    this.logger.info("Hello Initialization!");
  }
}

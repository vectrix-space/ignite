package com.mineteria.example;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.ModConfig;
import com.mineteria.ignite.api.event.Subscribe;
import com.mineteria.ignite.api.event.platform.PlatformInitializeEvent;

import java.nio.file.Path;

public final class ExampleMod {
  private final Platform platform;
  private final Path configuration;

  @Inject
  public ExampleMod(final Platform platform,
                    final @ModConfig Path configuration) {
    this.platform = platform;
    this.configuration = configuration;
  }

  @Subscribe
  public void onInitialize(final PlatformInitializeEvent event) {

  }
}

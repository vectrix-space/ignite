package com.mineteria.ignite.launch;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.Configs;
import com.mineteria.ignite.api.config.Mods;
import com.mineteria.ignite.api.event.EventManager;
import com.mineteria.ignite.api.mod.ModManager;
import com.mineteria.ignite.launch.event.IgniteEventManager;
import com.mineteria.ignite.launch.mod.IgniteModManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public final class IgnitePlatform implements Platform {
  private final Logger logger = LogManager.getLogger("IgnitePlatform");

  private final ModManager modManager;
  private final EventManager eventManager;
  private final Path configs;
  private final Path mods;

  @Inject
  public IgnitePlatform(final @NonNull @Configs Path configs,
                        final @NonNull @Mods Path mods) {
    this.mods = mods;
    this.configs = configs;

    this.modManager = new IgniteModManager(this);
    this.eventManager = new IgniteEventManager(this);
  }

  public Logger getLogger() {
    return this.logger;
  }

  @Override
  public final @NonNull ModManager getModManager() {
    return this.modManager;
  }

  @Override
  public final @NonNull EventManager getEventManager() {
    return this.eventManager;
  }

  @Override
  public final @NonNull Path getConfigs() {
    return this.configs;
  }

  @Override
  public final @NonNull Path getMods() {
    return this.mods;
  }
}

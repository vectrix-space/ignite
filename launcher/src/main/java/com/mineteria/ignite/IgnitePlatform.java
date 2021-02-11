package com.mineteria.ignite;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.Configs;
import com.mineteria.ignite.api.config.Mods;
import com.mineteria.ignite.api.event.EventManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public final class IgnitePlatform implements Platform {
  private final IgniteEngine engine;
  private final Path configs;
  private final Path mods;

  @Inject
  public IgnitePlatform(final @NonNull IgniteEngine engine,
                        final @NonNull @Configs Path configs,
                        final @NonNull @Mods Path mods) {
    this.engine = engine;
    this.mods = mods;
    this.configs = configs;
  }

  @Override
  public final @NonNull EventManager getEventManager() {
    return this.engine.getEventManager();
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

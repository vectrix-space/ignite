package com.mineteria.ignite.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.mineteria.ignite.IgniteBootstrap;
import com.mineteria.ignite.IgniteEngine;
import com.mineteria.ignite.IgnitePlatform;
import com.mineteria.ignite.api.Ignite;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.Configs;
import com.mineteria.ignite.api.config.Mods;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public final class IgniteModule extends AbstractModule {
  private final IgniteEngine engine;

  public IgniteModule(final IgniteEngine engine) {
    this.engine = engine;
  }

  @Override
  protected void configure() {
    this.requestStaticInjection(Ignite.class);

    this.bind(IgniteEngine.class).toInstance(this.engine);

    this.bind(Platform.class).to(IgnitePlatform.class).in(Scopes.SINGLETON);

    this.bind(Path.class)
      .annotatedWith(Mods.class)
      .toProvider(ModsPath.class)
      .in(Scopes.SINGLETON);

    this.bind(Path.class)
      .annotatedWith(Configs.class)
      .toProvider(ConfigsPath.class)
      .in(Scopes.SINGLETON);
  }

  /* package */ static final class ModsPath implements Provider<Path> {
    @Override
    public final @NonNull Path get() {
      return IgniteBootstrap.MOD_TARGET_PATH;
    }
  }

  /* package */ static final class ConfigsPath implements Provider<Path> {
    @Override
    public final @NonNull Path get() {
      return IgniteBootstrap.CONFIG_TARGET_PATH;
    }
  }
}

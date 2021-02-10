package com.mineteria.ignite.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.mineteria.ignite.IgnitePlatform;
import com.mineteria.ignite.api.Ignite;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.Configs;
import com.mineteria.ignite.api.config.Mods;
import com.mineteria.ignite.launch.IgniteBlackboard;

import java.nio.file.Path;

public final class IgniteModule extends AbstractModule {
  public IgniteModule() {}

  @Override
  protected void configure() {
    this.requestStaticInjection(Ignite.class);

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
    public Path get() {
      return IgniteBlackboard.getProperty(IgniteBlackboard.MOD_DIRECTORY_PATH);
    }
  }

  /* package */ static final class ConfigsPath implements Provider<Path> {
    @Override
    public Path get() {
      return IgniteBlackboard.getProperty(IgniteBlackboard.CONFIG_DIRECTORY_PATH);
    }
  }
}

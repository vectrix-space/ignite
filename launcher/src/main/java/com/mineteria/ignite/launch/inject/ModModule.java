package com.mineteria.ignite.launch.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.mineteria.ignite.api.config.Config;
import com.mineteria.ignite.api.config.Configs;
import com.mineteria.ignite.api.mod.ModContainer;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public final class ModModule extends AbstractModule {
  private final ModContainer container;
  private final Class<?> target;

  public ModModule(final @NonNull ModContainer container, final @NonNull Class<?> target) {
    this.container = container;
    this.target = target;
  }

  @Override
  protected void configure() {
    this.bind(this.target).in(Scopes.SINGLETON);

    this.bind(ModContainer.class).toInstance(this.container);
    this.bind(Logger.class).toInstance(this.container.getLogger());

    this.bind(Path.class)
      .annotatedWith(Config.class)
      .toProvider(ModConfigPath.class)
      .in(Scopes.SINGLETON);
  }

  /* package */ static final class ModConfigPath implements Provider<Path> {
    private final Path configs;
    private final ModContainer container;

    @Inject
    public ModConfigPath(final @NonNull @Configs Path configs, final @NonNull ModContainer container) {
      this.configs = configs;
      this.container = container;
    }

    @Override
    public final @NonNull Path get() {
      return this.configs.resolve(this.container.getId());
    }
  }
}

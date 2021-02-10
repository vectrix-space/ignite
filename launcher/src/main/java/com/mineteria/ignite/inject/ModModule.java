package com.mineteria.ignite.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.mineteria.ignite.api.mod.ModContainer;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

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
  }
}

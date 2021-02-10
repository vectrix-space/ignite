package com.mineteria.ignite;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.event.EventManager;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class IgnitePlatform implements Platform {
  private final IgniteEngine engine;

  @Inject
  public IgnitePlatform(final @NonNull IgniteEngine engine) {
    this.engine = engine;
  }

  @Override
  public EventManager getEventManager() {
    return this.engine.getEventManager();
  }
}

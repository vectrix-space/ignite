package com.mineteria.ignite.api;

import com.google.inject.Inject;
import com.mineteria.ignite.api.event.EventManager;

public final class Ignite {
  @Inject private static Platform platform;

  public static Platform getPlatform() {
    if (Ignite.platform == null) throw new IllegalStateException("Ignite has not been initialized yet!");
    return Ignite.platform;
  }

  public static EventManager getEventManager() {
    return Ignite.getPlatform().getEventManager();
  }
}

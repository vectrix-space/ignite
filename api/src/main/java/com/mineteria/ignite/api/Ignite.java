package com.mineteria.ignite.api;

import com.google.inject.Inject;
import com.mineteria.ignite.api.event.EventManager;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Provides static access to the core functions of Ignite.
 */
public final class Ignite {
  @Inject private static Platform platform;

  /**
   * Returns the {@link Platform}, if it is initialized.
   *
   * @return The platform
   */
  public static @NonNull Platform getPlatform() {
    if (Ignite.platform == null) throw new IllegalStateException("Ignite has not been initialized yet!");
    return Ignite.platform;
  }

  /**
   * Returns the {@link EventManager}, if it is initialized.
   *
   * @return The event manager
   */
  public static @NonNull EventManager getEventManager() {
    return Ignite.getPlatform().getEventManager();
  }
}

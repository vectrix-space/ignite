package com.mineteria.ignite.api;

import com.mineteria.ignite.api.event.EventManager;
import com.mineteria.ignite.api.mod.ModManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

/**
 * Represents the platform Ignite is starting on, to be used
 * by mods.
 */
public interface Platform {
  /**
   * Returns the {@link ModManager} mod manager.
   *
   * @return The mod manager
   */
  @NonNull ModManager getModManager();

  /**
   * Returns the {@link EventManager} event manager.
   *
   * @return The event manager
   */
  @NonNull EventManager getEventManager();

  /**
   * Returns the {@link Path} configurations directory.
   *
   * @return The configurations directory
   */
  @NonNull Path getConfigs();

  /**
   * Return the {@link Path} mods directory.
   *
   * @return The mods directory
   */
  @NonNull Path getMods();
}

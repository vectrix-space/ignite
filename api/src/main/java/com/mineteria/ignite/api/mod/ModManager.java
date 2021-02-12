package com.mineteria.ignite.api.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Provides access to manage mods.
 */
public interface ModManager {
  /**
   * Returns the {@link ModContainer} for the specified {@link String}
   * identifier, if it exists.
   *
   * @param mod The mod identifier
   * @return The mod container
   */
  @NonNull Optional<ModContainer> getContainer(final @NonNull String mod);

  /**
   * Returns the {@link ModContainer} for the specified {@link Object}
   * mod instance, if it exists.
   *
   * @param modInstance The mod instance
   * @return The mod container
   */
  @NonNull Optional<ModContainer> getContainer(final @NonNull Object modInstance);

  /**
   * Returns {@code true} if a mod with the specified {@link String}
   * mod identifier if loaded.
   *
   * @param mod The mod identifier
   * @return True if the mod is loaded, otherwise false
   */
  boolean isLoaded(final @NonNull String mod);

  /**
   * Returns {@code true} if the specified {@link Object} is a mod
   * instance.
   *
   * @param modInstance The possibly mod instance
   * @return True if the object is a mod instance, otherwise false
   */
  boolean isInstance(final @NonNull Object modInstance);

  /**
   * Returns a {@link Collection} of {@link ModContainer}s that have
   * been loaded.
   *
   * @return A collection of loaded mod containers
   */
  @NonNull Collection<ModContainer> getContainers();
}

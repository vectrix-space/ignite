package com.mineteria.ignite.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an interface to perform a direct dispatch of an event.
 *
 * @param <E> The event type
 */
@FunctionalInterface
public interface EventHandler<E> {
  /**
   * Called when an event is dispatched.
   *
   * @param event The event
   */
  void execute(final @NonNull E event);
}

package com.mineteria.ignite.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface EventManager {
  void register(final @NonNull Object mod, final @NonNull Object listener);

  default <E> void register(final @NonNull Object mod, final @NonNull Class<E> event, final @NonNull EventHandler<E> handler) {
    this.register(mod, event, SubscribePriority.NORMAL, handler);
  }

  <E> void register(final @NonNull Object mod, final @NonNull Class<E> event, final @NonNull SubscribePriority priority, final @NonNull EventHandler<E> handler);

  void unregister(final @NonNull Object mod);

  void unregister(final @NonNull Object mod, final @NonNull Object listener);

  <E> void unregister(final @NonNull Object mod, final @NonNull EventHandler<E> handler);

  void post(final @NonNull Object event);
}

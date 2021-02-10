package com.mineteria.ignite.api.event;

@FunctionalInterface
public interface EventHandler<E> {
  void execute(E event);
}

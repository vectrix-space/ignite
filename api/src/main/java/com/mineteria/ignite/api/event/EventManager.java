/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) Mineteria <https://mineteria.com/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mineteria.ignite.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Provides access to manage event listeners for event handlers.
 */
public interface EventManager {
  /**
   * Registers the specified {@link Object} listener for the specified
   * {@link Object} mod instance.
   *
   * @param mod The mod instance
   * @param listener The event listener
   */
  void register(final @NonNull Object mod, final @NonNull Object listener);

  /**
   * Registers the specified {@link EventHandler} handler and {@link Class} event,
   * for the specified {@link Object} mod instance.
   *
   * @param mod The mod instance
   * @param event The event class
   * @param handler The event handler
   * @param <E> The event type
   */
  default <E> void register(final @NonNull Object mod, final @NonNull Class<E> event, final @NonNull EventHandler<E> handler) {
    this.register(mod, event, PostPriority.NORMAL, handler);
  }

  /**
   * Registers the specified {@link EventHandler} handler and {@link Class} event,
   * under the {@link PostPriority}, for the specified {@link Object} mod instance.
   *
   * @param mod The mod instance
   * @param event The event class
   * @param priority The post priority
   * @param handler The event handler
   * @param <E> The event type
   */
  <E> void register(final @NonNull Object mod, final @NonNull Class<E> event, final @NonNull PostPriority priority, final @NonNull EventHandler<E> handler);

  /**
   * Unregisters all listeners for the specified {@link Object} mod instance.
   *
   * @param mod The mod instance
   */
  void unregister(final @NonNull Object mod);

  /**
   * Unregisters the specified {@link Object} listener, for the specified
   * {@link Object} mod instance.
   *
   * @param mod The mod instance
   * @param listener The event listener
   */
  void unregister(final @NonNull Object mod, final @NonNull Object listener);

  /**
   * Unregisters the specified {@link EventHandler} listener, for the specified
   * {@link Object} mod instance.
   *
   * @param mod The mod instance
   * @param handler The event handler
   * @param <E> The event type
   */
  <E> void unregister(final @NonNull Object mod, final @NonNull EventHandler<E> handler);

  /**
   * Posts the specified {@link Object} event to the event bus.
   *
   * <p>Any errors thrown from handling events will be handled.</p>
   *
   * @param event The event
   */
  void post(final @NonNull Object event);
}

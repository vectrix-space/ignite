/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
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
package space.vectrix.ignite.game;

import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.IgniteBootstrap;

/**
 * Represents a game locator service.
 *
 * @author vectrix
 * @since 1.0.0
 */
public interface GameLocatorService {
  /**
   * The game locator identifier.
   *
   * @return the identifier
   * @since 1.0.0
   */
  @NotNull String id();

  /**
   * The game locator name.
   *
   * @return the name
   * @since 1.0.0
   */
  @NotNull String name();

  /**
   * Returns {@code true} if this locator should be used, otherwise returns
   * {@code false}.
   *
   * @return whether this locator should be used
   * @since 1.0.0
   */
  boolean shouldApply();

  /**
   * Applies this game locator.
   *
   * @param bootstrap the bootstrap
   * @throws Throwable if there is a problem applying the locator
   * @since 1.0.0
   */
  void apply(final @NotNull IgniteBootstrap bootstrap) throws Throwable;

  /**
   * Returns the game resource provider.
   *
   * @return the game resource provider
   * @since 1.0.0
   */
  @NotNull GameProvider locate();
}

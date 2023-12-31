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
package space.vectrix.ignite.launch.ember;

import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the launch service for Ember.
 *
 * @author vectrix
 * @since 1.0.0
 */
public interface LaunchService {
  /**
   * Executed at the very beginning of the launch process, before mixin has
   * been initialized.
   *
   * @since 1.0.0
   */
  void initialize();

  /**
   * Configures the class loader, before mixin has been initialized.
   *
   * @param classLoader the class loader
   * @param transformer the transformer
   * @since 1.0.0
   */
  void configure(final @NotNull EmberClassLoader classLoader, final @NotNull EmberTransformer transformer);

  /**
   * Executed after mixin has been initialized, but before the game has
   * launched.
   *
   * @param transformer the transformer
   * @since 1.0.0
   */
  void prepare(final @NotNull EmberTransformer transformer);

  /**
   * Launches the game.
   *
   * @param arguments the launch arguments
   * @param loader the class loader
   * @return a callable
   * @since 1.0.0
   */
  @NotNull Callable<Void> launch(final @NotNull String@NotNull [] arguments, final @NotNull EmberClassLoader loader);
}

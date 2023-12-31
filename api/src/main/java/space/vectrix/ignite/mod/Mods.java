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
package space.vectrix.ignite.mod;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the mod manager.
 *
 * @author vectrix
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface Mods {
  /**
   * Returns {@code true} if the given mod is loaded, otherwise it returns
   * {@code false}.
   *
   * @param id the mod identifier
   * @return whether the mod is loaded
   * @since 1.0.0
   */
  boolean loaded(final @NotNull String id);

  /**
   * Returns the {@link ModContainer} for the given mod identifier.
   *
   * @param id the mod identifier
   * @return the mod container
   * @since 1.0.0
   */
  @NotNull Optional<ModContainer> container(final @NotNull String id);

  /**
   * Returns a list of the located mod resources.
   *
   * @return the located mod resources
   * @since 1.0.0
   */
  @NotNull List<ModResource> resources();

  /**
   * Returns a collection of the resolved mod containers.
   *
   * @return the resolved mod containers
   * @since 1.0.0
   */
  @NotNull Collection<ModContainer> containers();
}

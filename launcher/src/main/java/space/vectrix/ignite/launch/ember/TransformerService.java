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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

/**
 * Represents a transformer service for Ember.
 *
 * @author vectrix
 * @since 1.0.0
 */
public interface TransformerService {
  /**
   * Executed after mixin has completed bootstrapping, but before the game has
   * launched.
   *
   * @since 1.0.0
   */
  void prepare();

  /**
   * Returns the priority of this transformer for the given {@link TransformPhase}.
   *
   * <p>A result of -1 means this transformer should not be applied during
   * the given phase.</p>
   *
   * <p>This method will be called multiple times for sorting the transformers
   * each class.</p>
   *
   * @param phase the transform phase
   * @return the priority
   * @since 1.0.0
   */
  int priority(final @NotNull TransformPhase phase);

  /**
   * Returns {@code true} if this transformer should transform the given
   * {@link Type} and {@link ClassNode}, otherwise returns {@code false}.
   *
   * @param type the type
   * @param node the class node
   * @return whether the class should be transformed
   * @since 1.0.0
   */
  boolean shouldTransform(final @NotNull Type type, final @NotNull ClassNode node);

  /**
   * Attempts to transform a class, with the given {@link Type}, {@link ClassNode}
   * and {@link TransformPhase} and returns the {@link ClassNode} if modifications were
   * made, otherwise returns {@code null}.
   *
   * @param type the type
   * @param node the class node
   * @param phase the transform phase
   * @return whether the class node if the class was transformed
   * @since 1.1.0
   */
  @Nullable ClassNode transform(final @NotNull Type type, final @NotNull ClassNode node, final @NotNull TransformPhase phase) throws Throwable;
}

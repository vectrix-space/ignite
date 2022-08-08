/*
 * This file is part of ignite, licensed under the MIT License (MIT).
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
package space.vectrix.ignite.bootstrap.applaunch.blackboard;

import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.api.blackboard.Blackboard;
import space.vectrix.ignite.api.blackboard.Key;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class BlackboardImpl implements Blackboard {
  private final ConcurrentMap<Key<Object>, Object> values = new ConcurrentHashMap<>();

  public BlackboardImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T> @NotNull T compute(final @NotNull Key<T> key, final @NotNull Supplier<? super T> defaultValue) {
    requireNonNull(key, "key");
    requireNonNull(defaultValue, "defaultValue");
    return key.type().cast(this.values.computeIfAbsent((Key<Object>) key, x -> defaultValue.get()));
  }

  @Override
  public <T> @NotNull T get(final @NotNull Key<T> key) {
    requireNonNull(key, "key");
    final Object value = this.values.get(key);
    if(value == null) throw new IllegalArgumentException(String.format("Key '%s' does not have a value in this blackboard!", key.name()));
    return key.type().cast(value);
  }

  @Override
  public @NotNull <T> Optional<T> getIfPresent(final @NotNull Key<T> key) {
    requireNonNull(key, "key");
    final Object value = this.values.get(key);
    if(value == null) return Optional.empty();
    return Optional.of(key.type().cast(value));
  }
}

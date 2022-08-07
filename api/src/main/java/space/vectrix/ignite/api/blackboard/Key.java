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
package space.vectrix.ignite.api.blackboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Key<T> implements Comparable<Key<T>> {
  @SuppressWarnings("unchecked")
  public static <T> @NotNull Key<T> of(final @NotNull String name, final @NotNull Class<? super T> type) {
    return new Key<>(name, (Class<T>) type);
  }

  private final String name;
  private final Class<T> type;

  /* package */ Key(final @NotNull String name, final @NotNull Class<T> type) {
    this.name = name;
    this.type = type;
  }

  public @NotNull String name() {
    return this.name;
  }

  public @NotNull Class<T> type() {
    return this.type;
  }

  @Override
  public int compareTo(final @NotNull Key<T> other) {
    if(this == other) return 0;
    return this.name.compareTo(other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof Key<?>)) return false;
    final Key<?> that = (Key<?>) other;
    return Objects.equals(this.name, that.name);
  }

  @Override
  public String toString() {
    return "Key{name=" + this.name + ", type=" + this.type + "}";
  }
}

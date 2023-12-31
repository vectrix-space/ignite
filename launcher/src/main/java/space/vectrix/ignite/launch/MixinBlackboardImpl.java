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
package space.vectrix.ignite.launch;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.util.BlackboardMap;

/**
 * Represents the mixin blackboard provider.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class MixinBlackboardImpl implements IGlobalPropertyService {
  private final Map<String, IPropertyKey> keys = new HashMap<>();

  @Override
  public IPropertyKey resolveKey(final @NotNull String name) {
    return this.keys.computeIfAbsent(name, key -> new Key<>(key, Object.class));
  }

  @Override
  public <T> T getProperty(final @NotNull IPropertyKey key) {
    return this.getProperty(key, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setProperty(final @NotNull IPropertyKey key, final @NotNull Object other) {
    Blackboard.put(((Key<Object>) key).key, other);
  }

  @Override
  public @Nullable String getPropertyString(final @NotNull IPropertyKey key, final @Nullable String defaultValue) {
    return this.getProperty(key, defaultValue);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T getProperty(final @NotNull IPropertyKey key, final @Nullable T defaultValue) {
    return Blackboard.get(((Key<T>) key).key).orElse(defaultValue);
  }

  private static class Key<V> implements IPropertyKey {
    private final BlackboardMap.Key<V> key;

    /* package */ Key(final @NotNull String name, final @NotNull Class<V> clazz) {
      this.key = Blackboard.key(name, clazz, null);
    }
  }
}

package com.mineteria.ignite.api.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

// Taken from cpw.mods.modlauncher.api.TypesafeMap
public final class BlackboardMap {
  private static final ConcurrentHashMap<Class<?>, BlackboardMap> maps = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<Key<Object>, Object> map = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Key<Object>> keys = new ConcurrentHashMap<>();

  public BlackboardMap() {
  }

  public BlackboardMap(final @NonNull Class<?> owner) {
    KeyBuilder.keyBuilders.getOrDefault(owner, Collections.emptyList()).forEach(kb -> kb.buildKey(this));
    maps.put(owner, this);
  }

  public <V> @NonNull Optional<V> get(final @NonNull Key<V> key) {
    return Optional.ofNullable(key.clz.cast(map.get(key)));
  }

  public <V> @Nullable V computeIfAbsent(final @NonNull Key<V> key, final @NonNull Function<? super Key<V>, ? extends V> valueFunction) {
    return computeIfAbsent(this.map, key, valueFunction);
  }

  @SuppressWarnings("unchecked")
  private <C1, C2, V> V computeIfAbsent(final ConcurrentHashMap<C1, C2> map, final Key<V> key, final Function<? super Key<V>, ? extends V> valueFunction) {
    return (V) map.computeIfAbsent((C1) key, (Function<? super C1, ? extends C2>) valueFunction);
  }

  private ConcurrentHashMap<String, Key<Object>> getKeyIdentifiers() {
    return keys;
  }

  /**
   * Unique blackboard key
   */
  public static final class Key<T> implements Comparable<Key<T>> {
    private static final AtomicLong idGenerator = new AtomicLong();

    private final String name;
    private final long uniqueId;
    private final Class<T> clz;

    private Key(final String name, final Class<T> clz) {
      this.clz = clz;
      this.name = name;

      this.uniqueId = idGenerator.getAndIncrement();
    }

    @SuppressWarnings("unchecked")
    public static <V> @NonNull Key<V> getOrCreate(final @NonNull BlackboardMap owner, final @NonNull String name, final @NonNull Class<? super V> clazz) {
      final Key<V> result = (Key<V>) owner.getKeyIdentifiers().computeIfAbsent(name, (n) -> new Key<>(n, (Class<Object>) clazz));

      if (result.clz != clazz) {
        throw new IllegalArgumentException("Invalid type");
      }

      return result;
    }

    public static <V> @NonNull Supplier<Key<V>> getOrCreate(final @NonNull Supplier<BlackboardMap> owner, final @NonNull String name, final @NonNull Class<V> clazz) {
      return () -> getOrCreate(owner.get(), name, clazz);
    }

    public final @NonNull String name() {
      return this.name;
    }

    @Override
    public int hashCode() {
      return (int) (this.uniqueId ^ (this.uniqueId >>> 32));
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if (other == null) return false;

      try {
        return this.uniqueId == ((Key<?>) other).uniqueId;
      } catch (final ClassCastException cc) {
        return false;
      }
    }

    @Override
    public int compareTo(final @NonNull Key other) {
      if (this == other) {
        return 0;
      }

      if (this.uniqueId < other.uniqueId) {
        return -1;
      }

      if (this.uniqueId > other.uniqueId) {
        return 1;
      }

      throw new RuntimeException("Huh?");
    }
  }

  public static final class KeyBuilder<T> implements Supplier<Key<T>> {
    private static final Map<Class<?>, List<KeyBuilder<?>>> keyBuilders = new HashMap<>();

    private final Class<?> owner;
    private final String name;
    private final Class<? super T> clazz;

    private Key<T> key;

    public KeyBuilder(final @NonNull String name, final @NonNull Class<? super T> clazz, final @NonNull Class<?> owner) {
      this.name = name;
      this.clazz = clazz;
      this.owner = owner;

      keyBuilders.computeIfAbsent(owner, k -> new ArrayList<>()).add(this);
    }

    final void buildKey(final BlackboardMap map) {
      this.key = Key.getOrCreate(map, name, clazz);
    }

    @Override
    public @NonNull Key<T> get() {
      if (this.key == null && maps.containsKey(this.owner)) {
        buildKey(maps.get(this.owner));
      }

      if (this.key == null) {
        throw new NullPointerException("Missing map");
      }

      return this.key;
    }
  }
}

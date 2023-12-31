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
package space.vectrix.ignite;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import space.vectrix.ignite.util.BlackboardMap;

/**
 * Represents a map of startup flags.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class Blackboard {
  private static final BlackboardMap BLACKBOARD = BlackboardMap.create();

  // formatting:off
  public static final BlackboardMap.@NotNull Key<Boolean> DEBUG = key("ignite.debug", Boolean.class, false);
  public static final BlackboardMap.@NotNull Key<String> GAME_LOCATOR = key("ignite.locator", String.class, "dummy");
  public static final BlackboardMap.@NotNull Key<Path> GAME_JAR = key("ignite.jar", Path.class, Paths.get("./server.jar"));
  public static final BlackboardMap.@NotNull Key<String> GAME_TARGET = key("ignite.target", String.class, "org.bukkit.craftbukkit.Main");
  public static final BlackboardMap.@NotNull Key<Path> GAME_LIBRARIES = key("ignite.libraries", Path.class, Paths.get("./libraries"));
  public static final BlackboardMap.@NotNull Key<Path> MODS_DIRECTORY = key("ignite.mods", Path.class, Paths.get("./mods"));
  // formatting:on

  /**
   * Returns the value associated with the {@link BlackboardMap.Key}.
   *
   * @param key the key
   * @param <T> the value type
   * @return the value
   * @since 1.0.0
   */
  public static <T> @NotNull Optional<T> get(final BlackboardMap.@NotNull Key<T> key) {
    return Blackboard.BLACKBOARD.get(key);
  }

  /**
   * Returns the value associated with the {@link BlackboardMap.Key}.
   *
   * @param key the key
   * @param <T> the value type
   * @return the value
   * @since 1.0.0
   */
  public static <T> @UnknownNullability T raw(final BlackboardMap.@NotNull Key<T> key) {
    return Blackboard.BLACKBOARD.get(key).orElse(key.defaultValue());
  }

  /**
   * Supplies the value associated with the {@link BlackboardMap.Key}.
   *
   * @param key the key
   * @param supplier the supplier
   * @param <T> the value type
   * @since 1.0.0
   */
  public static <T> void compute(final BlackboardMap.@NotNull Key<T> key, final @NotNull Supplier<T> supplier) {
    Blackboard.BLACKBOARD.put(key, Blackboard.supplyOrNull(supplier));
  }

  /**
   * Sets the value associated with the {@link BlackboardMap.Key}.
   *
   * @param key the key
   * @param value the value
   * @param <T> the value type
   * @since 1.0.0
   */
  public static <T> void put(final BlackboardMap.@NotNull Key<T> key, final @Nullable T value) {
    Blackboard.BLACKBOARD.put(key, value);
  }

  /**
   * Returns a new {@link BlackboardMap.Key} for the given key, type and
   * default value.
   *
   * @param key the key
   * @param type the type
   * @param defaultValue the default value
   * @param <T> the value type
   * @return a new blackboard key
   * @since 1.0.0
   */
  public static <T> BlackboardMap.@NotNull Key<T> key(final @NotNull String key, final @NotNull Class<? super T> type, final @Nullable T defaultValue) {
    return BlackboardMap.Key.of(Blackboard.BLACKBOARD, key, type, defaultValue);
  }

  private static <T> @Nullable T supplyOrNull(final @NotNull Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch(final Throwable throwable) {
      return null;
    }
  }

  private Blackboard() {
  }
}

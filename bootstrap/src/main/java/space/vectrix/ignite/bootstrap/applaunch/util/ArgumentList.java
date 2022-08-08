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
package space.vectrix.ignite.bootstrap.applaunch.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ArgumentList {
  private static final Logger LOGGER = LogManager.getLogger("IgniteBootstrap | ArgumentList");

  public static @NotNull ArgumentList from(final @NotNull String... arguments) {
    final ArgumentList argumentList = new ArgumentList();

    boolean ended = false;
    for(int x = 0; x < arguments.length; x++) {
      if(!ended) {
        if("--".equals(arguments[x])) { // "--" by itself means there are no more arguments
          ended = true;
        } else if("-".equals(arguments[x])) {
          argumentList.add(arguments[x]);
        } else if(arguments[x].startsWith("-")) {
          final int idx = arguments[x].indexOf("=");
          final String key = idx == -1 ? arguments[x] : arguments[x].substring(0, idx);
          final String value = idx == -1 ? null : idx == arguments[x].length() - 1 ? "" : arguments[x].substring(idx + 1);

          if(idx == -1 && x + 1 < arguments.length && !arguments[x + 1].startsWith("-")) { // not in --key=value, so try and grab the next argument
            argumentList.add(key, arguments[x + 1], true); // assume that if the next value is an "argument" then don't use it as a value
            x++; // this isn't perfect, but the best we can do without knowing all the specifics
          } else {
            argumentList.add(key, value, false);
          }
        } else {
          argumentList.add(arguments[x]);
        }
      } else {
        argumentList.add(arguments[x]);
      }
    }

    return argumentList;
  }

  private final List<Supplier<String[]>> entries = new ArrayList<>();
  private final Map<String, EntryValue> values = new HashMap<>();

  public ArgumentList() {}

  public void add(final @NotNull String argument) {
    this.entries.add(() -> new String[] { argument });
  }

  public void add(final @NotNull String raw, final @Nullable String value, final boolean split) {
    final int idx = raw.startsWith("--") ? 2 : 1;
    final String prefix = raw.substring(0, idx);
    final String key = raw.substring(idx);
    final EntryValue entry = new EntryValue(prefix, key, value, split);

    if(this.values.containsKey(key)) {
      ArgumentList.LOGGER.debug(String.format("Duplicate entry for %s is un-indexable.", key));
    } else {
      this.values.put(key, entry);
    }

    this.entries.add(entry);
  }

  public @NotNull String[] arguments() {
    return this.entries.stream()
      .flatMap(entry -> Arrays.stream(entry.get()))
      .toArray(String[]::new);
  }

  private record EntryValue(String prefix, String key, String value, boolean split) implements Supplier<String[]> {
      @Override
      public @NotNull String[] get() {
        if(this.value() == null) return new String[] { this.prefix + this.key() };
        if(this.split) return new String[] { this.prefix + this.key(), this.value() };
        return new String[] { this.prefix + this.key() + "=" + this.value() };
      }

      @Override
      public String toString() {
        return String.join(", ", this.get());
      }
    }
}

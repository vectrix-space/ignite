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
package space.vectrix.example;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Blackboard;
import space.vectrix.ignite.api.config.Configuration;

import java.nio.file.Path;

public final class ExampleInfo {
  private static final @MonotonicNonNull Path CONFIGS_PATH = Blackboard.getProperty(Blackboard.CONFIG_DIRECTORY_PATH);

  private static @MonotonicNonNull Path EXAMPLE_PATH;
  private static Configuration.@MonotonicNonNull Key<ExampleConfig> EXAMPLE_CONFIG;

  public static @MonotonicNonNull Path getExamplePath() {
    if (ExampleInfo.EXAMPLE_PATH != null) return ExampleInfo.EXAMPLE_PATH;
    if (ExampleInfo.CONFIGS_PATH == null) return null;

    return ExampleInfo.EXAMPLE_PATH = ExampleInfo.CONFIGS_PATH.resolve("example");
  }

  public static Configuration.@NonNull Key<ExampleConfig> getExampleConfig() {
    if (ExampleInfo.EXAMPLE_CONFIG != null) return ExampleInfo.EXAMPLE_CONFIG;

    final Path examplePath = ExampleInfo.getExamplePath();
    if (examplePath == null) throw new IllegalStateException("Unable to locate example path.");

    return ExampleInfo.EXAMPLE_CONFIG = Configuration.key(ExampleConfig.class, examplePath.resolve("example.conf"));
  }
}

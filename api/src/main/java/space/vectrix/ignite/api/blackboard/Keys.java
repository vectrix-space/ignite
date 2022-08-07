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

import java.nio.file.Path;

public final class Keys {
  public static final Key<Boolean> DEVELOPMENT = Key.of("development", Boolean.class);

  public static final Key<String> VERSION = Key.of("version", String.class);

  public static final Key<Path> PLATFORM_DIRECTORY = Key.of("platform_directory", Path.class);

  public static final Key<Path> PLATFORM_JAR = Key.of("platform_jar", Path.class);

  public static final Key<String> PLATFORM_CLASSPATH = Key.of("platform_classpath", String.class);

  public static final Key<Path> MOD_DIRECTORY = Key.of("mod_directory", Path.class);

  private Keys() {
    throw new AssertionError("Attempted to instantiate a class that is non-instantiable");
  }
}

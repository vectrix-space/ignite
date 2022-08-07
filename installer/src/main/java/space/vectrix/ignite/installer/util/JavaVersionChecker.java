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
package space.vectrix.ignite.installer.util;

import org.tinylog.Logger;

public final class JavaVersionChecker {
  private static final int MINIMUM_VERSION = 16;

  public static void check() {
    // Ensure we are running Java 16+, otherwise fail early.
    String javaVersion = System.getProperty("java.version");
    if(!javaVersion.startsWith("1.")) {
      final int dot = javaVersion.indexOf(".");
      if(dot != -1) javaVersion = javaVersion.substring(0, dot);

      final int version = Integer.parseInt(javaVersion);
      if(version >= JavaVersionChecker.MINIMUM_VERSION) return;
    }

    Logger.error("UNABLE TO START IGNITE");
    Logger.error("");
    Logger.error(String.format("You are running an old version of Java. Please launch Ignite with at least Java %s or higher.", JavaVersionChecker.MINIMUM_VERSION));
    Logger.error("");
    Logger.error(String.format("Found: %s", javaVersion));
    Logger.error("");
    Logger.error("Once you have corrected the problem above, try again.");

    System.exit(1);
  }
}

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
package space.vectrix.ignite.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import space.vectrix.ignite.agent.IgniteAgent;

/**
 * Verifies classpath discovery in a fresh JVM with the real Ignite agent.
 *
 * @since 1.2.2
 */
public final class AgentClasspathCheck {
  /**
   * Runs the agent classpath regression check.
   *
   * @param args unused arguments
   * @throws Exception if the regression check fails
   * @since 1.2.2
   */
  public static void main(final String[] args) throws Exception {
    final String startupClassPath = System.getProperty("java.class.path");
    final URL[] startupUrls = ClassLoaders.systemClassPaths();
    final Path appendedJar = Files.createTempFile("ignite-agent-classpath-", ".jar");
    appendedJar.toFile().deleteOnExit();
    try(final JarOutputStream stream = new JarOutputStream(Files.newOutputStream(appendedJar))) {
      stream.finish();
    }

    IgniteAgent.addJar(appendedJar);
    final URL appendedUrl = appendedJar.toUri().toURL();
    if(!Arrays.asList(ClassLoaders.systemClassPaths()).contains(appendedUrl)) {
      throw new AssertionError("Agent-appended JAR is missing from the transformation classpath");
    }
    if(!startupClassPath.equals(System.getProperty("java.class.path"))) {
      throw new AssertionError("The agent must not rewrite java.class.path");
    }
    if(!Arrays.asList(ClassLoaders.systemClassPaths()).containsAll(Arrays.asList(startupUrls))) {
      throw new AssertionError("Startup classpath entries were lost");
    }

    // Both addJar overloads may see the same JAR; keep a single transformation path.
    IgniteAgent.addJar(new JarFile(appendedJar.toFile()));
    final long occurrences = Arrays.stream(ClassLoaders.systemClassPaths()).filter(appendedUrl::equals).count();
    if(occurrences != 1) {
      throw new AssertionError("Duplicate appended transformation paths: " + occurrences);
    }

    try {
      System.setProperty("java.class.path", "");
      if(!Arrays.asList(ClassLoaders.systemClassPaths()).contains(appendedUrl)) {
        throw new AssertionError("An empty startup classpath hid agent-appended JARs");
      }
    } finally {
      System.setProperty("java.class.path", startupClassPath);
    }
    System.out.println("Agent classpath regression check passed.");
  }

  private AgentClasspathCheck() {
  }
}

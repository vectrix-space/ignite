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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Provides a way to have mixins included inside the plugin jar, packed as a jar.
 *
 * @author MangoRage
 * @since 1.2.0
 */
public final class PreBoot {
  private static Path of(final String first, final String... more) {
    return FileSystems.getDefault().getPath(first, more);
  }

  private static final Path PLUGINS = of("plugins");
  private static final Path MODS = of("mods");

  private static final String TEMP_NAME = "TEMP_MIXIN_JAR_MOD_";

  /**
   * Initiiates the process of unpacking packed mixins.
   *
   * @author MangoRage
   * @since 1.2.0
   */
  public static void init() {
    System.out.println("Started Ignite Pre-Boot System...");
    try {
      if (Files.exists(PLUGINS)) {
        if (Files.exists(MODS)) {
          System.out.println("Deleting Unpacked Mods and finding Packed Ignite Mods...");
          // DELETE TEMP MODS
          Files.walk(MODS)
            .filter(path -> path.toString().startsWith(TEMP_NAME)) // Only look for .jar files
            .forEach(f -> {
              try {
                Files.delete(f);
              } catch (final IOException exception) {
                throw new RuntimeException(exception);
              }
            });
        } else {
          System.out.println("Finding Packed Ignite Mods...");
        }

        // Search for jar files in the rootDir
        Files.walk(PLUGINS)
          .filter(path -> path.toString().endsWith(".jar")) // Only look for .jar files
          .forEach(PreBoot::inspectJarFile);
      }
    } catch (final IOException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static void inspectJarFile(final Path jarPath) {
    try (JarFile jarFile = new JarFile(jarPath.toFile())) {
      final Enumeration<JarEntry> entries = jarFile.entries();

      // Iterate over the entries inside the jar
      while (entries.hasMoreElements()) {
        final JarEntry entry = entries.nextElement();

        // Check if the entry is a .jar file inside the mixinJar folder
        if (entry.getName().startsWith("mixinjar/") && entry.getName().endsWith(".jar")) {
          System.out.println("Found a mixin packed into " + jarFile.getName() + " named as " + entry.getName() + " unpacking into mods as " + copyJarFile(jarFile, entry, MODS));
        }
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  // Function to copy the jar file from within the mixinJar folder to another directory
  private static Path copyJarFile(final JarFile jarFile, final JarEntry entry, final Path targetDir) {
    final Path outputFile = targetDir.resolve(TEMP_NAME + Paths.get(entry.getName()).getFileName());

    // Create directories if they don't exist
    try {
      Files.createDirectories(targetDir);

      // Open the input stream from the jar entry and copy it to the output file
      try (InputStream inputStream = jarFile.getInputStream(entry)) {
        Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }

    return outputFile;
  }

  private PreBoot() {
  }
}

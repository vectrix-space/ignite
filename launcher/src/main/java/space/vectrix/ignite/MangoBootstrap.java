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
import java.util.jar.Manifest;

public class MangoBootstrap {
  private static final String IGNITE = "ignite.jar";

  public static Path of(String first, String... more) {
    return FileSystems.getDefault().getPath(first, more);
  }

  private static final Path PLUGINS = of("plugins");
  private static final Path MODS = of("mods");

  private static final String TEMP_NAME = "TEMP_MIXIN_JAR_";

  public static void main(String[] args) throws IOException {
    System.out.println("Started MangoRage's Ignite Boot System...");

    if (Files.exists(PLUGINS)) {

      if (Files.exists(MODS)) {
        System.out.println("Deleting Unpacked Mods and finding Packed Ignite Mods...");
        // DELETE TEMP MODS
        Files.walk(MODS)
          .filter(path -> path.toString().startsWith(TEMP_NAME)) // Only look for .jar files
          .forEach(f -> {
            try {
              Files.delete(f);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
      } else {
        System.out.println("Finding Packed Ignite Mods...");
      }

      // Search for jar files in the rootDir
      Files.walk(PLUGINS)
        .filter(path -> path.toString().endsWith(".jar")) // Only look for .jar files
        .forEach(MangoBootstrap::inspectJarFile);
    }
  }

  /**
   * Finds the Main-Class attribute from a given jar file.
   *
   * @param jarPath The path to the jar file.
   * @return The fully qualified Main-Class name, or null if not found.
   * @throws IOException If an I/O error occurs when reading the jar file.
   */
  public static String findMainClass(Path jarPath) throws IOException {
    // Open the jar file
    try (JarFile jarFile = new JarFile(jarPath.toFile())) {
      // Get the manifest from the jar
      Manifest manifest = jarFile.getManifest();
      // If the manifest exists, try to get the Main-Class attribute
      if (manifest != null) {
        return manifest.getMainAttributes().getValue("Main-Class");
      }
    }

    // Return null if no Main-Class is found
    return null;
  }

  private static void inspectJarFile(Path jarPath) {
    try (JarFile jarFile = new JarFile(jarPath.toFile())) {
      Enumeration<JarEntry> entries = jarFile.entries();

      // Iterate over the entries inside the jar
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();

        // Check if the entry is a .jar file inside the mixinJar folder
        if (entry.getName().startsWith("mixinjar/") && entry.getName().endsWith(".jar")) {
          System.out.println("Found mixinJar and un-packing it: " + entry.getName() + " in " + jarPath);
          // You can process the found jar entry here
          copyJarFile(jarFile, entry, MODS);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Function to copy the jar file from within the mixinJar folder to another directory
  private static void copyJarFile(JarFile jarFile, JarEntry entry, Path targetDir) {
    Path outputFile = targetDir.resolve(TEMP_NAME + Paths.get(entry.getName()).getFileName());

    // Create directories if they don't exist
    try {
      Files.createDirectories(targetDir);

      // Open the input stream from the jar entry and copy it to the output file
      try (InputStream inputStream = jarFile.getInputStream(entry)) {
        Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied to: " + outputFile);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

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
package space.vectrix.ignite.applaunch.mod;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Blackboard;
import space.vectrix.ignite.api.mod.ModResource;
import space.vectrix.ignite.applaunch.util.EngineResource;
import space.vectrix.ignite.applaunch.util.IgniteConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ModResourceLocator {
  public static final @NonNull String DEFAULT_METADATA_FILENAME = "ignite-mod.json";
  public static final @NonNull String ENGINE_LOCATOR = "engine_locator";
  public static final @NonNull String JAVA_LOCATOR = "java_locator";

  public final @NonNull List<ModResource> locateResources(final @NonNull ModEngine engine) {
    final List<ModResource> modResources = new ArrayList<>();

    modResources.add(EngineResource.createEngineResource(engine));

    final Path modDirectory = Blackboard.getProperty(Blackboard.MOD_DIRECTORY_PATH);
    if (modDirectory == null || Files.notExists(modDirectory)) {
      engine.getLogger().warn("Mod directory '" + modDirectory + "' does not exist for mod resource locator. Skipping...");
      return modResources;
    }

    try {
      for (final Path childDirectory : Files.walk(modDirectory).collect(Collectors.toList())) {
        if (!Files.isRegularFile(childDirectory) || !childDirectory.getFileName().toString().endsWith(".jar")) {
          continue;
        }

        try (final JarFile jarFile = new JarFile(childDirectory.toFile())) {
          final JarEntry jarEntry = jarFile.getJarEntry(this.getMetadataPath());
          if (jarEntry == null) {
            engine.getLogger().debug("The resource '" + jarFile.getName() + "' does not contain any mod metadata so it is not a mod. Skipping...");
            continue;
          }

          modResources.add(new ModResource(ModResourceLocator.JAVA_LOCATOR, childDirectory, jarFile.getManifest()));
        }
      }
    } catch (final IOException exception) {
      engine.getLogger().error("Failed to walk the mods directory '" + modDirectory + "'!", exception);
    }

    return modResources;
  }

  public final @NonNull String getMetadataPath() {
    return IgniteConstants.META_INF + "/" + ModResourceLocator.DEFAULT_METADATA_FILENAME;
  }
}

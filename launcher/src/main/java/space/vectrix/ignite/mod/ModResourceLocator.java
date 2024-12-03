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
package space.vectrix.ignite.mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.IgniteBootstrap;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Represents the mod resource locator.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class ModResourceLocator {
  public static final String JAVA_LOCATOR = "java_locator";
  public static final String LAUNCHER_LOCATOR = "launcher_locator";
  public static final String GAME_LOCATOR = "game_locator";

  /* package */ @NotNull List<ModResourceImpl> locateResources() {
    final List<ModResourceImpl> resources = new ArrayList<>();

    // Add the launcher and game resources.
    resources.add(this.createLauncherResource());
    resources.add(this.createGameResource());

    // Retrieve the mods from the mods directory, or inside plugins.
    this.scanModsDirectory(resources);
    this.scanPluginsDirectory(resources);

    return resources;
  }

  private void scanModsDirectory(final @NotNull List<ModResourceImpl> resources) {
    final Path modDirectory = Blackboard.raw(Blackboard.MODS_DIRECTORY);
    try {
      if(modDirectory == null) {
        throw new RuntimeException("Failed to get mods directory!");
      }

      if(Files.notExists(modDirectory)) {
        //noinspection ResultOfMethodCallIgnored
        modDirectory.toFile().mkdirs();
      }

      //noinspection resource
      for(final Path childDirectory : Files.walk(modDirectory, 1).collect(Collectors.toList())) {
        if(!Files.isRegularFile(childDirectory) || !childDirectory.getFileName().toString().endsWith(".jar")) {
          continue;
        }

        this.tryLoadMod(childDirectory, resources);
      }
    } catch(final Throwable throwable) {
      throw new RuntimeException("Failed to walk the mods directory!", throwable);
    }
  }

  private void scanPluginsDirectory(final @NotNull List<ModResourceImpl> resources) {
    final Path modDirectory = Blackboard.raw(Blackboard.MODS_DIRECTORY);
    final Path pluginDirectory = Blackboard.raw(Blackboard.PLUGINS_DIRECTORY);
    try {
      if(modDirectory == null
        || pluginDirectory == null
        || Files.notExists(pluginDirectory)
        || Files.notExists(modDirectory)) {
        return;
      }

      final Path destinationPath = modDirectory.resolve("_plugins");
      this.deleteModsFromPlugins(destinationPath);

      //noinspection resource
      for(final Path childDirectory : Files.walk(pluginDirectory, 1).collect(Collectors.toList())) {
        if(!Files.isRegularFile(childDirectory) || !childDirectory.getFileName().toString().endsWith(".jar")) {
          continue;
        }

        try(final JarFile jarFile = new JarFile(childDirectory.toFile())) {
          final Enumeration<JarEntry> entries = jarFile.entries();

          while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();

            if(entry.getName().endsWith(".mixin.jar")) {
              Logger.debug("Located mixin jar '{}' in plugin '{}'. Extracting to temporary mod directory...", entry.getName(), jarFile.getName());
              final Path modPath = this.extractModFromPlugin(
                childDirectory.getFileName().toString(),
                jarFile, entry, destinationPath
              );

              this.tryLoadMod(modPath, resources);
            }
          }
        }
      }
    } catch(final Throwable throwable) {
      throw new RuntimeException("Failed to walk the plugins directory!", throwable);
    }
  }

  private @NotNull ModResourceImpl createLauncherResource() {
    final File launcherFile;
    try {
      launcherFile = new File(IgniteBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch(final URISyntaxException exception) {
      throw new RuntimeException("Failed to get launcher path!", exception);
    }

    try(final JarFile jarFile = new JarFile(launcherFile)) {
      return new ModResourceImpl(ModResourceLocator.LAUNCHER_LOCATOR, launcherFile.toPath(), jarFile.getManifest());
    } catch(final Exception exception) {
      throw new RuntimeException("Failed to get launcher manifest!", exception);
    }
  }

  private @NotNull ModResourceImpl createGameResource() {
    final File gameFile = Blackboard.raw(Blackboard.GAME_JAR).toFile();
    try(final JarFile jarFile = new JarFile(gameFile)) {
      return new ModResourceImpl(ModResourceLocator.GAME_LOCATOR, gameFile.toPath(), jarFile.getManifest());
    } catch(final Exception exception) {
      throw new RuntimeException("Failed to get game manifest!", exception);
    }
  }

  private void deleteModsFromPlugins(final @NotNull Path destinationDirectory) {
    try {
      if(Files.exists(destinationDirectory)) {
        try(final Stream<Path> paths = Files.walk(destinationDirectory)) {
          paths
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
              try {
                Files.delete(path);
              } catch(final IOException exception) {
                Logger.debug("Failed to delete '{}'.", path, exception);
              }
            });
        }
      }
    } catch(final IOException exception) {
      Logger.debug("Error while cleaning directory '{}'.", destinationDirectory, exception);
    }
  }

  private Path extractModFromPlugin(final @NotNull String jarName,
                                    final @NotNull JarFile jarFile,
                                    final @NotNull JarEntry jarEntry,
                                    final @NotNull Path destinationDirectory) {
    final String baseJarName = jarName.substring(0, jarName.length() - ".jar".length());
    final Path outputPath = destinationDirectory.resolve(baseJarName).resolve(jarEntry.getName());

    if(Files.notExists(outputPath)) {
      //noinspection ResultOfMethodCallIgnored
      outputPath.toFile().mkdirs();
    }

    try {
      try(InputStream inputStream = jarFile.getInputStream(jarEntry)) {
        Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }

    return outputPath;
  }

  private void tryLoadMod(final @NotNull Path modPath, final @NotNull List<ModResourceImpl> resources) throws IOException {
    try(final JarFile jarFile = new JarFile(modPath.toFile())) {
      final JarEntry jarEntry = jarFile.getJarEntry(IgniteConstants.MOD_CONFIG);
      if(jarEntry == null) {
        Logger.warn("'{}' was in the mods directory, but could not load due to a missing '{}'.", jarFile.getName(), IgniteConstants.MOD_CONFIG);
        return;
      }

      resources.add(new ModResourceImpl(ModResourceLocator.JAVA_LOCATOR, modPath, jarFile.getManifest()));
    }
  }
}

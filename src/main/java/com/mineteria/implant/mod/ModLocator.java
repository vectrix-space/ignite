package com.mineteria.implant.mod;

import com.mineteria.implant.ImplantCore;
import com.mineteria.implant.launch.ImplantBlackboard;
import com.mineteria.implant.util.ImplantConstants;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ModLocator {
  public static final String DEFAULT_METADATA_FILENAME = "mod.json";

  private static final String NAME = "java_directory";

  public @NonNull List<ModResource> locateResources() {
    final ImplantCore core = ImplantCore.INSTANCE;
    final List<ModResource> modResources = new ArrayList<>();

    final Path modDirectory = ImplantBlackboard.getProperty(ImplantBlackboard.MOD_DIRECTORY_PATH);
    if (modDirectory == null || Files.notExists(modDirectory)) {
      core.getLogger().warn("Mod directory '{}' does not exist for mod locator. Skipping...", modDirectory);
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
            core.getLogger().debug("'{}' does not contain any mod metadata so it is not a mod. Skipping...", jarFile);
            continue;
          }

          modResources.add(new ModResource(ModLocator.NAME, childDirectory, jarFile.getManifest()));
        }
      }
    } catch (final IOException exception) {
      core.getLogger().error("Error walking mods directory '{}'.", modDirectory, exception);
    }

    return modResources;
  }

  public @NonNull String getMetadataPath() {
    return ImplantConstants.META_INF + "/" + ModLocator.DEFAULT_METADATA_FILENAME;
  }
}

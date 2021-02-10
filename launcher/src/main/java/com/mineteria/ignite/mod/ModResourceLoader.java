package com.mineteria.ignite.mod;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mineteria.ignite.api.mod.ModConfig;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.api.mod.ModResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ModResourceLoader {
  private final Gson gson = new Gson();

  public @NonNull List<ModContainer> loadResources(final @NonNull ModEngine engine) {
    final List<ModContainer> containers = new ArrayList<>();

    for (final ModResource resource : engine.getResources()) {
      final Path resourcePath = resource.getPath();

      try (final JarFile jarFile = new JarFile(resourcePath.toFile())) {
        final JarEntry jarEntry = jarFile.getJarEntry(engine.getResourceLocator().getMetadataPath());
        if (jarEntry == null) {
          engine.getLogger().debug("'{}' does not contain any mod metadata so it is not a mod. Skipping...", jarFile);
          continue;
        }

        final JsonReader reader = new JsonReader(new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8));
        final ModConfig config = this.gson.fromJson(reader, ModConfig.class);

        if (engine.hasContainer(config.getId())) {
          engine.getLogger().warn("The mod '" + config.getId() + "' is already loaded! Skipping...");
          continue;
        }

        final Logger logger = LogManager.getLogger(config.getId());
        containers.add(new ModContainer(logger, resource, config));
      } catch (final IOException exception) {
        engine.getLogger().warn("Failed to open '{}'!", resourcePath);
      }
    }

    return containers;
  }
}

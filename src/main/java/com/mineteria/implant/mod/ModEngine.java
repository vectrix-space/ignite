package com.mineteria.implant.mod;

import com.google.gson.stream.JsonReader;
import com.mineteria.implant.ImplantCore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ModEngine {
  private final ModLocator locator = new ModLocator();
  private final List<ModResource> modResources = new ArrayList<>();
  private final List<ModContainer> modContainers = new ArrayList<>();

  private final ImplantCore core;

  public ModEngine(final @NonNull ImplantCore core) {
    this.core = core;
  }

  /**
   * Locates and populates the mod resources list.
   */
  public void locateResources() {
    this.core.getLogger().info("Locating mod resources...");

    this.modResources.addAll(this.locator.locateResources());

    this.core.getLogger().info("Located [{}] mod(s).", this.modResources.size());
  }

  /**
   * Load the mods and initializes them from the resources list.
   */
  public void loadCandidates() {
    this.core.getLogger().info("Loading mod candidates...");

    for (final ModResource resource : this.getCandidates()) {
      final Path resourcePath = resource.getPath();

      this.core.getLogger().debug("Scanning mod candidate '{}' for mod configuration!", resourcePath);

      try (final JarFile jarFile = new JarFile(resourcePath.toFile())) {
        final JarEntry jarEntry = jarFile.getJarEntry(this.locator.getMetadataPath());
        if (jarEntry == null) {
          core.getLogger().debug("'{}' does not contain any mod metadata so it is not a mod. Skipping...", jarFile);
          continue;
        }

        final JsonReader reader = new JsonReader(new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8));
        final ModConfig config = this.core.getGson().fromJson(reader, ModConfig.class);

        this.modContainers.add(new ModContainer(config.getId(), resource, config));
      } catch (final IOException exception) {
        this.core.getLogger().warn("Failed to open '{}'!", resourcePath);
      }
    }

    this.core.getLogger().info("Loaded [{}] mod(s).", this.getContainers().size());
  }

  public void loadContainers() {
    this.core.getLogger().info("Applying mod transformations...");

    for (final ModContainer container : this.getContainers()) {
      final ModConfig config = container.getConfig();

      // Add the mixin configurations.
      for (final String mixinConfig : config.getMixins()) {
        Mixins.addConfiguration(mixinConfig);
      }

      this.core.getLogger().info("Applied [{}] transformations.", container.getId());
    }
  }

  public @NonNull List<ModResource> getCandidates() {
    return this.modResources;
  }

  public @NonNull List<ModContainer> getContainers() {
    return this.modContainers;
  }
}

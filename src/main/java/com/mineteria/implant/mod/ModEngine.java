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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    this.modResources.addAll(this.locator.locateResources());
  }

  /**
   * Load the mods and initializes them from the resources list.
   */
  public void loadCandidates() {
    for (final ModResource resource : this.modResources) {
      final Path resourcePath = resource.getPath();

      this.core.getLogger().debug("Scanning mod candidate '{}' for mod configuration!", resourcePath);

      try (final ZipFile zipFile = new ZipFile(resourcePath.toFile())) {
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
          final ZipEntry entry = entries.nextElement();

          // Skip directories.
          if (entry.isDirectory()) continue;

          // Look for the config.
          if (entry.getName().equalsIgnoreCase("mod.json")) {
            final JsonReader reader = new JsonReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8));
            final ModConfig config = this.core.getGson().fromJson(reader, ModConfig.class);

            this.modContainers.add(new ModContainer(config.getId(), resource, config));
          }
        }
      } catch (final IOException exception) {
        this.core.getLogger().warn("Failed to open '{}'!", resourcePath);
      }
    }
  }

  public void loadContainers() {
    for (final ModContainer container : this.modContainers) {
      final ModConfig config = container.getConfig();

      // Add the mixin configurations.
      for (final String mixinConfig : config.getMixins()) {
        Mixins.addConfiguration(mixinConfig);
      }
    }
  }

  public @NonNull List<ModResource> getCandidates() {
    return this.modResources;
  }

  public @NonNull List<ModContainer> getContainers() {
    return this.modContainers;
  }
}

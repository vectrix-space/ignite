package com.mineteria.implant.launcher.mod;

import com.google.gson.stream.JsonReader;
import com.mineteria.implant.launcher.ImplantCore;
import com.mineteria.implant.launcher.util.ClassLoaderUtil;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ModEngine {
  private final ModLocator locator = new ModLocator();
  private final List<ModResource> modCandidates = new ArrayList<>();
  private final List<ModContainer> modContainers = new ArrayList<>();

  private final ImplantCore core;

  public ModEngine(final ImplantCore core) {
    this.core = core;
  }

  /**
   * Locates and populates the mod candidates list.
   */
  public void loadCandidates() {
    this.modCandidates.addAll(this.locator.locateResources());
  }

  /**
   * Load the mods and initializes them from the candidates list.
   */
  public void loadMods() {
    for (final ModResource resource : this.modCandidates) {
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

            this.modContainers.add(new ModContainer(resource, config));
          }
        }
      } catch (final IOException exception) {
        this.core.getLogger().warn("Failed to open '{}'!", resourcePath);
      }
    }
  }

  public void transformMods(final @NonNull ITransformingClassLoader classLoader) {
    for (final ModContainer container : this.modContainers) {
      final ModResource resource = container.getResource();
      final ModConfig config = container.getConfig();
      final Path resourcePath = resource.getPath();

      // Load the target resource.
      ClassLoaderUtil.toUrl(resourcePath).ifPresent(url -> ClassLoaderUtil.loadJar(classLoader.getInstance(), url));

      // Add the mixin configurations.
      for (final String mixinConfig : config.getMixins()) {
        Mixins.addConfiguration(mixinConfig);
      }

//      try (final JarFile jarFile = new JarFile(resourcePath.toFile())) {
//      } catch (final IOException exception) {
//        this.core.getLogger().warn("Failed to open '{}'!", resourcePath);
//      }
    }
  }

  public @NonNull List<ModResource> getCandidates() {
    return this.modCandidates;
  }

  public @NonNull List<ModContainer> getContainers() {
    return modContainers;
  }
}

/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) Mineteria <https://mineteria.com/>
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
package com.mineteria.ignite.applaunch.mod;

import com.google.common.collect.Maps;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.api.mod.ModResource;
import com.mineteria.ignite.applaunch.util.IgniteConstants;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModEngine {
  private final Logger logger = LogManager.getLogger("IgniteEngine");
  private final ModResourceLocator resourceLocator = new ModResourceLocator();
  private final ModResourceLoader resourceLoader = new ModResourceLoader();
  private final Map<String, ModContainer> containers = new HashMap<>();
  private final List<ModResource> resources = new ArrayList<>();

  public ModEngine() {}

  public final @NonNull Logger getLogger() {
    return this.logger;
  }

  public final @NonNull ModResourceLocator getResourceLocator() {
    return this.resourceLocator;
  }

  public final @NonNull List<ModResource> getResources() {
    return this.resources;
  }

  public final @NonNull Collection<ModContainer> getContainers() {
    return this.containers.values();
  }

  public final boolean hasContainer(final @NonNull String id) {
    return this.containers.containsKey(id);
  }

  /**
   * Locates and populates the mod resources list.
   */
  public boolean locateResources() {
    return this.resources.addAll(this.resourceLocator.locateResources(this));
  }

  /**
   * Loads the located resources, adds them to the containers map
   * and returns a list of resource paths.
   */
  public @NonNull List<Map.Entry<String, Path>> loadResources() {
    final List<Map.Entry<String, Path>> targetResources = new ArrayList<>();
    for (final ModContainer container : this.resourceLoader.loadResources(this)) {
      final ModResource resource = container.getResource();

      this.containers.put(container.getId(), container);

      final Map.Entry<String, Path> entry = Maps.immutableEntry(resource.getPath().getFileName().toString(), resource.getPath());
      targetResources.add(entry);
    }

    this.getLogger().info("Located " + this.containers.size() + " mod(s).");

    return targetResources;
  }

  /**
   * Loads the mod transformers.
   */
  public void loadTransformers(final @NonNull IEnvironment environment) {
    final ILaunchPluginService accessTransformer = environment.findLaunchPlugin(IgniteConstants.AT_SERVICE).orElse(null);

    for (final ModContainer container : this.getContainers()) {
      final ModResource resource = container.getResource();

      // Access Transformer
      if (accessTransformer != null) {
        final String atFiles = resource.getManifest().getMainAttributes().getValue(IgniteConstants.AT);
        if (atFiles != null) {
          for (final String atFile : atFiles.split(",")) {
            if (!atFile.endsWith(".cfg")) continue;

            accessTransformer.offerResource(resource.getFileSystem().getPath(IgniteConstants.META_INF).resolve(atFile), atFile);
          }
        }
      }
    }

    this.getLogger().info("Applied access transformer(s).");
  }

  public void loadMixins() {
    for (final ModContainer container : this.getContainers()) {
      // Mixins
      final List<String> mixins = container.getConfig().getMixins();
      if (mixins != null && !mixins.isEmpty()) {
        Mixins.addConfigurations(mixins.toArray(new String[0]));
      }
    }

    this.getLogger().info("Applied mixin transformer(s).");
  }
}

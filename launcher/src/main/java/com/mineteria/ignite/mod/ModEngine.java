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
package com.mineteria.ignite.mod;

import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.api.mod.ModResource;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ModEngine {
  private final ModResourceLocator resourceLocator = new ModResourceLocator();
  private final ModResourceLoader resourceLoader = new ModResourceLoader();
  private final ModLoader containerLoader = new ModLoader();
  private final Map<Object, ModContainer> containerInstances = new IdentityHashMap<>();
  private final Map<String, ModContainer> containers = new HashMap<>();
  private final List<ModResource> resources = new ArrayList<>();

  private final Logger logger;

  public ModEngine(final @NonNull Logger logger) {
    this.logger = logger;
  }

  public @NonNull Logger getLogger() {
    return this.logger;
  }

  public @NonNull ModResourceLocator getResourceLocator() {
    return this.resourceLocator;
  }

  public @NonNull List<ModResource> getResources() {
    return this.resources;
  }

  public @NonNull Collection<ModContainer> getContainers() {
    return this.containers.values();
  }

  public boolean hasContainer(final @NonNull String id) {
    return this.containers.containsKey(id);
  }

  /**
   * Locates and populates the mod resources list.
   */
  public void locateResources() {
    this.logger.info("Scanning for mods...");

    this.resources.addAll(this.resourceLocator.locateResources(this));
  }

  /**
   * Loads the located resources and adds them to the containers map.
   */
  public void loadResources() {
    for (final ModContainer container : this.resourceLoader.loadResources(this)) {
      this.containers.put(container.getId(), container);
    }

    this.logger.info("Located {} mod(s).", this.containers.size());
  }

  /**
   * Loads the target instance for the mod containers.
   */
  public void loadContainers() {
    this.containerLoader.loadContainers(this, this.containerInstances);

    this.logger.info("Loaded [{}] mod(s).", this.containers.values().stream()
      .map(ModContainer::toString)
      .collect(Collectors.joining(", "))
    );
  }
}

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
package com.mineteria.ignite.launch;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.path.ConfigsPath;
import com.mineteria.ignite.api.config.path.ModsPath;
import com.mineteria.ignite.api.event.EventManager;
import com.mineteria.ignite.api.mod.ModManager;
import com.mineteria.ignite.launch.event.IgniteEventManager;
import com.mineteria.ignite.launch.mod.IgniteModManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public final class IgnitePlatform implements Platform {
  private final Logger logger = LogManager.getLogger("IgnitePlatform");

  private final ModManager modManager;
  private final EventManager eventManager;
  private final Path configs;
  private final Path mods;

  @Inject
  public IgnitePlatform(final @NonNull @ConfigsPath Path configs,
                        final @NonNull @ModsPath Path mods) {
    this.mods = mods;
    this.configs = configs;

    this.modManager = new IgniteModManager(this);
    this.eventManager = new IgniteEventManager(this);
  }

  public Logger getLogger() {
    return this.logger;
  }

  @Override
  public final @NonNull ModManager getModManager() {
    return this.modManager;
  }

  @Override
  public final @NonNull EventManager getEventManager() {
    return this.eventManager;
  }

  @Override
  public final @NonNull Path getConfigs() {
    return this.configs;
  }

  @Override
  public final @NonNull Path getMods() {
    return this.mods;
  }
}

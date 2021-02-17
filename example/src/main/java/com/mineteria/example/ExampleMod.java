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
package com.mineteria.example;

import com.google.inject.Inject;
import com.mineteria.ignite.api.Platform;
import com.mineteria.ignite.api.config.path.ConfigPath;
import com.mineteria.ignite.api.event.Subscribe;
import com.mineteria.ignite.api.event.platform.PlatformInitializeEvent;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public final class ExampleMod {
  private final Logger logger;
  private final Platform platform;
  private final Path configurationPath;

  @Inject
  public ExampleMod(final Logger logger,
                    final Platform platform,
                    final @ConfigPath Path configurationPath) {
    this.logger = logger;
    this.platform = platform;
    this.configurationPath = configurationPath;
  }

  @Subscribe
  public void onInitialize(final @NonNull PlatformInitializeEvent event) {
    this.logger.info("Hello Initialization!");
  }
}

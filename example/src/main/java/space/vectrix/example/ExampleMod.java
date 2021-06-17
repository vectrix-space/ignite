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
package space.vectrix.example;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import space.vectrix.ignite.api.Platform;
import space.vectrix.ignite.api.config.Configuration;
import space.vectrix.ignite.api.config.Configurations;
import space.vectrix.ignite.api.event.Subscribe;
import space.vectrix.ignite.api.event.platform.PlatformInitializeEvent;

public final class ExampleMod {
  private final Logger logger;
  private final Platform platform;

  @Inject
  public ExampleMod(final Logger logger,
                    final Platform platform) {
    this.logger = logger;
    this.platform = platform;
  }

  @Subscribe
  public void onInitialize(final @NonNull PlatformInitializeEvent event) {
    this.logger.info("Hello Example!");

    final Configuration<ExampleConfig, CommentedConfigurationNode> configWrapper = Configurations.getOrCreate(Configurations.HOCON_LOADER, ExampleInfo.getExampleConfig());
    final ExampleConfig config = configWrapper.instance();
    if (config != null) {
      this.logger.info("Foo is set to: " + (config.container.foo ? "Enabled" : "Disabled"));
    }
  }
}

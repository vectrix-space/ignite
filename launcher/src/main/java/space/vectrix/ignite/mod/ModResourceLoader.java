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
package space.vectrix.ignite.mod;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Represents the mod resource loader.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class ModResourceLoader {
  /* package */ @NotNull List<ModContainerImpl> loadResources(final @NotNull ModsImpl engine) {
    final List<ModContainerImpl> containers = new ArrayList<>();

    for(final ModResource resource : engine.resources()) {
      if(resource.locator().equals(ModResourceLocator.LAUNCHER_LOCATOR) || resource.locator().equals(ModResourceLocator.GAME_LOCATOR)) {
        final ModConfig config = new ModConfig(
          IgniteConstants.API_TITLE,
          IgniteConstants.API_VERSION
        );

        containers.add(new ModContainerImpl(Logger.tag(config.id()), resource, config));
        continue;
      }

      try(final InputStream inputStream = resource.loadResource(IgniteConstants.MOD_CONFIG)) {
        if(inputStream == null) continue;

        final ModConfig config = IgniteConstants.GSON.fromJson(new InputStreamReader(inputStream), ModConfig.class);

        containers.add(new ModContainerImpl(Logger.tag(config.id()), resource, config));
      } catch(final IOException exception) {
        // Ignore
      }
    }

    return containers;
  }
}

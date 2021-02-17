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
package com.mineteria.ignite.applaunch.handler;

import com.mineteria.ignite.api.mod.ModResource;
import com.mineteria.ignite.api.IgniteBlackboard;
import com.mineteria.ignite.applaunch.IgniteBootstrap;
import com.mineteria.ignite.applaunch.mod.ModEngine;
import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.jar.Manifest;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class IgniteLaunchService implements ILaunchHandlerService {
  private static final Optional<Manifest> UNKNOWN_MANIFEST = Optional.of(new Manifest());

  /**
   * A list of class loader exclusions to ignore when
   * transforming classes.
   */
  protected static final @NonNull List<String> EXCLUDED_PACKAGES = Arrays.asList(
    // Ignite
    "com.mineteria.ignite.api.",
    "com.mineteria.ignite.applaunch.",
    "com.mineteria.ignite.relocate.",

    // Logging
    "org.apache.logging.log4j.",
    "org.checkerframework.",
    "net.minecrell.terminalconsole.",
    "org.jline.",
    "com.sun.jna.",

    // Configuration
    "ninja.leaping.configurate.",
    "com.typesafe.config.",
    "com.google.gson.",
    "org.yaml.snakeyaml.",

    // Common
    "com.google.common.",
    "com.google.inject.",
    "javax.annotation.",
    "javax.inject.",
    "org.aopalliance.",

    // ASM
    "org.objectweb.asm.",

    // Mixin
    "org.spongepowered.asm.",

    // Access Transformers
    "net.minecraftforge.accesstransformer.",
    "org.antlr.v4.runtime.",

    // Core
    "joptsimple."
  );

  private final Logger logger = LogManager.getLogger("IgniteLaunch");
  private final ConcurrentMap<URL, Optional<Manifest>> manifestCache = new ConcurrentHashMap<>();

  @Override
  public final @NonNull String name() {
    return "ignite_launch";
  }

  @Override
  public void configureTransformationClassLoader(final @NonNull ITransformingClassLoaderBuilder builder) {
    for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
      if (url.toString().contains("mixin") && url.toString().endsWith(".jar")) {
        continue;
      }

      try {
        builder.addTransformationPath(Paths.get(url.toURI()));
      } catch (final URISyntaxException exception) {
        this.logger.error("Failed to add Mixin transformation path", exception);
      }
    }

    builder.setResourceEnumeratorLocator(this.getResourceLocator());
    builder.setManifestLocator(this.getManifestLocator());
  }

  @Override
  public final @NonNull Callable<Void> launchService(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {
    IgniteBootstrap.getInstance().getModEngine().loadTransformers();

    this.logger.info("Transitioning to launch target, please wait...");

    launchClassLoader.addTargetPackageFilter(other -> IgniteLaunchService.EXCLUDED_PACKAGES.stream().noneMatch(other::startsWith));

    return () -> {
      this.launchService0(arguments, launchClassLoader);
      return null;
    };
  }

  protected final @NonNull Function<String, Enumeration<URL>> getResourceLocator() {
    return string -> {
      // Save unnecessary searches of mod classes for things that are definitely not mods
      // In this case: MC and fastutil
      if (string.startsWith("net/minecraft") || string.startsWith("it/unimi")) {
        return Collections.emptyEnumeration();
      }

      return new Enumeration<URL>() {
        private final @NonNull Iterator<ModResource> resources = IgniteBootstrap.getInstance().getModEngine().getResources().iterator();

        private @Nullable URL next = this.computeNext();

        @Override
        public final boolean hasMoreElements() {
          return this.next != null;
        }

        @Override
        public final @NonNull URL nextElement() {
          final URL next = this.next;
          if (next == null) throw new NoSuchElementException();
          this.next = this.computeNext();
          return next;
        }

        private URL computeNext() {
          while (this.resources.hasNext()) {
            final ModResource resource = this.resources.next();
            final Path resolved = resource.getFileSystem().getPath(string);
            if (Files.exists(resolved)) {
              try {
                return resolved.toUri().toURL();
              } catch (final MalformedURLException ex) {
                throw new RuntimeException(ex);
              }
            }
          }

          return null;
        }
      };
    };
  }

  protected final @NonNull Function<URLConnection, Optional<Manifest>> getManifestLocator() {
    return connection -> {
      if (connection instanceof JarURLConnection) {
        final URL jarUrl = ((JarURLConnection) connection).getJarFileURL();
        final Optional<Manifest> manifest = this.manifestCache.computeIfAbsent(jarUrl, key -> {
          for (final ModResource resource : IgniteBootstrap.getInstance().getModEngine().getResources()) {
            try {
              if (resource.getPath().toAbsolutePath().normalize().equals(Paths.get(key.toURI()).toAbsolutePath().normalize())) {
                return Optional.of(resource.getManifest());
              }
            } catch (final URISyntaxException exception) {
              this.logger.error("Failed to load manifest from jar '{}'!", key, exception);
            }
          }

          return IgniteLaunchService.UNKNOWN_MANIFEST;
        });

        try {
          if (manifest == IgniteLaunchService.UNKNOWN_MANIFEST) {
            return Optional.ofNullable(((JarURLConnection) connection).getManifest());
          } else {
            return manifest;
          }
        } catch (final IOException exception) {
          this.logger.error("Failed to load manifest from jar '{}'!", jarUrl, exception);
        }
      }

      return Optional.empty();
    };
  }

  /**
   * Launch the service (Minecraft).
   *
   * @param arguments The arguments to launch the service with
   * @param launchClassLoader The transforming class loader to load classes with
   */
  protected void launchService0(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) throws Exception {
    final Path launchJar = IgniteBlackboard.getProperty(IgniteBlackboard.LAUNCH_JAR);
    if (launchJar == null || !Files.exists(launchJar)) {
      throw new IllegalStateException("No launch jar was found!");
    } else {
      // Invoke the main method on the provided ClassLoader.
      Class.forName("com.mineteria.ignite.launch.IgniteLaunch", true, launchClassLoader.getInstance())
        .getMethod("main", ModEngine.class, String[].class)
        .invoke(null, IgniteBootstrap.getInstance().getModEngine(), arguments);
    }
  }
}

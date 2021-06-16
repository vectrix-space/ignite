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
package space.vectrix.ignite.applaunch.handler;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Blackboard;
import space.vectrix.ignite.api.mod.ModResource;
import space.vectrix.ignite.applaunch.IgniteBootstrap;
import space.vectrix.ignite.applaunch.mod.ModEngine;
import space.vectrix.ignite.applaunch.util.IgniteConstants;
import space.vectrix.ignite.applaunch.util.IgniteExclusions;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.jar.Manifest;

public final class IgniteLaunchService implements ILaunchHandlerService {
  private final ConcurrentMap<String, Manifest> manifests = new ConcurrentHashMap<>();
  private final Logger logger = LogManager.getLogger("Ignite Launch");

  @Override
  public final @NonNull String name() {
    return IgniteConstants.IGNITE_LAUNCH_SERVICE;
  }

  @Override
  public void configureTransformationClassLoader(final @NonNull ITransformingClassLoaderBuilder builder) {
    for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
      // Exclude mixin from transformations.
      final String target = url.toString();
      if (target.contains("mixin") && target.endsWith(".jar")) {
        continue;
      }

      try {
        builder.addTransformationPath(Paths.get(url.toURI()));
      } catch (final URISyntaxException exception) {
        this.logger.error("Failed to add transformation path for '" + target + "'!", exception);
      }
    }

    builder.setResourceEnumeratorLocator(this.getResourceLocator());
    builder.setManifestLocator(this.getManifestLocator());
  }

  @Override
  public final @NonNull Callable<Void> launchService(final @NonNull String @NonNull [] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {
    launchClassLoader.addTargetPackageFilter(packageLocation -> IgniteExclusions.getExclusions().stream()
      .map(IgniteExclusions.Exclusion::getPackageExclusion)
      .filter(Objects::nonNull)
      .noneMatch(packageLocation::startsWith));

    return () -> {
      this.launchService0(arguments, launchClassLoader);
      return null;
    };
  }

  protected final @NonNull Function<String, Enumeration<URL>> getResourceLocator() {
    return resourceLocation -> {
      if (IgniteExclusions.getExclusions().stream()
        .map(IgniteExclusions.Exclusion::getResourceExclusion)
        .filter(Objects::nonNull)
        .anyMatch(resourceLocation::startsWith)) {
        return Collections.emptyEnumeration();
      }

      return new Enumeration<URL>() {
        private final Iterator<ModResource> resources = IgniteBootstrap.getInstance().getModEngine().getResources().iterator();
        private URL next = this.computeNext();

        @Override
        public final boolean hasMoreElements() {
          return this.next != null;
        }

        @Override
        public final @NonNull URL nextElement() {
          final URL current = this.next;
          if (current == null) throw new NoSuchElementException();
          this.next = this.computeNext();
          return current;
        }

        private URL computeNext() {
          ModResource resource = null;
          while (this.resources.hasNext() && resource == null) {
            final Path resolved = (resource = this.resources.next()).getFileSystem().getPath(resourceLocation);
            if (Files.exists(resolved)) {
              try {
                return resolved.toUri().toURL();
              } catch (final MalformedURLException exception) {
                IgniteLaunchService.this.logger.error("Failed to compute resource path for '" + resolved + "'!", exception);
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
        final JarURLConnection jarConnection = (JarURLConnection) connection;
        final URL url = jarConnection.getJarFileURL();
        final String target = url.toString();

        final Manifest manifest = this.manifests.computeIfAbsent(target, key -> {
          for (final ModResource resource : IgniteBootstrap.getInstance().getModEngine().getResources()) {
            try {
              if (resource.getPath().toAbsolutePath().normalize().equals(Paths.get(url.toURI()).toAbsolutePath().normalize())) {
                return resource.getManifest();
              }
            } catch (final URISyntaxException exception) {
              this.logger.error("Failed to load manifest from resource for '" + target + "'!", exception);
            }
          }

          try {
            return jarConnection.getManifest();
          } catch (final IOException exception) {
            this.logger.error("Failed to load manifest from connection for '" + target + "'!", exception);
          }

          return null;
        });

        return Optional.ofNullable(manifest);
      }

      return Optional.empty();
    };
  }

  /**
   * Launch the service.
   *
   * @param arguments The arguments to launch the service with
   * @param launchClassLoader The transforming class loader to load classes with
   */
  protected void launchService0(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) throws Exception {
    final Path launchJar = Blackboard.getProperty(Blackboard.LAUNCH_JAR);
    if (launchJar != null && Files.exists(launchJar)) {
      // Invoke the main method on the provided ClassLoader.
      Class.forName("space.vectrix.ignite.launch.IgniteLaunch", true, launchClassLoader.getInstance())
        .getMethod("main", ModEngine.class, String[].class)
        .invoke(null, IgniteBootstrap.getInstance().getModEngine(), arguments);
    } else {
      throw new IllegalStateException("No launch jar was found!");
    }
  }
}

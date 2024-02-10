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
package space.vectrix.ignite.launch;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.IgniteBootstrap;
import space.vectrix.ignite.launch.ember.EmberClassLoader;
import space.vectrix.ignite.launch.ember.EmberTransformer;
import space.vectrix.ignite.launch.ember.LaunchService;
import space.vectrix.ignite.mod.ModResource;
import space.vectrix.ignite.mod.ModResourceLocator;
import space.vectrix.ignite.mod.ModsImpl;
import space.vectrix.ignite.util.ClassLoaders;
import space.vectrix.ignite.util.IgniteExclusions;

/**
 * Provides the launch handling for Ignite to Ember.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class LaunchImpl implements LaunchService {
  private static final String JAVA_HOME = System.getProperty("java.home");
  private static final @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Manifest> DEFAULT_MANIFEST = Optional.of(new Manifest());

  private final ConcurrentMap<String, Optional<Manifest>> manifests = new ConcurrentHashMap<>();

  @Override
  public void initialize() {
    // Initialize the mod engine.
    final ModsImpl engine = IgniteBootstrap.instance().engine();
    if(engine.locateResources()) {
      final Set<String> names = engine.resolveResources().stream()
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());

      Logger.info("Found {} mod(s): {}", names.size(), String.join(", ", names));
    }
  }

  @Override
  public void configure(final @NotNull EmberClassLoader classLoader, final @NotNull EmberTransformer transformer) {
    for(final URL url : ClassLoaders.systemClassPaths()) {
      try {
        final URI uri = url.toURI();
        if(!this.transformable(uri)) {
          Logger.debug("Skipped adding transformation path for: {}", url);
          continue;
        }

        classLoader.addTransformationPath(Paths.get(url.toURI()));
        Logger.debug("Added transformation path for: {}", url);
      } catch(final URISyntaxException | IOException exception) {
        Logger.error(exception, "Failed to add transformation path for: {}", url);
      }
    }

    classLoader.addTransformationFilter(this.packageFilter());
    classLoader.addManifestLocator(this.manifestLocator());
    transformer.addResourceExclusion(this.resourceFilter());
  }

  @Override
  public void prepare(final @NotNull EmberTransformer transformer) {
    final ModsImpl engine = IgniteBootstrap.instance().engine();

    // Resolve the wideners.
    engine.resolveWideners(transformer);

    // Resolve the mixins.
    engine.resolveMixins();
  }

  @Override
  public @NotNull Callable<Void> launch(final @NotNull String@NotNull [] arguments, final @NotNull EmberClassLoader loader) {
    return () -> {
      final Path gameJar = Blackboard.raw(Blackboard.GAME_JAR);
      final String gameTarget = Blackboard.raw(Blackboard.GAME_TARGET);
      if(gameJar != null && Files.exists(gameJar)) {
        // Invoke the main method.
        Class.forName(gameTarget, true, loader)
          .getMethod("main", String[].class)
          .invoke(null, (Object) arguments);
      } else {
        throw new IllegalStateException("No game jar was found to launch!");
      }

      return null;
    };
  }

  private @NotNull Predicate<String> packageFilter() {
    return name -> {
      for(final String test : IgniteExclusions.TRANSFORMATION_EXCLUDED_PACKAGES) {
        if(name.startsWith(test)) {
          return false;
        }
      }

      return true;
    };
  }

  private @NotNull Predicate<String> resourceFilter() {
    return path -> {
      for(final String test : IgniteExclusions.TRANSFORMATION_EXCLUDED_RESOURCES) {
        if(path.startsWith(test)) {
          return false;
        }
      }

      return true;
    };
  }

  private @NotNull Function<URLConnection, Optional<Manifest>> manifestLocator() {
    final ModsImpl engine = IgniteBootstrap.instance().engine();

    return connection -> {
      if(connection instanceof JarURLConnection) {
        final URL url = ((JarURLConnection) connection).getJarFileURL();
        final Optional<Manifest> manifest = this.manifests.computeIfAbsent(url.toString(), key -> {
          for(final ModResource resource : engine.resources()) {
            if(!resource.locator().equals(ModResourceLocator.JAVA_LOCATOR)) {
              continue;
            }

            try {
              if(resource.path().toAbsolutePath().normalize().equals(Paths.get(url.toURI()).toAbsolutePath().normalize())) {
                return Optional.ofNullable(resource.manifest());
              }
            } catch(final URISyntaxException exception) {
              Logger.error(exception, "Failed to load manifest from jar: {}", url);
            }
          }

          return LaunchImpl.DEFAULT_MANIFEST;
        });

        try {
          if(manifest == LaunchImpl.DEFAULT_MANIFEST) {
            return Optional.ofNullable(((JarURLConnection) connection).getManifest());
          } else {
            return manifest;
          }
        } catch(final IOException exception) {
          Logger.error(exception, "Failed to load manifest from connection for: {}", url);
        }
      }

      return Optional.empty();
    };
  }

  private boolean transformable(final @NotNull URI uri) throws URISyntaxException, IOException {
    final File target = new File(uri);

    // Ensure JVM internals are not transformable.
    if(target.getAbsolutePath().startsWith(LaunchImpl.JAVA_HOME)) {
      return false;
    }

    if(target.isDirectory()) {
      for(final String test : IgniteExclusions.TRANSFORMATION_EXCLUDED_RESOURCES) {
        if(new File(target, test).exists()) {
          return false;
        }
      }
    } else if(target.isFile()) {
      try(final JarFile jarFile = new JarFile(new File(uri))) {
        for(final String test : IgniteExclusions.TRANSFORMATION_EXCLUDED_RESOURCES) {
          if(jarFile.getEntry(test) != null) {
            return false;
          }
        }
      }
    }

    return true;
  }
}

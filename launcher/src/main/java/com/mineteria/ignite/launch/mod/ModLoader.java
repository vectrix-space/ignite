package com.mineteria.ignite.launch.mod;

import com.google.inject.Injector;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.applaunch.agent.Agent;
import com.mineteria.ignite.launch.IgniteLaunch;
import com.mineteria.ignite.launch.IgnitePlatform;
import com.mineteria.ignite.launch.inject.ModModule;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ModLoader {
  public final @NonNull List<ModContainer> loadContainers(final @NonNull IgnitePlatform platform,
                                                          final @NonNull Collection<ModContainer> pending,
                                                          final @NonNull Map<String, ModContainer> identifierTarget,
                                                          final @NonNull Map<Object, ModContainer> instanceTarget) {
    final List<ModContainer> ordered;
    try {
      ordered = ModDependencyResolver.resolveDependencies(platform, pending);
    } catch (final IllegalStateException exception) {
      platform.getLogger().error("Unable to generate mod dependency graph!");
      platform.getLogger().error("\n", exception);
      return Collections.emptyList();
    }

    for (final ModContainer container : ordered) {
      try {
        // Instantiate the container.
        final Object modInstance = this.instantiateContainer(container);
        identifierTarget.put(container.getId(), container);

        if (modInstance != null) {
          instanceTarget.put(modInstance, container);

          // Register the instance events.
          try {
            platform.getEventManager().register(modInstance, modInstance);
          } catch (final Exception exception) {
            throw new IllegalStateException("Unable to register mod listeners!", exception);
          }
        } else {
          platform.getLogger().warn("Loaded '{}' without a target class.", container.getId());
        }
      } catch (final Exception exception) {
        platform.getLogger().error("Failed to load mod '{}'!", container.getId());
        platform.getLogger().error("\n", exception);
      }
    }

    return ordered;
  }

  private Object instantiateContainer(final ModContainer container) throws IllegalStateException {
    try {
      // Add the resource to the class loader.
      final URL modJar = container.getResource().getPath().toUri().toURL();
      final ModClassLoader classLoader = new ModClassLoader(new URL[] { modJar });
      classLoader.addLoaders();

      final String targetClass = container.getConfig().getTarget();
      if (targetClass != null) {
        // Load the class.
        final Class<?> modClass = classLoader.loadClass(targetClass);

        final Injector parentInjector = IgniteLaunch.getInstance().getInjector();
        if (parentInjector != null) {
          final Injector childInjector = parentInjector.createChildInjector(new ModModule(container, modClass));
          return childInjector.getInstance(modClass);
        }

        return modClass.newInstance();
      }

      return null;
    } catch (final Throwable throwable) {
      throw new IllegalStateException("An error occurred attempting to create an instance of mod '" + container.toString() + "'!", throwable);
    }
  }
}

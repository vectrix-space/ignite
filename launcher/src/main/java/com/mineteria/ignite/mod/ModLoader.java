package com.mineteria.ignite.mod;

import com.google.inject.Injector;
import com.mineteria.ignite.agent.Agent;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.inject.ModModule;
import com.mineteria.ignite.launch.IgniteBlackboard;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixins;

import java.util.List;
import java.util.Map;

public final class ModLoader {
  public void loadContainers(final @NonNull ModEngine engine, final @NonNull Map<Object, ModContainer> target) {
    final List<ModContainer> ordered;
    try {
      ordered = ModDependencyResolver.resolveDependencies(engine, engine.getContainers());
    } catch (final IllegalStateException exception) {
      engine.getLogger().error("Unable to generate mod dependency graph!");
      engine.getLogger().error("\n", exception);
      return;
    }

    for (final ModContainer container : ordered) {
      try {
        // Add the resource to the class loader.
        Agent.addJar(container.getResource().getPath());

        // Instantiate the container.
        final Object modInstance = this.instantiateContainer(container);
        if (modInstance != null) {
          target.put(modInstance, container);

          // Register the instance events.
          try {
            engine.getEventManager().register(modInstance, modInstance);
          } catch (final Exception exception) {
            throw new IllegalStateException("Unable to register mod listeners!");
          }
        }
      } catch (final Exception exception) {
        engine.getLogger().error("Failed to load mod '{}'!", container.getId());
        engine.getLogger().error("\n", exception);
      }
    }
  }

  public void loadMods(final @NonNull ModEngine engine) {
    for (final ModContainer container : engine.getContainers()) {
      final List<String> mixins = container.getConfig().getRequiredMixins();
      if (mixins != null && !mixins.isEmpty()) {
        for (final String mixinConfig : mixins) {
          Mixins.addConfiguration(mixinConfig);
        }
      }
    }
  }

  private @Nullable Object instantiateContainer(final @NonNull ModContainer container) throws IllegalStateException {
    try {
      final String targetClass = container.getConfig().getTarget();
      if (targetClass != null) {
        final Class<?> modClass = Class.forName(targetClass, true, ClassLoader.getSystemClassLoader());

        final Injector parentInjector = IgniteBlackboard.getProperty(IgniteBlackboard.PARENT_INJECTOR);
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

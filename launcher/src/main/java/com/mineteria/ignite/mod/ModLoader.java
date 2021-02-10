package com.mineteria.ignite.mod;

import com.google.inject.Injector;
import com.mineteria.ignite.agent.Agent;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.inject.ModModule;
import com.mineteria.ignite.launch.IgniteBlackboard;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

public final class ModLoader {
  public void loadContainers(final @NonNull ModEngine engine, final @NonNull Map<Object, ModContainer> target) {
    for (final ModContainer container : engine.getContainers()) {
      try {
        Agent.addJar(container.getResource().getPath());

        final Object modInstance = this.initializeContainer(container);
        if (modInstance != null) target.put(modInstance, container);
      } catch (final Exception exception) {
        engine.getLogger().error("Failed to load mod!", exception);
      }
    }
  }

  public @Nullable Object initializeContainer(final @NonNull ModContainer container) throws IllegalStateException {
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

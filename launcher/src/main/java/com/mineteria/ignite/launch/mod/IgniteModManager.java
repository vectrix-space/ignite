package com.mineteria.ignite.launch.mod;

import static java.util.Objects.requireNonNull;

import com.mineteria.ignite.api.event.platform.PlatformConstructEvent;
import com.mineteria.ignite.api.event.platform.PlatformInitializeEvent;
import com.mineteria.ignite.api.mod.ModContainer;
import com.mineteria.ignite.api.mod.ModManager;
import com.mineteria.ignite.applaunch.mod.ModEngine;
import com.mineteria.ignite.launch.IgnitePlatform;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class IgniteModManager implements ModManager {
  private final Map<Object, ModContainer> containerInstances = new IdentityHashMap<>();
  private final List<ModContainer> containersSorted = new ArrayList<>();
  private final Map<String, ModContainer> containers = new HashMap<>();
  private final ModLoader containerLoader = new ModLoader();
  private final IgnitePlatform platform;

  public IgniteModManager(final @NonNull IgnitePlatform platform) {
    this.platform = platform;
  }

  @Override
  public @NonNull Optional<ModContainer> getContainer(final @NonNull String mod) {
    requireNonNull(mod, "mod");
    return Optional.ofNullable(this.containers.get(mod));
  }

  @Override
  public @NonNull Optional<ModContainer> getContainer(final @NonNull Object modInstance) {
    requireNonNull(modInstance, "modInstance");
    return Optional.ofNullable(this.containerInstances.get(modInstance));
  }

  @Override
  public boolean isLoaded(final @NonNull String mod) {
    requireNonNull(mod, "mod");
    return this.containers.containsKey(mod);
  }

  @Override
  public boolean isInstance(final @NonNull Object modInstance) {
    requireNonNull(modInstance, "modInstance");
    return this.containerInstances.containsKey(modInstance);
  }

  @Override
  public @NonNull Collection<ModContainer> getContainers() {
    return Collections.unmodifiableList(this.containersSorted);
  }

  public void loadPlugins(final @NonNull ModEngine engine) {
    this.containersSorted.addAll(this.containerLoader.loadContainers(this.platform, engine.getContainers(), this.containers, this.containerInstances));

    this.platform.getEventManager().post(new PlatformConstructEvent());

    this.platform.getLogger().info("Constructed [{}] mod(s).", this.containers.values().stream()
      .map(ModContainer::toString)
      .collect(Collectors.joining(", "))
    );

    this.platform.getEventManager().post(new PlatformInitializeEvent());

    this.platform.getLogger().info("Initialized mod(s).");
  }
}

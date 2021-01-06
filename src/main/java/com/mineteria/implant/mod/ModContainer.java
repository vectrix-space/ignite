package com.mineteria.implant.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class ModContainer {
  private final String id;
  private final ModResource resource;
  private final ModConfig config;

  public ModContainer(final @NonNull String id,
                      final @NonNull ModResource resource,
                      final @NonNull ModConfig config) {
    this.id = id;
    this.resource = resource;
    this.config = config;
  }

  public String getId() {
    return this.id;
  }

  public @NonNull ModResource getResource() {
    return this.resource;
  }

  public @NonNull ModConfig getConfig() {
    return this.config;
  }
}

package com.mineteria.implant.launcher.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class ModContainer {
  private final ModResource resource;
  private final ModConfig config;

  public ModContainer(final @NonNull ModResource resource, final @NonNull ModConfig config) {
    this.resource = resource;
    this.config = config;
  }

  public @NonNull ModResource getResource() {
    return this.resource;
  }

  public @NonNull ModConfig getConfig() {
    return this.config;
  }
}

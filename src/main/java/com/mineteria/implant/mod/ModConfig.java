package com.mineteria.implant.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public final class ModConfig {
  private String id;
  private List<String> mixins;

  public ModConfig() {}

  public ModConfig(final @NonNull String id,
                   final @NonNull List<String> mixins) {
    this.id = id;
    this.mixins = mixins;
  }

  public @NonNull String getId() {
    return id;
  }

  public @NonNull List<String> getMixins() {
    return this.mixins;
  }
}

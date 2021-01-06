package com.mineteria.implant.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public final class ModConfig {
  private String id;
  private List<String> mixins;
  private List<String> transformers;

  public ModConfig() {}

  public ModConfig(final @NonNull String id,
                   final @NonNull List<String> mixins,
                   final @NonNull List<String> transformers) {
    this.id = id;
    this.mixins = mixins;
    this.transformers = transformers;
  }

  public List<String> getMixins() {
    return this.mixins;
  }

  public List<String> getTransformers() {
    return this.transformers;
  }
}

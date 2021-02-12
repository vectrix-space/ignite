package com.mineteria.example.mixin.plugins;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class CorePlugin implements IMixinConfigPlugin {
  @Override
  public void onLoad(final @NonNull String mixinPackage) {
  }

  @Override
  public final @Nullable String getRefMapperConfig() {
    return null;
  }

  @Override
  public final boolean shouldApplyMixin(final @NonNull String targetClassName, final @NonNull String mixinClassName) {
    return true;
  }

  @Override
  public void acceptTargets(final @NonNull Set<String> myTargets, final @NonNull Set<String> otherTargets) {
  }

  @Override
  public final @Nullable List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(final @NonNull String targetClassName, final @NonNull ClassNode targetClass, final @NonNull String mixinClassName, final @NonNull IMixinInfo mixinInfo) {
  }

  @Override
  public void postApply(final @NonNull String targetClassName, final @NonNull ClassNode targetClass, final @NonNull String mixinClassName, final @NonNull IMixinInfo mixinInfo) {
  }
}

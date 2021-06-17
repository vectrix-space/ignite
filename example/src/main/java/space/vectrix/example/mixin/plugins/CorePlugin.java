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
package space.vectrix.example.mixin.plugins;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.configurate.CommentedConfigurationNode;
import space.vectrix.example.ExampleConfig;
import space.vectrix.example.ExampleInfo;
import space.vectrix.ignite.api.config.Configuration;
import space.vectrix.ignite.api.config.Configurations;

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
    final Configuration<ExampleConfig, CommentedConfigurationNode> configWrapper = Configurations.getOrCreate(Configurations.HOCON_LOADER, ExampleInfo.getExampleConfig());
    final ExampleConfig config = configWrapper.instance();
    if (config != null) {
      return config.test;
    }

    return false;
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

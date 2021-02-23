/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) Mineteria <https://mineteria.com/>
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
package com.mineteria.ignite.applaunch.util;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public final class IgniteExclusions {
  private static final @NonNull List<Exclusion> EXCLUSIONS = Lists.newArrayList(
    // Ignite
    exclude("com.mineteria.ignite.api."),
    exclude("com.mineteria.ignite.applaunch."),
    exclude("com.mineteria.ignite.relocate."),

    // Logging
    exclude("org.apache.logging.log4j."),
    exclude("org.checkerframework."),
    exclude("net.minecrell.terminalconsole."),
    exclude("org.jline."),
    exclude("com.sun.jna."),

    // Configuration
    exclude("ninja.leaping.configurate."),
    exclude("com.typesafe.config."),
    exclude("com.google.gson."),
    exclude("org.yaml.snakeyaml."),

    // Common
    exclude("com.google.common."),
    exclude("com.google.inject."),
    exclude("javax.annotation."),
    exclude("javax.inject."),
    exclude("org.aopalliance."),

    // ASM
    exclude("org.objectweb.asm."),

    // Mixin
    exclude("org.spongepowered.asm."),

    // Access Transformers
    exclude("net.minecraftforge.accesstransformer."),
    exclude("org.antlr.v4.runtime."),

    // Core
    exclude("joptsimple.")
  );

  public static @NonNull Exclusion exclude(final @NonNull String packageExclusion) {
    return new Exclusion(packageExclusion);
  }

  public static @NonNull Exclusion exclude(final @NonNull String packageExclusion, final @NonNull String resourceExclusion) {
    return new Exclusion(packageExclusion, resourceExclusion);
  }

  public static @NonNull List<Exclusion> getExclusions() {
    return IgniteExclusions.EXCLUSIONS;
  }

  private IgniteExclusions() {}

  public static final class Exclusion {
    private static @NonNull String toResourceTarget(final @NonNull String packageTarget) {
      String resourceTarget = packageTarget.replace('.', '/');
      if (resourceTarget.endsWith("/")) {
        resourceTarget = resourceTarget.substring(0, resourceTarget.length() - 1);
      }
      return resourceTarget;
    }

    private final String packageExclusion;
    private final String resourceExclusion;

    /* package */ Exclusion(final @NonNull String packageTarget) {
      this(packageTarget, Exclusion.toResourceTarget(packageTarget));
    }

    /* package */ Exclusion(final @Nullable String packageExclusion, final @Nullable String resourceExclusion) {
      this.packageExclusion = packageExclusion;
      this.resourceExclusion = resourceExclusion;
    }

    public @MonotonicNonNull String getPackageExclusion() {
      return this.packageExclusion;
    }

    public @MonotonicNonNull String getResourceExclusion() {
      return this.resourceExclusion;
    }
  }
}

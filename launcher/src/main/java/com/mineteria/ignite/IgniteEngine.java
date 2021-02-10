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
package com.mineteria.ignite;

import com.google.inject.Guice;
import com.mineteria.ignite.api.event.EventManager;
import com.mineteria.ignite.event.IgniteEventManager;
import com.mineteria.ignite.inject.IgniteModule;
import com.mineteria.ignite.launch.IgniteBlackboard;
import com.mineteria.ignite.mod.ModEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.launch.MixinBootstrap;

public final class IgniteEngine {
  public static final IgniteEngine INSTANCE = new IgniteEngine();

  static {
    MixinBootstrap.init();
  }

  private final Logger logger = LogManager.getLogger("IgniteEngine");

  private final ModEngine modEngine;
  private final EventManager eventManager;

  /* package */ IgniteEngine() {
    this.modEngine = new ModEngine(this);
    this.eventManager = new IgniteEventManager(this.modEngine);

    IgniteBlackboard.setProperty(IgniteBlackboard.PARENT_INJECTOR, Guice.createInjector(new IgniteModule(this)));
  }

  public @NonNull Logger getLogger() {
    return this.logger;
  }

  public @NonNull ModEngine getModEngine() {
    return this.modEngine;
  }

  public @NonNull EventManager getEventManager() {
    return this.eventManager;
  }
}

package com.mineteria.implant;

import com.google.gson.Gson;
import com.mineteria.implant.mod.ModEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class ImplantCore {
  public static final ImplantCore INSTANCE = new ImplantCore();

  private final Logger logger = LogManager.getLogger("ImplantCore");
  private final Gson gson = new Gson();

  private final ModEngine modEngine;

  /* package */ ImplantCore() {
    this.modEngine = new ModEngine(this);

    this.logger.info("Starting Mixin");
    MixinBootstrap.init();

    this.logger.info("Mixin Environment: SERVER");
    MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.SERVER);

    this.logger.info("Started Mixin");
  }

  public @NonNull Logger getLogger() {
    return this.logger;
  }

  public @NonNull Gson getGson() {
    return this.gson;
  }

  public @NonNull ModEngine getEngine() {
    return this.modEngine;
  }
}

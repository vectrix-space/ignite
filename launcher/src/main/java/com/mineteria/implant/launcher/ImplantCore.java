package com.mineteria.implant.launcher;

import com.mineteria.implant.launcher.mod.ModEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class ImplantCore {
  public static final ImplantCore INSTANCE = new ImplantCore();

  private final Logger logger = LogManager.getLogger("ImplantCore");

  private final ModEngine modEngine = new ModEngine();

  /* package */ ImplantCore() {
    this.setupMixins();
    this.setupMods();
  }

  public Logger getLogger() {
    return this.logger;
  }

  public ModEngine getModEngine() {
    return this.modEngine;
  }

  private void setupMixins() {
    this.logger.info("Starting Mixin");
    MixinBootstrap.init();

    this.logger.info("Mixin Environment: SERVER");
    MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.SERVER);
    //MixinEnvironment.getDefaultEnvironment().registerTokenProviderClass();
    //Mixins.registerErrorHandlerClass();

    this.logger.info("Started Mixin");
  }

  private void setupMods() {
    // TODO: locate candidates in the mod engine.
  }
}

package com.mineteria.implant.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class ImplantCore {
  public static final ImplantCore INSTANCE = new ImplantCore();

  private final Logger logger = LogManager.getLogger("ImplantCore");

  /* package */ ImplantCore() {
    this.logger.info("Starting Mixin");
    MixinBootstrap.init();

    this.logger.info("Mixin Environment: SERVER");
    MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.SERVER);
    //MixinEnvironment.getDefaultEnvironment().registerTokenProviderClass();
    //Mixins.registerErrorHandlerClass();

    this.logger.info("Started Mixin");
  }

  public Logger getLogger() {
    return this.logger;
  }
}

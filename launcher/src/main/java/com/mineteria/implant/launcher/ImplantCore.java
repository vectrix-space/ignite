package com.mineteria.implant.launcher;

import com.google.gson.Gson;
import com.mineteria.implant.launcher.mod.ModEngine;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
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

  private boolean initialized = false;

  /* package */ ImplantCore() {
    this.modEngine = new ModEngine(this);

    this.logger.info("Starting Mixin");
    MixinBootstrap.init();

    this.logger.info("Mixin Environment: SERVER");
    MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.SERVER);

    this.logger.info("Started Mixin");
  }

  public void initialize() {
    if (this.initialized) return;
    this.initialized = true;

    this.modEngine.loadCandidates();
  }

  public void load(final @NonNull ITransformingClassLoader classLoader) {
    this.logger.info("Initializing Mods");
    this.modEngine.loadMods();
    this.modEngine.transformMods(classLoader);
  }

  public Logger getLogger() {
    return this.logger;
  }

  public Gson getGson() {
    return this.gson;
  }

  public ModEngine getModEngine() {
    return this.modEngine;
  }
}

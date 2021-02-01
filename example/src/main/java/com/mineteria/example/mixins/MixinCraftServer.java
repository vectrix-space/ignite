package com.mineteria.example.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(targets = "org.bukkit.craftbukkit.v1_12_R1.CraftServer", remap = false)
public abstract class MixinCraftServer {
  @Shadow public abstract Logger getLogger();

  @Inject(method = "<init>", at = @At("RETURN"))
  private void onConstruction(CallbackInfo callback) {
    this.getLogger().info("Hello World!");
  }
}

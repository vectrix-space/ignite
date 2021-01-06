package com.mineteria.implant.mixin;

import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;

public final class ImplantMixinService extends MixinServiceModLauncher {
  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public ContainerHandleModLauncher getPrimaryContainer() {
    return new ContainerHandleModLauncher(this.getName());
  }
}

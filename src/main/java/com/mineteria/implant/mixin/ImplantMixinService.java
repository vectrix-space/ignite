package com.mineteria.implant.mixin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;

public final class ImplantMixinService extends MixinServiceModLauncher {
  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public ContainerHandleModLauncher getPrimaryContainer() {
    return new LauncherContainer(this.getName());
  }

  private static final class LauncherContainer extends ContainerHandleModLauncher {
    public LauncherContainer(final @NonNull String name) {
      super(name);
    }
  }
}

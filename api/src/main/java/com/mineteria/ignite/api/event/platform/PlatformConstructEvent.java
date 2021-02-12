package com.mineteria.ignite.api.event.platform;

import com.mineteria.ignite.api.mod.ModContainer;

/**
 * This event is called when all loaded {@link ModContainer}s
 * have been created and all target classes have been injected.
 *
 * <p>This event occurs before the {@link PlatformInitializeEvent}.</p>
 */
public final class PlatformConstructEvent {
  @Override
  public String toString() {
    return "PlatformConstructEvent";
  }
}

package com.mineteria.ignite.api.event.platform;

/**
 * This event is called when the mods should load configurations
 * and prepare for the registration of mixins, mixin plugins and
 * more.
 *
 * <p>This event occurs after the {@link PlatformConstructEvent}.</p>
 */
public final class PlatformInitializeEvent {
  @Override
  public String toString() {
    return "PlatformInitializeEvent";
  }
}

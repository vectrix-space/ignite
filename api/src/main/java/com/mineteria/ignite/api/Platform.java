package com.mineteria.ignite.api;

import com.mineteria.ignite.api.event.EventManager;

import java.nio.file.Path;

public interface Platform {
  EventManager getEventManager();
  Path getConfigs();
  Path getMods();
}

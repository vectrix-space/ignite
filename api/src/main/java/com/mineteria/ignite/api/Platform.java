package com.mineteria.ignite.api;

import com.mineteria.ignite.api.event.EventManager;

public interface Platform {
  EventManager getEventManager();
}

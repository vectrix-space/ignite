package com.mineteria.ignite.api.event;

/**
 * Represents the priority a listener should receive an
 * event in, relative to the other listeners.
 */
public enum PostPriority {
  FIRST,
  EARLY,
  NORMAL,
  LATE,
  LAST
}

package com.mineteria.implant.api;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class Implant {
  private static Game INSTANCE;

  public static @NonNull Game getGame() {
    return Implant.INSTANCE;
  }

  public static void setGame(final @NonNull Game game) {
    Implant.INSTANCE = game;
  }
}

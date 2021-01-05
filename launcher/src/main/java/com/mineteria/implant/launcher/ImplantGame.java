package com.mineteria.implant.launcher;

import com.mineteria.implant.api.Game;

public final class ImplantGame implements Game {
  private final ImplantCore core;

  /* package */ ImplantGame(final ImplantCore core) {
    this.core = core;
  }
}

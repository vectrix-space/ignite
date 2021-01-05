package com.mineteria.implant.launcher.transformer;

import com.mineteria.implant.api.transformer.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ImplantTransformer implements Transformer {
  private static final Logger LOGGER = LogManager.getLogger("ImplantCore");

  @Override
  public byte[] transformClass(final byte[] source, final @NonNull String className, final @NonNull String remappedClassName) {
    return new byte[0];
  }
}

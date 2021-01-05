package com.mineteria.implant.api.transformer;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a bytecode transformer.
 */
public interface Transformer {
  /**
   * Transforms a class.
   *
   * @param source The class bytecode, if present
   * @param className The class name
   * @param remappedClassName The remapped class name
   * @return The transformed class, if present
   */
  byte[] transformClass(byte[] source, @NonNull String className, @NonNull String remappedClassName);
}

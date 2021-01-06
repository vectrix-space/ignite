package com.mineteria.implant.launcher.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class ModResource {
  private final String locator;
  private final Path path;

  private FileSystem fileSystem;

  public ModResource(final @NonNull String locator, final @NonNull Path path) {
    this.locator = locator;
    this.path = path;
  }

  public @NonNull String getLocator() {
    return this.locator;
  }

  public @NonNull Path getPath() {
    return this.path;
  }

  public FileSystem getFileSystem() {
    if (this.fileSystem == null) {
      try {
        this.fileSystem = FileSystems.newFileSystem(this.getPath(), this.getClass().getClassLoader());
      } catch (final IOException exception) {
        throw new RuntimeException(exception);
      }
    }

    return this.fileSystem;
  }

  @Override
  public String toString() {
    return "ModResource{name=" + this.locator + ", path=" + this.path + ", fileSystem=" + this.fileSystem + "}";
  }
}

package com.mineteria.ignite.launch.mod;

import com.mineteria.ignite.launch.IgniteLaunch;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class ModClassLoader extends URLClassLoader {
  private static final Set<ModClassLoader> loaders = new CopyOnWriteArraySet<>();

  static {
    ClassLoader.registerAsParallelCapable();
  }

  public ModClassLoader(final @NonNull URL[] urls) {
    super(urls, IgniteLaunch.class.getClassLoader());
  }

  public void addLoaders() {
    ModClassLoader.loaders.add(this);
  }

  @Override
  protected Class<?> loadClass(final @NonNull String name, final boolean resolve) throws ClassNotFoundException {
    return this.loadClass0(name, resolve, true);
  }

  @Override
  public void close() throws IOException {
    ModClassLoader.loaders.remove(this);
    super.close();
  }

  private Class<?> loadClass0(final @NonNull String name, final boolean resolve, final boolean checkOther)
    throws ClassNotFoundException {
    try {
      return super.loadClass(name, resolve);
    } catch (ClassNotFoundException ignored) {
      // Ignored: we'll try others
    }

    if (checkOther) {
      for (ModClassLoader loader : ModClassLoader.loaders) {
        if (loader != this) {
          try {
            return loader.loadClass0(name, resolve, false);
          } catch (final ClassNotFoundException ignored) {
            // We're trying others, safe to ignore
          }
        }
      }
    }

    throw new ClassNotFoundException(name);
  }
}

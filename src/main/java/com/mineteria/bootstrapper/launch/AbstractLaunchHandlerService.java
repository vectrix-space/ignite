package com.mineteria.bootstrapper.launch;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class AbstractLaunchHandlerService implements ILaunchHandlerService {
  /**
   * A list of class loader exclusions to ignore when
   * transforming classes.
   */
  protected static final List<String> EXCLUDED_PACKAGES = Arrays.asList(
    // TODO: Add excluded packages.
  );

  @Override
  public void configureTransformationClassLoader(final @NonNull ITransformingClassLoaderBuilder builder) {
    // TODO: Add resource resolver for class bytes locator.
  }

  @Override
  public @NonNull Callable<Void> launchService(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {
    launchClassLoader.addTargetPackageFilter(other -> {
      for (final String pkg : AbstractLaunchHandlerService.EXCLUDED_PACKAGES) {
        if (other.startsWith(pkg)) {
          return false;
        }
      }

      return true;
    });

    return () -> {
      this.launchService0(arguments, launchClassLoader);
      return null;
    };
  }

  /**
   * Launch the service (Minecraft).
   *
   * <p>Take care to ONLY load classes on the provided {@link ClassLoader},
   * which can be retrieved with </p>
   *
   * @param arguments The arguments to launch the service with
   * @param launchClassLoader The transforming class loader to load classes with
   */
  protected abstract void launchService0(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader);
}

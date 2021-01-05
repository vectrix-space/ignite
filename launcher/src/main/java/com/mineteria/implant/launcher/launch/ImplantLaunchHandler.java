package com.mineteria.implant.launcher.launch;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public final class ImplantLaunchHandler implements ILaunchHandlerService {
  private final Logger logger = LogManager.getLogger("ImplantLaunch");

  /**
   * A list of class loader exclusions to ignore when
   * transforming classes.
   */
  protected static final List<String> EXCLUDED_PACKAGES = Arrays.asList(
    // TODO: Add excluded packages.
  );

  @Override
  public String name() {
    return "mineteria";
  }

  @Override
  public void configureTransformationClassLoader(final @NonNull ITransformingClassLoaderBuilder builder) {
    for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
      if (url.toString().contains("mixin") && url.toString().endsWith(".jar")) {
        continue;
      }

      try {
        builder.addTransformationPath(Paths.get(url.toURI()));
      } catch (final URISyntaxException exception) {
        this.logger.error("Failed to add Mixin transformation path.", exception);
      }
    }

    // TODO: Add resource resolver for class bytes locator.
    //builder.setClassBytesLocator();
  }

  @Override
  public @NonNull Callable<Void> launchService(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {
    this.logger.info("Transitioning to Minecraft launcher, please wait...");

    launchClassLoader.addTargetPackageFilter(other -> {
      for (final String pkg : ImplantLaunchHandler.EXCLUDED_PACKAGES) {
        if (other.startsWith(pkg)) return false;
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
  protected void launchService0(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {

  }
}

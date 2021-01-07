package com.mineteria.implant.launch;

import com.mineteria.implant.ImplantCore;
import com.mineteria.implant.mod.ModResource;
import com.mineteria.implant.util.ClassLoaderUtil;
import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

public final class ImplantLaunchService implements ILaunchHandlerService {
  private final Logger logger = LogManager.getLogger("ImplantLaunch");

  /**
   * A list of class loader exclusions to ignore when
   * transforming classes.
   */
  protected static final List<String> EXCLUDED_PACKAGES = Arrays.asList(
    // Implant
    "org.mineteria.implant.launch.",
    "org.mineteria.implant.mod.",
    "org.mineteria.implant.mixin.",

    // Libraries
    "ninja.leaping.configurate.",
    "javax.inject.",
    "joptsimple.",
    "gnu.trove.",
    "it.unimi.dsi.fastutil.",
    "org.apache.logging.log4j.",
    "org.yaml.snakeyaml.",
    "com.google.inject.",
    "com.google.common.",
    "com.google.gson.",
    "javax.annotation.",
    "org.apache.commons.",

    // Note: Fix for logging.
    "net.minecrell.terminalconsole.",
    "com.sun.jna.",
    "org.fusesource.jansi.",
    "org.jline."
  );

  @Override
  public @NonNull String name() {
    return "implant_launch";
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

    builder.setClassBytesLocator(this.getResourceLocator());
  }

  @Override
  public @NonNull Callable<Void> launchService(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) {
    ImplantCore.INSTANCE.getEngine().loadContainers();

    this.logger.info("Transitioning to Minecraft launcher, please wait...");

    launchClassLoader.addTargetPackageFilter(other -> {
      for (final String pkg : ImplantLaunchService.EXCLUDED_PACKAGES) {
        if (other.startsWith(pkg)) return false;
      }
      return true;
    });

    return () -> {
      this.launchService0(arguments, launchClassLoader);
      return null;
    };
  }

  protected @NonNull Function<String, Optional<URL>> getResourceLocator() {
    return string -> {
      for (final ModResource resource : ImplantCore.INSTANCE.getEngine().getCandidates()) {
        final Path resolved = resource.getFileSystem().getPath(string);
        if (Files.exists(resolved)) {
          try {
            return Optional.of(resolved.toUri().toURL());
          } catch (final MalformedURLException exception) {
            throw new RuntimeException(exception);
          }
        }
      }

      return Optional.empty();
    };
  }

  /**
   * Launch the service (Minecraft).
   *
   * @param arguments The arguments to launch the service with
   * @param launchClassLoader The transforming class loader to load classes with
   */
  protected void launchService0(final @NonNull String[] arguments, final @NonNull ITransformingClassLoader launchClassLoader) throws Exception {
    final ClassLoader classLoader = launchClassLoader.getInstance();

    final Path launchJar = ImplantBlackboard.getProperty(ImplantBlackboard.LAUNCH_JAR);
    if (launchJar == null || !Files.exists(launchJar)) {
      throw new IllegalStateException("No launch jar was found!");
    } else {
      // Load the server jar on the provided ClassLoader.
      final ClassLoader childLoader = ClassLoaderUtil.toUrl(launchJar)
        .map(url -> (ClassLoader) ClassLoaderUtil.loadJar(classLoader, url))
        .orElse(classLoader);

      // Invoke the main method on the provided ClassLoader.
      Class.forName("org.bukkit.craftbukkit.Main", true, childLoader)
        .getMethod("main", String[].class)
        .invoke(null, (Object) arguments);
    }
  }
}

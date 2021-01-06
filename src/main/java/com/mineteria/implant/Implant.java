package com.mineteria.implant;

import com.mineteria.implant.launch.ImplantBlackboard;
import com.mineteria.implant.util.ClassLoaderUtil;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Implant {
  /**
   * The launch jar path.
   */
  public static final Path LAUNCH_JAR = Paths.get(System.getProperty(ImplantBlackboard.LAUNCH_JAR.name(), "./server.jar"));

  /**
   * The launch target class path.
   */
  public static final String LAUNCH_TARGET = System.getProperty(ImplantBlackboard.LAUNCH_TARGET.name(), "org.bukkit.craftbukkit.Main");

  /**
   * The mods directory.
   */
  public static final Path MOD_TARGET_PATH = Paths.get(System.getProperty(ImplantBlackboard.MOD_DIRECTORY_PATH.name(), "./mods"));

  /**
   * The mods configuration directory.
   */
  public static final Path MOD_CONFIG_PATH = Paths.get(System.getProperty(ImplantBlackboard.MOD_CONFIG_PATH.name(), "./modconfigs"));

  private static final String LAUNCHER_VERSION = "0.1.0";

  /**
   * The main launch target to boostrap from.
   *
   * @param args The launch arguments
   */
  public static void main(String[] args) {
    final List<String> arguments = Arrays.asList(args);

    // Target Loading
    final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    if (!Files.exists(Implant.LAUNCH_JAR)) {
      throw new IllegalStateException("Unable to locate launch jar at '" + Implant.LAUNCH_JAR + "'.");
    } else {
      ClassLoaderUtil.toUrl(Implant.LAUNCH_JAR).ifPresent(url -> ClassLoaderUtil.loadJar(classLoader, url));
    }

    // Logger
    final Logger logger = LogManager.getLogger("ImplantMain");
    logger.info("Implant Launcher version {}", Implant.LAUNCHER_VERSION);

    // Blackboard
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_ARGUMENTS, Collections.unmodifiableList(arguments));
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_JAR, Implant.LAUNCH_JAR);
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_TARGET, Implant.LAUNCH_TARGET);
    ImplantBlackboard.setProperty(ImplantBlackboard.MOD_DIRECTORY_PATH, Implant.MOD_TARGET_PATH);
    ImplantBlackboard.setProperty(ImplantBlackboard.MOD_CONFIG_PATH, Implant.MOD_CONFIG_PATH);

    // Modlauncher
    logger.info("Preparing ModLauncher with arguments {}", arguments);
    Launcher.main(arguments.toArray(new String[0]));
  }
}

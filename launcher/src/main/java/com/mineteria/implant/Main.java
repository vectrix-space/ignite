package com.mineteria.implant;

import com.mineteria.implant.launcher.launch.ImplantBlackboard;
import com.mineteria.implant.launcher.util.ClassLoaderUtility;
import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Main {
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
    if (!Files.exists(Main.LAUNCH_JAR)) {
      throw new IllegalStateException("Unable to locate launch jar at '" + Main.LAUNCH_JAR + "'.");
    } else {
      ClassLoaderUtility.toUrl(Main.LAUNCH_JAR).ifPresent(url -> ClassLoaderUtility.loadJar(classLoader, url));
    }

    // Logger
    final Logger logger = LogManager.getLogger("ImplantMain");
    logger.info("Implant Launcher version {}", Main.LAUNCHER_VERSION);

    // Blackboard
    final ImplantBlackboard blackboard = ImplantBlackboard.INSTANCE;
    blackboard.setProperty(ImplantBlackboard.LAUNCH_ARGUMENTS, Collections.unmodifiableList(arguments));
    blackboard.setProperty(ImplantBlackboard.LAUNCH_JAR, Main.LAUNCH_JAR);
    blackboard.setProperty(ImplantBlackboard.LAUNCH_TARGET, Main.LAUNCH_TARGET);
    blackboard.setProperty(ImplantBlackboard.MOD_DIRECTORY_PATH, Main.MOD_TARGET_PATH);
    blackboard.setProperty(ImplantBlackboard.MOD_CONFIG_PATH, Main.MOD_CONFIG_PATH);

    // Modlauncher
    logger.info("Preparing ModLauncher with arguments {}", arguments);
    Launcher.main(arguments.toArray(new String[0]));
  }
}

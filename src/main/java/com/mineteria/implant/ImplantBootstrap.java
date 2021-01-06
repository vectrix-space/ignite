package com.mineteria.implant;

import com.mineteria.implant.launch.ImplantBlackboard;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ImplantBootstrap {
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

  private static final String LAUNCHER_VERSION = "0.1.0";

  /**
   * The main launch target to boostrap from.
   *
   * @param args The launch arguments
   */
  public static void main(String[] args) {
    final List<String> arguments = Arrays.asList(args);
    final List<String> launchArguments = new ArrayList<>(arguments);

    // Launch Arguments
    launchArguments.add("--launchTarget");
    launchArguments.add("implant_launch");

    // Target Loading
    if (!Files.exists(ImplantBootstrap.LAUNCH_JAR)) {
      throw new IllegalStateException("Unable to locate launch jar at '" + ImplantBootstrap.LAUNCH_JAR + "'.");
    }

    // Logger
    final Logger logger = LogManager.getLogger("ImplantBootstrap");
    logger.info("Implant Launcher version {}", ImplantBootstrap.LAUNCHER_VERSION);

    // Blackboard
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_ARGUMENTS, Collections.unmodifiableList(arguments));
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_JAR, ImplantBootstrap.LAUNCH_JAR);
    ImplantBlackboard.setProperty(ImplantBlackboard.LAUNCH_TARGET, ImplantBootstrap.LAUNCH_TARGET);
    ImplantBlackboard.setProperty(ImplantBlackboard.MOD_DIRECTORY_PATH, ImplantBootstrap.MOD_TARGET_PATH);

    // Modlauncher
    logger.info("Preparing ModLauncher with arguments {}", launchArguments);
    Launcher.main(launchArguments.toArray(new String[0]));
  }
}

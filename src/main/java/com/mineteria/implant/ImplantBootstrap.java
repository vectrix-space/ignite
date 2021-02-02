/*
 * This file is part of Implant, licensed under the MIT License (MIT).
 *
 * Copyright (c) Mineteria <https://mineteria.com/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mineteria.implant;

import com.mineteria.implant.agent.Agent;
import com.mineteria.implant.launch.ImplantBlackboard;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
  private static final Path LAUNCH_JAR = Paths.get(System.getProperty(ImplantBlackboard.LAUNCH_JAR.name(), "./server.jar"));

  /**
   * The launch target class path.
   */
  private static final String LAUNCH_TARGET = System.getProperty(ImplantBlackboard.LAUNCH_TARGET.name(), "org.bukkit.craftbukkit.Main");

  /**
   * The mods directory.
   */
  private static final Path MOD_TARGET_PATH = Paths.get(System.getProperty(ImplantBlackboard.MOD_DIRECTORY_PATH.name(), "./mods"));

  private static final String LAUNCHER_VERSION = "@version@";

  /**
   * The main launch target to boostrap from.
   *
   * @param args The launch arguments
   */
  public static void main(String[] args) {
    final List<String> arguments = Arrays.asList(args);
    final List<String> launchArguments = new ArrayList<>(arguments);

    // Target Check
    if (!Files.exists(ImplantBootstrap.LAUNCH_JAR)) {
      throw new IllegalStateException("Unable to locate launch jar at '" + ImplantBootstrap.LAUNCH_JAR + "'.");
    }

    // Launch Target
    launchArguments.add("--launchTarget");
    launchArguments.add("implant_launch");

    // Load the server jar on the provided ClassLoader via the Agent.
    try {
      Agent.addJar(ImplantBootstrap.LAUNCH_JAR);
    } catch (final IOException exception) {
      throw new IllegalStateException("Unable to add launch jar to classpath!");
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

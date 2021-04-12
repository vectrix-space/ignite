/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
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
package space.vectrix.ignite.applaunch;

import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.Blackboard;
import space.vectrix.ignite.applaunch.agent.Agent;
import space.vectrix.ignite.applaunch.mod.ModEngine;
import space.vectrix.ignite.applaunch.util.IgniteConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class IgniteBootstrap {
  /**
   * The launch jar path.
   */
  public static final @NonNull Path LAUNCH_JAR = Paths.get(System.getProperty(Blackboard.LAUNCH_JAR.getName(), "./server.jar"));

  /**
   * The launch target class path.
   */
  public static final @NonNull String LAUNCH_TARGET = System.getProperty(Blackboard.LAUNCH_TARGET.getName(), "org.bukkit.craftbukkit.Main");

  /**
   * The mods directory.
   */
  public static final @NonNull Path MOD_TARGET_PATH = Paths.get(System.getProperty(Blackboard.MOD_DIRECTORY_PATH.getName(), "./mods"));

  /**
   * The configs directory.
   */
  public static final @NonNull Path CONFIG_TARGET_PATH = Paths.get(System.getProperty(Blackboard.CONFIG_DIRECTORY_PATH.getName(), "./configs"));

  private static IgniteBootstrap instance;

  public static IgniteBootstrap getInstance() {
    return IgniteBootstrap.instance;
  }

  public static void main(final @NonNull String[] args) {
    new IgniteBootstrap().run(args);
  }

  private final ModEngine modEngine;

  public IgniteBootstrap() {
    IgniteBootstrap.instance = this;
    this.modEngine = new ModEngine();
  }

  /**
   * The main launch target to boostrap from.
   *
   * @param args The launch arguments
   */
  public void run(final @NonNull String[] args) {
    final List<String> arguments = Arrays.asList(args);
    final List<String> launchArguments = new ArrayList<>(arguments);

    // Target Check
    if (!Files.exists(IgniteBootstrap.LAUNCH_JAR)) {
      throw new IllegalStateException("Unable to locate launch jar at '" + IgniteBootstrap.LAUNCH_JAR + "'.");
    }

    // Launch Target
    launchArguments.add("--launchTarget");
    launchArguments.add(IgniteConstants.IGNITE_LAUNCH_SERVICE);

    // Load the server jar on the provided ClassLoader via the Agent.
    try {
      Agent.addJar(IgniteBootstrap.LAUNCH_JAR);
    } catch (final IOException exception) {
      throw new IllegalStateException("Unable to add launch jar to classpath!");
    }

    // Logger
    final Logger logger = LogManager.getLogger("IgniteBootstrap");
    logger.info("Ignite Launcher v" + IgniteBootstrap.class.getPackage().getImplementationVersion());

    // Blackboard
    Blackboard.setProperty(Blackboard.LAUNCH_ARGUMENTS, Collections.unmodifiableList(arguments));
    Blackboard.setProperty(Blackboard.LAUNCH_JAR, IgniteBootstrap.LAUNCH_JAR);
    Blackboard.setProperty(Blackboard.LAUNCH_TARGET, IgniteBootstrap.LAUNCH_TARGET);
    Blackboard.setProperty(Blackboard.MOD_DIRECTORY_PATH, IgniteBootstrap.MOD_TARGET_PATH);
    Blackboard.setProperty(Blackboard.CONFIG_DIRECTORY_PATH, IgniteBootstrap.CONFIG_TARGET_PATH);

    // Update Security - Java 9+
    Agent.updateSecurity();

    // Modlauncher
    logger.info("Preparing ModLauncher with arguments " + launchArguments);
    Launcher.main(launchArguments.toArray(new String[0]));
  }

  public final @NonNull ModEngine getModEngine() {
    return this.modEngine;
  }
}

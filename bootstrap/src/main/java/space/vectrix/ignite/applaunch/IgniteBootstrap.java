/*
 * This file is part of ignite, licensed under the MIT License (MIT).
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

import cpw.mods.modlauncher.InvalidLauncherSetupException;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.api.Ignite;
import space.vectrix.ignite.api.blackboard.Keys;
import space.vectrix.ignite.applaunch.blackboard.BlackboardImpl;
import space.vectrix.ignite.applaunch.util.ArgumentList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IgniteBootstrap {
  private static final Logger LOGGER = LogManager.getLogger("IgniteBootstrap");

  public static void main(final @NotNull String[] args) throws Exception {
    IgniteBootstrap.main(args, new Path[0]);
  }

  public static void main(final @NotNull String[] args, final @NotNull Path[] extraPaths) throws Exception {
    BootstrapTerminal.configure(args);
    new IgniteBootstrap(extraPaths).run();
  }

  private final Path[] extraPaths;

  public IgniteBootstrap(final @NotNull Path[] extraPaths) {
    Ignite.blackboard(new BlackboardImpl());

    this.extraPaths = extraPaths;
  }

  public void run() throws IOException {
    final String specificationVersion = Ignite.class.getPackage().getSpecificationVersion();

    Ignite.blackboard().compute(Keys.VERSION, () -> specificationVersion == null ? "0.0" : specificationVersion);
    Ignite.blackboard().compute(Keys.PLATFORM_DIRECTORY, () -> BootstrapTerminal.PLATFORM_DIRECTORY);
    Ignite.blackboard().compute(Keys.PLATFORM_JAR, () -> BootstrapTerminal.PLATFORM_JAR);
    Ignite.blackboard().compute(Keys.PLATFORM_CLASSPATH, () -> BootstrapTerminal.PLATFORM_CLASSPATH);

    final Path modsDirectory = BootstrapTerminal.PLATFORM_DIRECTORY.resolve("mods");
    if(Files.notExists(modsDirectory)) {
      Files.createDirectories(modsDirectory);
    }

    Ignite.blackboard().compute(Keys.MOD_DIRECTORY, () -> modsDirectory);

    IgniteBootstrap.LOGGER.info("Transitioning to ModLauncher. Please wait...");
    final ArgumentList argumentList = ArgumentList.from(BootstrapTerminal.RAW_ARGS);

    System.out.println("Launcher Layer: " + Launcher.class.getModule().getLayer());

    try {
      Launcher.main(argumentList.arguments());
    } catch(final InvalidLauncherSetupException exception) {
      IgniteBootstrap.LOGGER.error("The bootstrapper is unable to be setup correctly.", exception);
      System.exit(1);
    }
  }
}

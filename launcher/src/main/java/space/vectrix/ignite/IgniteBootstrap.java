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
package space.vectrix.ignite;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.util.JavaVersion;
import org.spongepowered.asm.util.asm.ASM;
import org.tinylog.Logger;
import space.vectrix.ignite.agent.IgniteAgent;
import space.vectrix.ignite.game.GameLocatorService;
import space.vectrix.ignite.game.GameProvider;
import space.vectrix.ignite.launch.ember.Ember;
import space.vectrix.ignite.mod.ModsImpl;
import space.vectrix.ignite.util.IgniteCollections;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Represents the main class which starts Ignite.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class IgniteBootstrap {
  private static IgniteBootstrap INSTANCE;

  /**
   * Returns the bootstrap instance.
   *
   * @return this instance
   * @since 1.0.0
   */
  public static @NotNull IgniteBootstrap instance() {
    return IgniteBootstrap.INSTANCE;
  }

  /**
   * The main entrypoint to start Ignite.
   *
   * @param arguments the launch arguments
   * @since 1.0.0
   */
  public static void main(final String@NotNull [] arguments) {
    PreBoot.init();
    new IgniteBootstrap().run(arguments);
  }

  private final ModsImpl engine;

  /* package */ IgniteBootstrap() {
    IgniteBootstrap.INSTANCE = this;
    this.engine = new ModsImpl();
  }

  private void run(final String@NotNull [] args) {
    final List<String> arguments = Arrays.asList(args);
    final List<String> launchArguments = new ArrayList<>(arguments);

    // Print the runtime information for this launch.
    Logger.info(
      "Running {} v{} (API: {}, ASM: {}, Java: {})",
      IgniteConstants.API_TITLE,
      IgniteConstants.IMPLEMENTATION_VERSION,
      IgniteConstants.API_VERSION,
      ASM.getVersionString(),
      JavaVersion.current()
    );

    // Initialize the blackboard and populate it with the startup
    // flags.
    Blackboard.compute(Blackboard.DEBUG, () -> Boolean.parseBoolean(System.getProperty(Blackboard.DEBUG.name())));
    Blackboard.compute(Blackboard.GAME_LOCATOR, () -> System.getProperty(Blackboard.GAME_LOCATOR.name()));
    Blackboard.compute(Blackboard.GAME_JAR, () -> Paths.get(System.getProperty(Blackboard.GAME_JAR.name())));
    Blackboard.compute(Blackboard.GAME_TARGET, () -> System.getProperty(Blackboard.GAME_TARGET.name()));
    Blackboard.compute(Blackboard.GAME_LIBRARIES, () -> Paths.get(System.getProperty(Blackboard.GAME_LIBRARIES.name())));
    Blackboard.compute(Blackboard.MODS_DIRECTORY, () -> Paths.get(System.getProperty(Blackboard.MODS_DIRECTORY.name())));

    // Get a suitable game locator and game provider.
    final GameLocatorService gameLocator;
    final GameProvider gameProvider;
    {
      final Optional<String> requiredGameLocator = Blackboard.get(Blackboard.GAME_LOCATOR);
      final ServiceLoader<GameLocatorService> gameLocatorLoader = ServiceLoader.load(GameLocatorService.class);
      final Optional<GameLocatorService> gameLocatorProvider = requiredGameLocator.map(locatorIdentifier -> IgniteCollections.stream(gameLocatorLoader)
        .filter(locator -> locator.id().equalsIgnoreCase(locatorIdentifier))
        .findFirst()).orElseGet(() -> IgniteCollections.stream(gameLocatorLoader)
        .sorted(Comparator.comparingInt(GameLocatorService::priority))
        .filter(GameLocatorService::shouldApply)
        .findFirst()
      );

      if(!gameLocatorProvider.isPresent()) {
        Logger.error("Failed to start game: Unable to find a suitable GameLocator service.");
        System.exit(1);
        return;
      }

      gameLocator = gameLocatorProvider.get();

      Logger.info("Detected game locator: {}", gameLocator.name());

      try {
        gameLocator.apply(this);
      } catch(final Throwable throwable) {
        Logger.error(throwable, "Failed to start game: Unable to apply GameLocator service.");
        System.exit(1);
        return;
      }

      gameProvider = gameLocator.locate();
    }

    Logger.info("Preparing the game...");

    // Add the game.
    final Path gameJar = gameProvider.gamePath();
    try {
      IgniteAgent.addJar(gameJar);

      Logger.trace("Added game jar: {}", gameJar);
    } catch(final IOException exception) {
      Logger.error(exception, "Failed to resolve game jar: {}", gameJar);
      System.exit(1);
      return;
    }

    // Add the game libraries.
    gameProvider.gameLibraries().forEach(path -> {
      if(!path.toString().endsWith(".jar")) return;

      try {
        IgniteAgent.addJar(path);

        Logger.trace("Added game library jar: {}", path);
      } catch(final IOException exception) {
        Logger.error(exception, "Failed to resolve game library jar: {}", path);
      }
    });

    Logger.info("Launching the game...");

    // Initialize the API.
    Ignite.initialize(new PlatformImpl());

    // Launch the game.
    Ember.launch(launchArguments.toArray(new String[0]));
  }

  /**
   * Returns the mod engine.
   *
   * @return the mod engine
   * @since 1.0.0
   */
  public @NotNull ModsImpl engine() {
    return this.engine;
  }
}

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
package space.vectrix.ignite.game;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.IgniteBootstrap;

/**
 * Provides a game locator for Hytale.
 *
 * @author vectrix
 * @since 1.2.0
 */
public final class HytaleGameLocator implements GameLocatorService {
  private HytaleGameProvider provider;

  @Override
  public @NotNull String id() {
    return "hytale";
  }

  @Override
  public @NotNull String name() {
    return "Hytale";
  }

  @Override
  public int priority() {
    return 50;
  }

  @Override
  public boolean shouldApply() {
    final Path path = Blackboard.get(Blackboard.GAME_JAR).orElseGet(() -> Paths.get("./HytaleServer.jar"));
    try(final JarFile jarFile = new JarFile(path.toFile())) {
      return jarFile.getJarEntry("manifests.json") != null;
    } catch(final IOException exception) {
      return false;
    }
  }

  @Override
  public void apply(final @NotNull IgniteBootstrap bootstrap) throws Throwable {
    Blackboard.compute(Blackboard.GAME_TARGET, () -> Blackboard.get(Blackboard.GAME_TARGET).orElse("com.hypixel.hytale.Main"));

    // Create the game provider.
    if(this.provider == null) {
      this.provider = new HytaleGameProvider();
    }

    // Locate the game jar.
    if(!Blackboard.get(Blackboard.GAME_JAR).isPresent()) {
      Blackboard.put(Blackboard.GAME_JAR, this.provider.gamePath());
    }
  }

  @Override
  public @NotNull GameProvider locate() {
    return this.provider;
  }

  /* package */ static final class HytaleGameProvider implements GameProvider {
    /* package */ HytaleGameProvider() {
    }

    @Override
    public @NotNull Stream<Path> gameLibraries() {
      return Stream.empty();
    }

    @Override
    public @NotNull Path gamePath() {
      return Blackboard.get(Blackboard.GAME_JAR).orElseGet(() -> Paths.get("./HytaleServer.jar"));
    }
  }
}

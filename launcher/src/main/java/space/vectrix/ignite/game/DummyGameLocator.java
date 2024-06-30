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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.IgniteBootstrap;

/**
 * Provides a general game locator.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class DummyGameLocator implements GameLocatorService {
  private DummyGameProvider provider;

  @Override
  public @NotNull String id() {
    return "dummy";
  }

  @Override
  public @NotNull String name() {
    return "Dummy";
  }

  @Override
  public int priority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean shouldApply() {
    return true;
  }

  @Override
  public void apply(final @NotNull IgniteBootstrap bootstrap) throws Throwable {
    Logger.warn("Using the dummy game provider means that all the jars found in the game libraries directory");
    Logger.warn("will be loaded into the classpath. If this causes an unexpected problem, please delete the");
    Logger.warn("libraries directory and try launch again.");

    if(this.provider == null) {
      this.provider = new DummyGameProvider();
    }
  }

  @Override
  public @NotNull GameProvider locate() {
    return this.provider;
  }

  /* package */ static final class DummyGameProvider implements GameProvider {
    /* package */ DummyGameProvider() {
    }

    @Override
    public @NotNull Stream<Path> gameLibraries() {
      final Path libraryPath = Blackboard.raw(Blackboard.GAME_LIBRARIES);
      final List<Path> libraries;

      try(final Stream<Path> stream = Files.walk(libraryPath)) {
        // We must .collect() to a list and re-stream() as Stream is AutoClosable, and thus
        // will be closed as soon as we exit the try-catch block.
        libraries = stream
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().endsWith(".jar"))
          .collect(Collectors.toList());
      } catch(final Throwable throwable) {
        return Stream.empty();
      }

      return libraries.stream();
    }

    @Override
    public @NotNull Path gamePath() {
      return Blackboard.raw(Blackboard.GAME_JAR);
    }
  }
}

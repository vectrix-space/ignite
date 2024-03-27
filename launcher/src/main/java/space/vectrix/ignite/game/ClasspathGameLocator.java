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

import java.nio.file.Path;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.Blackboard;
import space.vectrix.ignite.IgniteBootstrap;

/**
 * Provides a game locator for that uses the classpath.
 * Also enables mod loading via classpath.
 *
 * @author Flowey
 * @since 1.0.2
 */
public final class ClasspathGameLocator implements GameLocatorService {
  private ClasspathGameProvider provider;

  @Override
  public @NotNull String id() {
    return "classpath";
  }

  @Override
  public @NotNull String name() {
    return "Dev Class Path Loader";
  }

  @Override
  public int priority() {
    return 100;
  }

  @Override
  public boolean shouldApply() {
    // we can't afford to load the class here, so just assume it's valid
    return true;
  }

  @Override
  public void apply(final @NotNull IgniteBootstrap bootstrap) throws Throwable {
    // Create the game provider.
    if (this.provider == null) {
      this.provider = this.createProvider();
    }

    Blackboard.compute(Blackboard.IS_CLASS_PATH, () -> true);
  }

  @Override
  public @NotNull GameProvider locate() {
    return this.provider;
  }

  private ClasspathGameProvider createProvider() {
    return new ClasspathGameProvider();
  }

  /* package */ static final class ClasspathGameProvider implements GameProvider {
    /* package */ ClasspathGameProvider() {
    }

    @Override
    public @NotNull Stream<Path> gameLibraries() {
      return Stream.empty(); // should be in classpath...
    }

    @Override
    public @NotNull Path gamePath() {
      throw new UnsupportedOperationException();
    }
  }
}

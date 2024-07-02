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
package space.vectrix.ignite.launch.ember;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;

/**
 * Represents the root container.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class EmberMixinContainer extends ContainerHandleVirtual {
  /**
   * Creates a new root container handle.
   *
   * @param name the name
   * @since 1.0.0
   */
  public EmberMixinContainer(final @NotNull String name) {
    super(name);
  }

  /**
   * Adds a resource to this container.
   *
   * @param name the name
   * @param path the path
   * @since 1.0.0
   */
  public void addResource(final @NotNull String name, final @NotNull Path path) {
    this.add(new ResourceContainer(name, path));
  }

  /**
   * Adds a classpath resource to this container.
   *
   * @since 1.0.2
   */
  public void addClassPath() {
    this.add(new ClassPathResourceContainer());
  }

  /**
   * Adds a resource to this container.
   *
   * @param entry the entry
   * @since 1.0.0
   */
  public void addResource(final Map.@NotNull Entry<String, Path> entry) {
    this.add(new ResourceContainer(entry.getKey(), entry.getValue()));
  }

  @Override
  public String toString() {
    return "EmberMixinContainer{name=" + this.getName() + "}";
  }

  /* package */ static class ResourceContainer extends ContainerHandleURI {
    private final String name;
    private final Path path;

    /* package */ ResourceContainer(final @NotNull String name, final @NotNull Path path) {
      super(path.toUri());

      this.name = name;
      this.path = path;
    }

    public @NotNull String name() {
      return this.name;
    }

    public @NotNull Path path() {
      return this.path;
    }

    @Override
    public @NotNull String toString() {
      return "ResourceContainer{name=" + this.name + ", path=" + this.path + "}";
    }
  }

  static class ClassPathResourceContainer implements IContainerHandle {
    @Override
    public String getAttribute(final String s) {
      return null;
    }

    @Override
    public Collection<IContainerHandle> getNestedContainers() {
      return Collections.emptyList();
    }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof ClassPathResourceContainer;
    }
  }
}

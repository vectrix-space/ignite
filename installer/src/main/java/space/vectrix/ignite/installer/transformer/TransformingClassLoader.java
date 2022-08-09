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
package space.vectrix.ignite.installer.transformer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

public final class TransformingClassLoader extends URLClassLoader {
  static {
    ClassLoader.registerAsParallelCapable();
  }

  private final List<Transformer> transformers = new ArrayList<>();

  public TransformingClassLoader(final @NotNull String name, final @NotNull ClassLoader parent) {
    super(name, new URL[0], parent);
  }

  public void transformer(final @NotNull Transformer transformer) {
    this.transformers.add(transformer);
  }

  @Override
  public void addURL(final @NotNull URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> findClass(final @NotNull String name) throws ClassNotFoundException {
    final String resourceName = name.replace('.', '/') + ".class";
    final URL resource = this.findResource(resourceName);

    if(resource == null) throw new ClassNotFoundException(name);

    byte[] bytes;
    CodeSource codeSource;
    try {
      bytes = this.loadBytes(resource);

      // Transform the class bytecode.
      for(final Transformer transformer : this.transformers) {
        bytes = transformer.transform(name, bytes);
      }

      final URL codeBase = this.resourceClasspath(resource, resourceName);
      codeSource = new CodeSource(codeBase, (Certificate[]) null);
    } catch(final Exception exception) {
      throw new RuntimeException(String.format("Unable to load class '%s' from '%s'.", name, resource), exception);
    }

    if(bytes == null) throw new ClassNotFoundException(name);

    final String packageName = name.substring(0, name.lastIndexOf('.'));
    @SuppressWarnings("deprecation") final Package packageInstance = this.getPackage(packageName);
    if(packageInstance == null) this.definePackage(packageName, null, null, null, null, null, null, null);

    return this.defineClass(name, bytes, 0, bytes.length, codeSource);
  }

  private URL resourceClasspath(final @NotNull URL resource, final @NotNull String name) {
    final URI location;
    final String path;
    try {
      location = resource.toURI();
      path = location.getPath();

      if(location.getScheme().equals("file")) {
        assert path.endsWith("/" + name);
        return new File(path.substring(0, path.length() - (name.length() + 1)))
          .toURI()
          .toURL();
      } else if(location.getScheme().equals("jar")) {
        final String schemeSpecificPart = location.getRawSchemeSpecificPart();
        int pos = schemeSpecificPart.indexOf("!");
        if(pos > 0) {
          assert schemeSpecificPart.substring(pos + 1).equals("/" + name);
          final URI jarFile = new URI(schemeSpecificPart.substring(0, pos));
          if(jarFile.getScheme().equals("file")) {
            return new File(jarFile.getPath()).toURI().toURL();
          }
        }
      }
    } catch(final Exception exception) {
      throw new RuntimeException("Unable to get classpath for '%s'.", exception);
    }

    throw new RuntimeException("Unable to get classpath for '%s'.");
  }

  private byte[] loadBytes(final @NotNull URL resource) throws IOException {
    try(final InputStream stream = resource.openStream()) {
      return stream.readAllBytes();
    }
  }

  public interface Transformer {
    byte[] transform(final @NotNull String className, final byte[] classBytes) throws IOException;
  }
}

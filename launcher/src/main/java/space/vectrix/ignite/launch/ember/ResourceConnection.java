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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.function.Function;
import java.util.jar.Manifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* package */ final class ResourceConnection implements AutoCloseable {
  private final URLConnection connection;
  private final InputStream stream;
  private final Function<URLConnection, Manifest> manifestFunction;
  private final Function<URLConnection, CodeSource> sourceFunction;

  /* package */ ResourceConnection(final @NotNull URL url,
                                   final @NotNull Function<@NotNull URLConnection, @Nullable Manifest> manifestLocator,
                                   final @NotNull Function<@NotNull URLConnection, @Nullable CodeSource> sourceLocator) throws IOException {
    this.connection = url.openConnection();
    this.stream = this.connection.getInputStream();
    this.manifestFunction = manifestLocator;
    this.sourceFunction = sourceLocator;
  }

  /* package */ int contentLength() {
    return this.connection.getContentLength();
  }

  /* package */ @NotNull InputStream stream() {
    return this.stream;
  }

  /* package */ @Nullable Manifest manifest() {
    return this.manifestFunction.apply(this.connection);
  }

  /* package */ @Nullable CodeSource source() {
    return this.sourceFunction.apply(this.connection);
  }

  @Override
  public void close() throws Exception {
    this.stream.close();
  }
}

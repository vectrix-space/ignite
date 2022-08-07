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
package space.vectrix.ignite.installer;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public final class InstallerLibraries {
  private static FileSystem FILE_SYSTEM;

  public static void installLibraries(final @NotNull Path targetDirectory) throws Exception {
    final URI installerPath = IgniteInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI();
    if(InstallerLibraries.FILE_SYSTEM == null) {
      InstallerLibraries.FILE_SYSTEM = FileSystems.newFileSystem(Path.of(installerPath));
    }

    final Path sourceDirectory = InstallerLibraries.FILE_SYSTEM.getPath("META-INF", "libraries");
    try(final Stream<Path> pathStream = Files.walk(sourceDirectory)) {
      for(final Path sourcePath : pathStream.filter(path -> path.toString().endsWith(".jar")).toList()) {
        final Path relative = sourceDirectory.relativize(sourcePath);
        final Path destination = targetDirectory.resolve(relative.toString());

        final InputStream stream = IgniteInstaller.class.getResourceAsStream("/META-INF/libraries/" + relative);
        if(stream == null) throw new IllegalStateException("Unable to stream library resource!");

        Files.createDirectories(destination.getParent());
        Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }
}

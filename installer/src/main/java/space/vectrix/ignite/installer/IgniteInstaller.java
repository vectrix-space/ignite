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
import org.tinylog.Logger;
import space.vectrix.ignite.installer.service.InstallProcessorService;
import space.vectrix.ignite.installer.service.ProcessorServiceHandler;
import space.vectrix.ignite.installer.util.JavaVersionChecker;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IgniteInstaller {
  public static void main(final @NotNull String[] args) throws Exception {
    JavaVersionChecker.check();
    InstallerTerminal.configure(args);
    new IgniteInstaller().run();
  }

  public IgniteInstaller() {}

  @SuppressWarnings("unchecked")
  public void run() {
    // install libraries
    final Path librariesDirectory = InstallerTerminal.PLATFORM_LIBRARIES;
    try {
      if(Files.notExists(librariesDirectory)) {
        Files.createDirectories(librariesDirectory);
      }

      InstallerLibraries.installLibraries(librariesDirectory);
    } catch(final Exception exception) {
      Logger.error(exception, "Failed to install libraries");
      System.exit(1);
    }

    // install target
    JarFile launchJar = null;
    try {
      final Path normalized = Paths.get(InstallerTerminal.INSTALL_JAR.toRealPath().toUri().toURL().toURI());
      launchJar = new JarFile(new File(normalized.toUri()));
    } catch(final Exception exception) {
      Logger.error(exception, "Failed to add installer jar to the system class loader");
      System.exit(1);
    }

    // install service
    try {
      final ProcessorServiceHandler serviceHandler = new ProcessorServiceHandler();
      final InstallProcessorService service;
      if(InstallerTerminal.PLATFORM_TYPE != null) {
        service = serviceHandler.findService(InstallerTerminal.PLATFORM_TYPE)
          .orElseThrow(() -> new RuntimeException(String.format("Unable to locate platform type '%s'!", InstallerTerminal.PLATFORM_TYPE)));
      } else {
        service = serviceHandler.findService(launchJar).orElse(null);
      }

      if(service != null) {
        Logger.info("Running '{}' installer service.", service.name());

        service.execute();
      } else {
        Logger.info("Running no installer service.");
      }
    } catch(final Exception exception) {
      Logger.error(exception, "Failed to run installer service");
      System.exit(1);
    }

    final Path[] transformablePaths = new Path[0];

    // load libraries
    try(final Stream<Path> libraryStream = Files.walk(librariesDirectory)) {
      final List<Path> libraryPaths = libraryStream
        .filter(path -> path.toString().endsWith(".jar"))
        .toList();

      Logger.info("Adding libraries: " + libraryPaths.stream().map(Path::toString).collect(Collectors.joining(";")));

      final ModuleFinder finder = ModuleFinder.of(libraryPaths.toArray(new Path[0]));
      final Configuration configuration = Configuration.resolve(finder, List.of(ModuleLayer.boot().configuration()), finder, finder.findAll().stream().map(ref -> ref.descriptor().name()).collect(Collectors.toSet()));
      final ModuleLayer layer = ModuleLayer.defineModulesWithOneLoader(configuration, List.of(ModuleLayer.boot()), ClassLoader.getSystemClassLoader()).layer();

      Logger.info("Adding modules: " + layer.modules().stream().map(Module::getName).collect(Collectors.joining(", ")));

      var serviceLoader = ServiceLoader.load(layer, BiConsumer.class);
      ((BiConsumer<String[], Path[]>) serviceLoader.findFirst().orElseThrow()).accept(
        InstallerTerminal.REMAINING_ARGS.toArray(new String[0]),
        transformablePaths
      );
    } catch(final Exception exception) {
      Logger.error(exception, "Failed to run bootstrap service");
      System.exit(1);
    }
  }
}

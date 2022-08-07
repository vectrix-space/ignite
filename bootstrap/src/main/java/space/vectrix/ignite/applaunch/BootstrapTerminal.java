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

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.applaunch.util.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class BootstrapTerminal {
  private static final OptionParser PARSER = new OptionParser();

  private static final ArgumentAcceptingOptionSpec<String> LAUNCH_TARGET_ARGUMENT = BootstrapTerminal.PARSER
    .accepts("launchTarget", "Launch Target")
    .withRequiredArg();

  private static final ArgumentAcceptingOptionSpec<Path> PLATFORM_DIRECTORY_ARGUMENT = BootstrapTerminal.PARSER
    .accepts("platformDirectory", "Platform Directory")
    .withRequiredArg()
    .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING))
    .defaultsTo(Paths.get("."));

  private static final ArgumentAcceptingOptionSpec<Path> PLATFORM_JAR_ARGUMENT = BootstrapTerminal.PARSER
    .accepts("platformJar", "Platform Jar")
    .withRequiredArg()
    .withValuesConvertedBy(new PathConverter(PathProperties.FILE_EXISTING))
    .defaultsTo(Paths.get("server.jar"));

  private static final ArgumentAcceptingOptionSpec<String> PLATFORM_CLASSPATH_ARGUMENT = BootstrapTerminal.PARSER
    .accepts("platformClasspath", "Platform Classpath")
    .withRequiredArg()
    .defaultsTo("org.bukkit.craftbukkit.Main");

  static {
    BootstrapTerminal.PARSER.allowsUnrecognizedOptions();
  }

  public static String[] RAW_ARGS;
  public static Path PLATFORM_DIRECTORY;
  public static Path PLATFORM_JAR;
  public static String PLATFORM_CLASSPATH;

  public static void configure(@NotNull String[] args) throws Exception {
    final OptionSet options = BootstrapTerminal.PARSER.parse(args);

    String launchTarget = options.valueOf(BootstrapTerminal.LAUNCH_TARGET_ARGUMENT);
    boolean serviceDefault = false;
    if(launchTarget == null) {
      launchTarget = Constants.IGNITE_LAUNCH_SERVICE;
      serviceDefault = true;
    }

    if(serviceDefault) {
      final int size = args.length;
      args = Arrays.copyOf(args, args.length + 2);
      args[size] = "--launchTarget";
      args[size + 1] = launchTarget;
    }

    BootstrapTerminal.PLATFORM_DIRECTORY = options.valueOf(BootstrapTerminal.PLATFORM_DIRECTORY_ARGUMENT);
    BootstrapTerminal.PLATFORM_JAR = BootstrapTerminal.PLATFORM_DIRECTORY.resolve(options.valueOf(BootstrapTerminal.PLATFORM_JAR_ARGUMENT));
    BootstrapTerminal.PLATFORM_CLASSPATH = options.valueOf(BootstrapTerminal.PLATFORM_CLASSPATH_ARGUMENT);

    BootstrapTerminal.RAW_ARGS = args;
  }

  private BootstrapTerminal() {
    throw new AssertionError("Attempted to instantiate a class that is non-instantiable");
  }
}

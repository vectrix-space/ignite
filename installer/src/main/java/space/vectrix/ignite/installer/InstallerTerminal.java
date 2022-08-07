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

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public final class InstallerTerminal {
  private static final OptionParser PARSER = new OptionParser();

  private static final ArgumentAcceptingOptionSpec<Path> INSTALL_DIRECTORY_ARGUMENT = InstallerTerminal.PARSER
    .accepts("installDirectory", "Install Directory")
    .withRequiredArg()
    .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING))
    .defaultsTo(Paths.get("."));

  private static final ArgumentAcceptingOptionSpec<Path> INSTALL_JAR_ARGUMENT = InstallerTerminal.PARSER
    .accepts("installJar", "Install Jar")
    .withRequiredArg()
    .withValuesConvertedBy(new PathConverter(PathProperties.FILE_EXISTING))
    .defaultsTo(Paths.get("server.jar"));

  private static final ArgumentAcceptingOptionSpec<Path> PLATFORM_LIBRARIES_ARGUMENT = InstallerTerminal.PARSER
    .accepts("platformLibraries", "Platform Libraries")
    .withRequiredArg()
    .withValuesConvertedBy(new PathConverter())
    .defaultsTo(Paths.get("libraries"));

  private static final ArgumentAcceptingOptionSpec<String> PLATFORM_TYPE_ARGUMENT = InstallerTerminal.PARSER
    .accepts("platformType", "Platform Type")
    .withRequiredArg();

  private static final NonOptionArgumentSpec<String> REMAINING_ARGUMENTS = InstallerTerminal.PARSER
    .nonOptions()
    .ofType(String.class);

  static {
    InstallerTerminal.PARSER.allowsUnrecognizedOptions();
  }

  public static List<String> REMAINING_ARGS;
  public static Path INSTALL_DIRECTORY;
  public static Path INSTALL_JAR;
  public static Path PLATFORM_LIBRARIES;
  public static String PLATFORM_TYPE;

  public static void configure(final @NotNull String[] args) {
    final OptionSet options = InstallerTerminal.PARSER.parse(args);

    InstallerTerminal.INSTALL_DIRECTORY = options.valueOf(InstallerTerminal.INSTALL_DIRECTORY_ARGUMENT);
    InstallerTerminal.INSTALL_JAR = options.valueOf(InstallerTerminal.INSTALL_JAR_ARGUMENT);
    InstallerTerminal.PLATFORM_LIBRARIES = options.valueOf(InstallerTerminal.PLATFORM_LIBRARIES_ARGUMENT);
    InstallerTerminal.PLATFORM_TYPE = options.valueOf(InstallerTerminal.PLATFORM_TYPE_ARGUMENT);

    InstallerTerminal.REMAINING_ARGS = Collections.unmodifiableList(options.valuesOf(InstallerTerminal.REMAINING_ARGUMENTS));
  }
}

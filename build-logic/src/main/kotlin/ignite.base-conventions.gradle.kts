import com.diffplug.gradle.spotless.FormatExtension
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.regex.Pattern
import java.util.stream.Collectors

plugins {
  `java-library`
  id("checkstyle")
  id("com.diffplug.spotless")
}

// Expose version catalog
val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

group = rootProject.group
version = rootProject.version

java {
  javaTarget(8)
  withSourcesJar()
}

dependencies {
  compileOnly(libs.jetbrains.annotations)

  checkstyle(libs.stylecheck)
}

spotless {
  fun FormatExtension.applyCommon() {
    trimTrailingWhitespace()
    endWithNewline()
    indentWithSpaces(2)
  }

  fun formatLicense(): String {
    val splitPattern = Pattern.compile("\r?\n")
    val lineSeparator = System.lineSeparator()
    val headerPrefix = "/*$lineSeparator"
    val linePrefix = " * "
    val headerSuffix = "$lineSeparator */"

    val headerText = String(Files.readAllBytes(rootProject.file("license_header.txt").toPath()), StandardCharsets.UTF_8)

    return splitPattern.splitAsStream(headerText)
      .map {
        StringBuilder(it.length + 4)
          .append(linePrefix)
          .append(it)
          .toString()
      }
      .collect(Collectors.joining(
        lineSeparator,
        headerPrefix,
        headerSuffix
      ))
  }


  java {
    importOrderFile(rootProject.file(".spotless/ignite.importorder"))
    licenseHeader(formatLicense())
    applyCommon()
  }

  kotlin {
    applyCommon()
  }
}

val configPath: File = rootProject.file(".checkstyle")

checkstyle {
  toolVersion = libs.stylecheck.get().toString()
  configDirectory.set(configPath)

  setConfigProperties(
    "configDirectory" to configPath,
    "severity" to "error"
  )
}

tasks {
  javadoc {
    val minimalOptions: MinimalJavadocOptions = options
    options.encoding("UTF-8")

    if (minimalOptions is StandardJavadocDocletOptions) {
      val options: StandardJavadocDocletOptions = minimalOptions
      options.addStringOption("Xdoclint:none", "-quiet")
    }
  }

  compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
      "-nowarn",
      "-Xlint:-unchecked",
      "-Xlint:-deprecation"
    ))
  }
}

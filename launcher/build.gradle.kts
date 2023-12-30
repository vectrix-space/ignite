import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.launcher-conventions")
}

dependencies {
  implementation(project(":ignite-api"))

  // Logging

  implementation("net.minecrell:terminalconsoleappender:1.3.0")
  implementation("org.apache.logging.log4j:log4j-core:2.22.1")
  implementation("org.jline:jline-terminal:3.25.0")
  implementation("org.jline:jline-reader:3.25.0")
  implementation("org.jline:jline-terminal-jansi:3.25.0")

  // Configuration

  implementation("org.spongepowered:configurate-hocon:4.2.0-SNAPSHOT")
  implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
  implementation("org.spongepowered:configurate-gson:4.2.0-SNAPSHOT")

  // Common

  implementation("com.google.guava:guava:33.0.0-jre") { // 21.0 -> 22.0
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  implementation("com.google.errorprone:error_prone_annotations:2.18.0")

  // Event

  implementation("net.kyori:event-api:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  implementation("net.kyori:event-method-asm:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  // Transformation

  implementation("net.fabricmc:access-widener:2.1.0")
  implementation("net.fabricmc:sponge-mixin:0.12.5+mixin.0.8.5") {
    exclude(group = "org.ow2.asm")
  }

  // Launcher

  implementation("cpw.mods:modlauncher:8.1.3") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  implementation("cpw.mods:modlauncher:8.1.3:api") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  implementation("cpw.mods:grossjava9hacks:1.3.3")
}

tasks.named<ShadowJar>("shadowJar") {
  configureRelocations()
  configureExcludes()
}

fun ShadowJar.configureRelocations() {
  relocate("com.google.common", "space.vectrix.ignite.libs.google.common")
  relocate("net.kyori", "space.vectrix.ignite.libs.kyori")
  relocate("org.spongepowered.configurate.gson", "space.vectrix.ignite.libs.configurate.gson")
  relocate("org.spongepowered.configurate.hocon", "space.vectrix.ignite.libs.configurate.hocon")
  relocate("org.spongepowered.configurate.yaml", "space.vectrix.ignite.libs.configurate.yaml")
  relocate("org.yaml.snakeyaml", "space.vectrix.ignite.libs.snakeyaml")
  relocate("com.typesafe.config", "space.vectrix.ignite.libs.typesafe")
}

fun ShadowJar.configureExcludes() {
  // Guava - Only need a few things.
  exclude("com/google/common/escape/*")
  exclude("com/google/common/eventbus/*")
  exclude("com/google/common/html/*")
  exclude("com/google/common/net/*")
  exclude("com/google/common/xml/*")
  exclude("com/google/thirdparty/**")

  dependencies {
    // Checkerframework
    exclude(dependency("org.checkerframework:checker-qual"))

    // Google
    exclude(dependency("com.google.errorprone:error_prone_annotations"))
    exclude(dependency("com.google.j2objc:j2objc-annotations"))
  }
}

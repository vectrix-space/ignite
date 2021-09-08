import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.launcher-conventions")
}

dependencies {
  implementation(project(":ignite-api"))

  implementation("net.minecrell:terminalconsoleappender:1.3.0")
  implementation("org.jline:jline-terminal:3.20.0")
  implementation("org.jline:jline-reader:3.20.0")
  implementation("org.jline:jline-terminal-jansi:3.20.0")

  implementation("org.spongepowered:mixin:0.8.4") {
    exclude(group = "org.ow2.asm")
  }

  implementation("net.kyori:event-api:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  implementation("net.kyori:event-method-asm:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  implementation("org.quiltmc:access-widener:1.0.2")

  implementation("cpw.mods:modlauncher:8.0.9") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  implementation("cpw.mods:modlauncher:8.0.9:api") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  implementation("cpw.mods:grossjava9hacks:1.3.3")
}

tasks.named<ShadowJar>("shadowJar") {
  dependencies {
    include(project(":ignite-api"))

    include(dependency("org.apache.logging.log4j:log4j-api"))
    include(dependency("org.apache.logging.log4j:log4j-core"))
    include(dependency("net.minecrell:terminalconsoleappender"))
    include(dependency("org.jline:jline-reader"))
    include(dependency("org.jline:jline-terminal"))
    include(dependency("org.jline:jline-terminal-jansi"))
    include(dependency("org.fusesource.jansi:jansi"))

    include(dependency("org.spongepowered:configurate-core"))
    include(dependency("org.spongepowered:configurate-hocon"))
    include(dependency("org.spongepowered:configurate-yaml"))
    include(dependency("org.spongepowered:configurate-gson"))
    include(dependency("io.leangen.geantyref:geantyref"))
    include(dependency("com.typesafe:config"))
    include(dependency("com.google.code.gson:gson"))
    include(dependency("org.yaml:snakeyaml"))

    include(dependency("com.google.guava:guava"))
    include(dependency("com.google.guava:failureaccess"))
    include(dependency("com.google.inject:guice"))
    include(dependency("com.google.code.gson:gson"))
    include(dependency("javax.inject:javax.inject"))
    include(dependency("aopalliance:aopalliance"))

    include(dependency("net.kyori:event-api"))
    include(dependency("net.kyori:event-method"))
    include(dependency("net.kyori:event-method-asm"))

    include(dependency("org.ow2.asm:asm"))
    include(dependency("org.ow2.asm:asm-analysis"))
    include(dependency("org.ow2.asm:asm-commons"))
    include(dependency("org.ow2.asm:asm-tree"))
    include(dependency("org.ow2.asm:asm-util"))

    include(dependency("org.spongepowered:mixin"))

    include(dependency("org.quiltmc:access-widener"))

    include(dependency("cpw.mods:modlauncher"))
    include(dependency("cpw.mods:grossjava9hacks"))
    include(dependency("net.sf.jopt-simple:jopt-simple"))
  }
}

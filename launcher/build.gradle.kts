import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

apply<ShadowPlugin>()

dependencies {
  // API
  implementation(project(":ignite-api"))

  implementation("net.minecrell:terminalconsoleappender:1.2.0") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
  }

  // Event
  implementation("net.kyori:event-api:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.guava", module = "guava")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  implementation("net.kyori:event-method-asm:4.0.0-SNAPSHOT") {
    exclude(group = "org.ow2.asm", module = "asm")
  }

  // Access Transformers
  implementation("net.minecraftforge:accesstransformers:2.2.1:service") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  // Core
  implementation("cpw.mods:modlauncher:8.0.9") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation("cpw.mods:modlauncher:8.0.9:api") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation("cpw.mods:grossjava9hacks:1.3.0") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }
}

tasks {
  jar {
    manifest {
      attributes(
        "Main-Class" to "com.mineteria.ignite.applaunch.IgniteBootstrap",
        "Premain-Class" to "com.mineteria.ignite.applaunch.agent.Agent"
      )

      attributes("com/mineteria/ignite/applaunch/",
        "Specification-Title" to "Ignite",
        "Specification-Vendor" to "Mineteria",
        "Specification-Version" to 1.0,
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version.toString(),
        "Implementation-Vendor" to "Mineteria"
      )

      from({
        zipTree { configurations.runtimeClasspath.get().files.find { entry -> entry.name.contains("modlauncher") } }.matching { include("**/MANIFEST.MF") }.singleFile
      })
    }
  }

  named<ShadowJar>("shadowJar") {
    mergeServiceFiles()

    transform(Log4j2PluginsCacheFileTransformer())

    dependencies {
      // API
      include(project(":ignite-api"))

      // Logging
      include(dependency("org.apache.logging.log4j:log4j-api"))
      include(dependency("org.apache.logging.log4j:log4j-core"))
      include(dependency("org.checkerframework:checker-qual"))
      include(dependency("net.minecrell:terminalconsoleappender"))
      include(dependency("org.jline:jline-reader"))
      include(dependency("org.jline:jline-terminal"))
      include(dependency("org.jline:jline-terminal-jna"))
      include(dependency("net.java.dev.jna:jna"))

      // Configuration
      include(dependency("org.spongepowered:configurate-core"))
      include(dependency("org.spongepowered:configurate-hocon"))
      include(dependency("org.spongepowered:configurate-yaml"))
      include(dependency("org.spongepowered:configurate-gson"))
      include(dependency("com.typesafe:config"))
      include(dependency("com.google.code.gson:gson"))
      include(dependency("org.yaml:snakeyaml"))

      // Event
      include(dependency("net.kyori:event-api"))
      include(dependency("net.kyori:event-method"))
      include(dependency("net.kyori:event-method-asm"))

      // Common
      include(dependency("com.google.guava:guava"))
      include(dependency("com.google.guava:failureaccess"))
      include(dependency("com.google.inject:guice"))
      include(dependency("com.google.code.gson:gson"))
      include(dependency("javax.inject:javax.inject"))
      include(dependency("aopalliance:aopalliance"))

      // ASM
      include(dependency("org.ow2.asm:asm"))
      include(dependency("org.ow2.asm:asm-analysis"))
      include(dependency("org.ow2.asm:asm-commons"))
      include(dependency("org.ow2.asm:asm-tree"))
      include(dependency("org.ow2.asm:asm-util"))

      // Mixin
      include(dependency("org.spongepowered:mixin"))

      // Access Transformers
      include(dependency("net.minecraftforge:accesstransformers"))
      include(dependency("org.antlr:antlr4-runtime"))

      // Core
      include(dependency("cpw.mods:modlauncher"))
      include(dependency("cpw.mods:grossjava9hacks"))
      include(dependency("net.sf.jopt-simple:jopt-simple"))
    }
  }

  create<DefaultTask>("copyJarToTarget") {
    doLast {
      val shadowJar: ShadowJar = getByName<ShadowJar>("shadowJar")
      val targetJarDirectory: Path = projectDir.toPath().toAbsolutePath().resolve("../target")

      Files.createDirectories(targetJarDirectory)
      Files.copy(
        shadowJar.archiveFile.get().asFile.toPath().toAbsolutePath(),
        targetJarDirectory.resolve(shadowJar.archiveBaseName.get() + ".jar"),
        StandardCopyOption.REPLACE_EXISTING
      )
    }
  }

  build {
    dependsOn("copyJarToTarget")
  }
}

artifacts {
  archives(tasks.getByName<ShadowJar>("shadowJar"))
}

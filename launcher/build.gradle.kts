import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

apply(plugin = "com.github.johnrengelman.shadow")

dependencies {
  // API
  implementation(project(":ignite-api"))

  // Logger
  implementation("net.minecrell:terminalconsoleappender:1.3.0-SNAPSHOT")
  implementation("org.jline:jline-terminal:3.20.0")
  implementation("org.jline:jline-reader:3.20.0")
  implementation("org.jline:jline-terminal-jansi:3.20.0")

  // Event
  implementation("net.kyori:event-api:4.0.0-SNAPSHOT") {
    exclude(group = "com.google.guava", module = "guava")
    exclude(group = "org.checkerframework", module = "checker-qual")
  }

  implementation("net.kyori:event-method-asm:4.0.0-SNAPSHOT") {
    exclude(group = "org.ow2.asm:asm")
    exclude(group = "org.ow2.asm:asm-analysis")
    exclude(group = "org.ow2.asm:asm-commons")
    exclude(group = "org.ow2.asm:asm-tree")
    exclude(group = "org.ow2.asm:asm-util")
  }

  // Access Widener
  implementation("org.quiltmc:access-widener:1.0.2") {
    exclude(group = "org.apache.logging.log4j")
  }

  // Core
  implementation("cpw.mods:modlauncher:8.0.9") {
    exclude(group = "org.apache.logging.log4j")
    exclude(group = "net.sf.jopt-simple")
  }

  implementation("cpw.mods:modlauncher:8.0.9:api") {
    exclude(group = "org.apache.logging.log4j")
    exclude(group = "net.sf.jopt-simple")
  }

  implementation("cpw.mods:grossjava9hacks:1.3.3") {
    exclude(group = "org.apache.logging.log4j")
  }
}

val launcherJava9 by sourceSets.register("java9") {
  val main: SourceSet = sourceSets.main.get()

  this.java.setSrcDirs(setOf("src/java9"))
  compileClasspath += main.compileClasspath
  compileClasspath += main.runtimeClasspath

  tasks.named(compileJavaTaskName, JavaCompile::class) {
    options.release.set(9)
    if (JavaVersion.current() < JavaVersion.VERSION_11) {
      javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(11)) })
    }
  }

  dependencies.add(implementationConfigurationName, objects.fileCollection().from(main.output.classesDirs))
}

tasks {
  jar {
    manifest {
      attributes(
        "Premain-Class" to "space.vectrix.ignite.applaunch.agent.Agent",
        "Agent-Class" to "space.vectrix.ignite.applaunch.agent.Agent",
        "Launcher-Agent-Class" to "space.vectrix.ignite.applaunch.agent.Agent",
        "Main-Class" to "space.vectrix.ignite.applaunch.IgniteBootstrap",
        "Multi-Release" to true
      )

      attributes("space/vectrix/ignite/applaunch/",
        "Specification-Title" to "ignite",
        "Specification-Vendor" to "vectrix.space",
        "Specification-Version" to 1.0,
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version.toString(),
        "Implementation-Vendor" to "vectrix.space"
      )

      from({
        zipTree { configurations.runtimeClasspath.get().files.find { entry -> entry.name.contains("modlauncher") } }.matching { include("**/MANIFEST.MF") }.singleFile
      })
    }

    into("META-INF/versions/9/") {
      from(launcherJava9.output)
    }
  }

  named<ShadowJar>("shadowJar") {
    mergeServiceFiles()

    transform(Log4j2PluginsCacheFileTransformer())

    from(jar)

    dependencies {
      // API
      include(project(":ignite-api"))

      // Logging
      include(dependency("org.apache.logging.log4j:log4j-api"))
      include(dependency("org.apache.logging.log4j:log4j-core"))
      include(dependency("net.minecrell:terminalconsoleappender"))
      include(dependency("org.jline:jline-reader"))
      include(dependency("org.jline:jline-terminal"))
      include(dependency("org.jline:jline-terminal-jansi"))
      include(dependency("org.fusesource.jansi:jansi:2.3.2"))

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

      // Access Widener
      include(dependency("org.quiltmc:access-widener"))

      // Core
      include(dependency("cpw.mods:modlauncher"))
      include(dependency("cpw.mods:grossjava9hacks"))
      include(dependency("net.sf.jopt-simple:jopt-simple"))
    }

    exclude("META-INF/versions/*/module-info.class")
    exclude("module-info.class")
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

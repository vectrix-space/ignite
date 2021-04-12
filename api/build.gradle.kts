import de.marcphilipp.gradle.nexus.NexusPublishExtension

plugins {
  id("signing")
}

apply(plugin = "net.kyori.indra.publishing")
apply(plugin = "de.marcphilipp.nexus-publish")

dependencies {
  // API
  api("org.checkerframework:checker-qual:3.9.1")
  api("org.apache.logging.log4j:log4j-api:2.8.1")

  // Configuration
  api("org.spongepowered:configurate-hocon:3.7.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  api("org.spongepowered:configurate-yaml:3.7.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  api("org.spongepowered:configurate-gson:3.7.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  // Common
  api("com.google.guava:guava:21.0") {
    exclude(group = "com.google.code.findbugs", module = "jsr305") // We don't want to use jsr305, use checkerframework
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.j2objc", module = "j2objc-annotations")
    exclude(group = "org.codehaus.mojo", module = "animal-sniffer-annotations")
  }

  api("com.google.errorprone:error_prone_annotations:2.0.15") // Keeps guava happy

  api("com.google.inject:guice:4.2.0") {
    exclude(group = "com.google.code.findbugs", module = "jsr305") // We don't want to use jsr305, use checkerframework
    exclude(group = "com.google.guava", module = "guava")
  }

  api("com.google.code.gson:gson:2.8.6")

  // Mixins
  api("org.spongepowered:mixin:0.8.2")

  // ASM
  api("org.ow2.asm:asm:7.2")
  api("org.ow2.asm:asm-analysis:7.2")
  api("org.ow2.asm:asm-commons:7.2")
  api("org.ow2.asm:asm-tree:9.1")
  api("org.ow2.asm:asm-util:7.2")

  // Access Transformers
  api("net.minecraftforge:accesstransformers:2.2.1") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
    exclude(group = "cpw.mods", module = "grossjava9hacks")
    exclude(group = "cpw.mods", module = "modlauncher")
  }
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.ignite"
  )
}

signing {
  val signingKey: String? by project
  val signingPassword: String? by project
  useInMemoryPgpKeys(signingKey, signingPassword)
  sign(configurations.archives.get())
}

indra {
  extensions.configure<NexusPublishExtension> {
    repositories.sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }

  configurePublications {
    pom {
      developers {
        developer {
          id.set("VectrixDevelops")
          name.set("Vectrix")
        }
      }
    }
  }
}

tasks.withType<PublishToMavenRepository>().configureEach {
  onlyIf {
    val version: String = project.version.toString()
    System.getenv("CI") == null || version.endsWith("-SNAPSHOT")
  }
}

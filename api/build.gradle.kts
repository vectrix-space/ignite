import de.marcphilipp.gradle.nexus.NexusPublishExtension

plugins {
  id("signing")
}

apply(plugin = "net.kyori.indra.publishing")
apply(plugin = "de.marcphilipp.nexus-publish")

dependencies {
  // API
  api("org.checkerframework:checker-qual:3.16.0")
  api("org.apache.logging.log4j:log4j-api:2.8.1")

  // Configuration
  api("org.spongepowered:configurate-hocon:4.1.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  api("org.spongepowered:configurate-yaml:4.1.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  api("org.spongepowered:configurate-gson:4.1.1") {
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.guava", module = "guava") // We use our own version
    exclude(group = "com.google.inject", module = "guice") // We use our own version
  }

  // Common

  // Note: While the game usually uses 21.0, it should be fine
  //       to bump it to 22.0 for the additional features.
  api("com.google.guava:guava:22.0") {
    exclude(group = "com.google.code.findbugs", module = "jsr305") // We don't want to use jsr305, use checkerframework
    exclude(group = "org.checkerframework", module = "checker-qual") // We use our own version
    exclude(group = "com.google.j2objc", module = "j2objc-annotations")
    exclude(group = "org.codehaus.mojo", module = "animal-sniffer-annotations")
  }

  api("com.google.errorprone:error_prone_annotations:2.0.15") // Keeps guava happy

  api("com.google.inject:guice:5.0.1") {
    exclude(group = "com.google.code.findbugs", module = "jsr305") // We don't want to use jsr305, use checkerframework
    exclude(group = "com.google.guava", module = "guava")
    exclude(group = "org.ow2.asm:asm")
  }

  // Note: While the game usually uses 2.8.0, it should be fine
  //       to bump it to 2.8.7 for the additional features.
  api("com.google.code.gson:gson:2.8.7")

  // Mixins
  api("org.spongepowered:mixin:0.8.3") {
    exclude(group = "org.ow2.asm")
  }

  // ASM
  api("org.ow2.asm:asm:9.2")
  api("org.ow2.asm:asm-analysis:9.2")
  api("org.ow2.asm:asm-commons:9.2")
  api("org.ow2.asm:asm-tree:9.2")
  api("org.ow2.asm:asm-util:9.2")
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

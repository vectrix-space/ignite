import net.kyori.indra.IndraPlugin
import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.sonatype.IndraSonatypePublishingPlugin
import net.kyori.indra.sonatypeSnapshots

plugins {
  id("java")
  id("java-library")
  id("net.kyori.indra") version "1.3.1"
  id("net.kyori.indra.license-header") version "1.3.1"
  id("net.kyori.indra.publishing.sonatype") version "1.3.1"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<IndraPlugin>()
  apply<IndraLicenseHeaderPlugin>()
  apply<IndraSonatypePublishingPlugin>()

  group = "com.mineteria.ignite"
  version = "0.3.0-SNAPSHOT"
  description = "Bootstraps the Minecraft Server with ModLauncher to apply Mixins and Access Transformers from mods."

  repositories {
    mavenCentral()
    sonatypeSnapshots()
    maven {
      url = uri("https://repo.spongepowered.org/maven/")
    }
    maven {
      url = uri("https://files.minecraftforge.net/maven/")
    }
  }

  indra {
    javaVersions {
      target.set(8)
    }

    mitLicense()

    github("Mineteria-Development", "Ignite")
  }
}

plugins {
  id("net.kyori.indra") version "1.3.1"
  id("net.kyori.indra.publishing") version "1.3.1" apply false
  id("net.kyori.indra.license-header") version "1.3.1" apply false
  id("de.marcphilipp.nexus-publish") version "0.4.0" apply false
  id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}

group = "space.vectrix.ignite"
version = "0.3.1-SNAPSHOT"
description = "Bootstraps the Minecraft Server with ModLauncher to apply Mixins and Access Transformers from mods."

subprojects {
  apply(plugin = "net.kyori.indra")
  apply(plugin = "net.kyori.indra.license-header")

  group = rootProject.group
  version = rootProject.version
  description = rootProject.description

  repositories {
    mavenLocal()
    mavenCentral()
    maven {
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
      url = uri("https://repo.spongepowered.org/maven/")
    }
    maven {
      url = uri("https://files.minecraftforge.net/maven/")
    }
  }

  indra {
    github("vectrix-space", "ignite") {
      ci = true
    }

    mitLicense()
  }
}

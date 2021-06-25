plugins {
  id("net.kyori.indra") version "2.0.5"
  id("net.kyori.indra.publishing") version "2.0.5" apply false
  id("net.kyori.indra.license-header") version "2.0.5" apply false
  id("de.marcphilipp.nexus-publish") version "0.4.0" apply false
  id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}

group = "space.vectrix.ignite"
version = "0.5.0"
description = "Bootstraps the Minecraft Server with ModLauncher to apply Mixins and Access Wideners from mods."

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
    maven {
      url = uri("https://maven.quiltmc.org/repository/release/")
    }
  }

  indra {
    github("vectrix-space", "ignite") {
      ci(true)
    }

    mitLicense()
  }
}

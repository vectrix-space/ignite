plugins {
  id("net.kyori.indra")
  id("net.kyori.indra.license-header")
}

repositories {
  mavenLocal()
  mavenCentral()
  maven {
    url = uri("https://oss.sonatype.org/content/groups/public/")
  }
  maven {
    // For modlauncher
    name = "forge"
    url = uri("https://files.minecraftforge.net/maven")
  }
}

indra {
  github("vectrix-space", "ignite") {
    ci(true)
  }

  javaVersions {
    target(16)
  }

  mitLicense()
}

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
}

indra {
  github("vectrix-space", "ignite") {
    ci(true)
  }

  mitLicense()
}

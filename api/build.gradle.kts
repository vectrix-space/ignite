plugins {
  id("ignite.api-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.18.1")

  // Logging

  api("org.apache.logging.log4j:log4j-api:2.14.1")
  api("org.apache.logging.log4j:log4j-core:2.14.1")

  // Configuration

  api("org.spongepowered:configurate-core:4.1.2")
  api("org.spongepowered:configurate-hocon:4.1.2")
  api("org.spongepowered:configurate-yaml:4.1.2")
  api("org.spongepowered:configurate-gson:4.1.2")

  api("com.google.inject:guice:5.0.1") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "com.google.guava", module = "guava")
  }

  // Minecraft

  api("com.google.code.gson:gson:2.8.8") // 2.8.0 -> 2.8.8
}

applyJarMetadata("space.vectrix.ignite")
repositories {
  mavenCentral()
}

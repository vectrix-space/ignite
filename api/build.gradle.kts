plugins {
  id("ignite.api-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.23.0")

  // Logging

  api("org.apache.logging.log4j:log4j-api:2.19.0")

  // Configuration

  api("org.spongepowered:configurate-core:4.1.2")

  api("com.google.inject:guice:5.1.0") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "com.google.guava", module = "guava")
  }

  // Transformation

  api("org.ow2.asm:asm:9.2")
  api("org.ow2.asm:asm-analysis:9.2")
  api("org.ow2.asm:asm-commons:9.2")
  api("org.ow2.asm:asm-tree:9.2")
  api("org.ow2.asm:asm-util:9.2")

  // Minecraft

  api("com.google.code.gson:gson:2.8.9") // 2.8.0 -> 2.8.9
}

applyJarMetadata("space.vectrix.ignite")
repositories {
  mavenCentral()
}

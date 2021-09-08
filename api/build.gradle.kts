plugins {
  id("ignite.api-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.18.0")

  api("org.apache.logging.log4j:log4j-api:2.14.1")
  api("org.apache.logging.log4j:log4j-core:2.14.1")

  api("org.spongepowered:configurate-core:4.1.2")
  api("org.spongepowered:configurate-hocon:4.1.2")
  api("org.spongepowered:configurate-yaml:4.1.2")
  api("org.spongepowered:configurate-gson:4.1.2")

  api("com.google.inject:guice:5.0.1") {
    exclude(group = "com.google.code.findbugs", module = "jsr305")
    exclude(group = "com.google.guava", module = "guava")
  }

  api("org.ow2.asm:asm:9.2")
  api("org.ow2.asm:asm-analysis:9.2")
  api("org.ow2.asm:asm-commons:9.2")
  api("org.ow2.asm:asm-tree:9.2")
  api("org.ow2.asm:asm-util:9.2")

  // Minecraft

  api("com.google.guava:guava:22.0") { // 21.0 -> 22.0
    exclude(group = "com.google.code.findbugs", module = "jsr305")
  }

  api("com.google.errorprone:error_prone_annotations:2.0.18")
  api("com.google.code.gson:gson:2.8.8") // 2.8.0 -> 2.8.8
}

applyJarMetadata("space.vectrix.ignite")

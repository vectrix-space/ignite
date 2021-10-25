plugins {
  id("ignite.base-conventions")
}

repositories {
  maven {
    url = uri("https://repo.spongepowered.org/maven/")
  }
}

dependencies {
  implementation(project(":ignite-api"))

  implementation("org.spongepowered:mixin:0.8.4")

  implementation("org.ow2.asm:asm:9.2")
  implementation("org.ow2.asm:asm-analysis:9.2")
  implementation("org.ow2.asm:asm-commons:9.2")
  implementation("org.ow2.asm:asm-tree:9.2")
  implementation("org.ow2.asm:asm-util:9.2")
}

applyJarMetadata("space.vectrix.example")

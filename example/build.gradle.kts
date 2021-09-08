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
}

applyJarMetadata("space.vectrix.example")

plugins {
  id("ignite.base-conventions")
}

repositories {
  maven {
    url = uri("https://repo.spongepowered.org/maven/")
  }

  maven {
    url = uri("https://papermc.io/repo/repository/maven-public/")
  }

}

dependencies {
  implementation(project(":ignite-api"))

  implementation("org.spongepowered:mixin:0.8.5")

  compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

}

applyJarMetadata("space.vectrix.example")

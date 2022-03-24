plugins {
  id("ignite.base-conventions")
}

repositories {
  maven {
    url = uri("https://maven.fabricmc.net/")
  }

  maven {
    url = uri("https://papermc.io/repo/repository/maven-public/")
  }

}

dependencies {
  implementation(project(":ignite-api"))

  implementation("net.fabricmc:sponge-mixin:0.11.2+mixin.0.8.5")

  compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

}

applyJarMetadata("space.vectrix.example")

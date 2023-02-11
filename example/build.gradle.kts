plugins {
  id("ignite.base-conventions")
  id("io.papermc.paperweight.userdev") version "1.5.0"
}

repositories {
  mavenLocal()
  mavenCentral()
  maven {
    url = uri("https://repo.spongepowered.org/maven/")
  }
  maven {
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

indra {
  javaVersions {
    target(17)
  }
}

dependencies {
  implementation(project(":ignite-api"))

  implementation("org.spongepowered:mixin:0.8.5")

  paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  // Add mixin flag to support ignite
  reobfJar {
    remapperArgs.add("--mixin")
  }
}

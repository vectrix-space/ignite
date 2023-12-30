plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("net.kyori", "indra-common", "3.1.3")
  implementation("gradle.plugin.com.github.johnrengelman", "shadow", "8.0.0")
  implementation("de.marcphilipp.gradle", "nexus-publish-plugin", "0.4.0")
}

plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("net.kyori", "indra-common", "2.0.6")
  implementation("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.0")
  implementation("de.marcphilipp.gradle", "nexus-publish-plugin", "0.4.0")
}

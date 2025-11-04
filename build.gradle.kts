plugins {
  alias(libs.plugins.indra.sonatype)
  alias(libs.plugins.nexusPublish)
}

// Project metadata is configured in gradle.properties

tasks.register("clean", Delete::class) {
  delete(rootProject.layout.buildDirectory)
}

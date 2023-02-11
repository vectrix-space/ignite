enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}

rootProject.name = "ignite-parent"

includeBuild("build-logic")

sequenceOf(
  "api",
  "launcher",
  "example"
).forEach {
  include("ignite-$it")
  project(":ignite-$it").projectDir = file(it)
}

pluginManagement {
  includeBuild("build-logic")

  repositories {
    gradlePluginPortal()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ignite-parent"

sequenceOf(
  "api",
  "bootstrap",
  "installer",
  "launcher"
).forEach {
  include("ignite-$it")
  project(":ignite-$it").projectDir = file(it)
}

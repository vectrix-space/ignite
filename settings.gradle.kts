pluginManagement {
  includeBuild("build-logic")
}

rootProject.name = "ignite-parent"

sequenceOf(
  "api",
  "launcher",
  "example"
).forEach {
  include("ignite-$it")
  project(":ignite-$it").projectDir = file(it)
}

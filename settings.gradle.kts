rootProject.name = "ignite-parent"

include("api")
include("launcher")
include("example")

listOf(
  "api",
  "launcher",
  "example"
).forEach {
  findProject(":$it")?.name = "ignite-$it"
}

rootProject.name = "ignite"

include("api", "launcher", "example")

findProject(":api")?.name = "ignite-api"
findProject(":launcher")?.name = "ignite-launcher"
findProject(":example")?.name = "example-mod"

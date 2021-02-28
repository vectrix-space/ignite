dependencies {
  // API
  api(project(":ignite-api"))
}

tasks.jar {
  manifest {
    attributes(
      "Specification-Title" to "example",
      "Specification-Vendor" to "Mineteria",
      "Specification-Version" to 1,
      "AT" to "example_at.cfg"
    )
  }
}

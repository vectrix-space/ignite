dependencies {
  // API
  api(project(":ignite-api"))
}

tasks.jar {
  manifest.attributes(
    "Specification-Title" to "example",
    "Specification-Vendor" to "vectrix.space",
    "Specification-Version" to 1
  )
}

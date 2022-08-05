rootProject.name = "ignite-build-logic"

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
  }

  versionCatalogs {
    register("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

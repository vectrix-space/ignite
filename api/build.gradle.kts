plugins {
  id("ignite.base")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
}

applyJarMetadata("space/vectrix/ignite/api/", "space.vectrix.ignite.api")

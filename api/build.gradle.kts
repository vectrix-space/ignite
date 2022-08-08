plugins {
  id("ignite.module")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
}

applyJarMetadata("space.vectrix.ignite.api")

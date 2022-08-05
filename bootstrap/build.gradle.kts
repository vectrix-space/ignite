plugins {
  id("ignite.base")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
  implementation(project(":ignite-api"))
  implementation(libs.joptsimple)
  implementation(libs.log4jApi)
  implementation(libs.log4jCore)
  implementation(libs.modlauncher) {
    exclude(group = "org.apache.logging.log4j")
    exclude(group = "net.sf.jopt-simple")
  }
}

applyJarMetadata("space.vectrix.ignite")

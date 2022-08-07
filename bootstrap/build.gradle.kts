plugins {
  id("ignite.implementation")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
  compileOnly(project(":ignite-api"))
  compileOnly(project(":ignite-service"))

  runtimeLibrary(libs.joptsimple)
  runtimeLibrary(libs.log4jApi)
  runtimeLibrary(libs.log4jCore)
  runtimeLibrary(libs.modlauncher)
  runtimeLibrary(libs.securejar)
  runtimeLibrary(libs.asm)
  runtimeLibrary(libs.asmCommon)
  runtimeLibrary(libs.asmAnalysis)
  runtimeLibrary(libs.asmTree)
  runtimeLibrary(libs.asmUtil)
}

applyJarMetadata("space/vectrix/ignite/applaunch/", "space.vectrix.ignite.applaunch")

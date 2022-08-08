plugins {
  id("ignite.module")
}


extraJavaModuleInfo {
  automaticModule("jopt-simple-5.0.4.jar", "jopt.simple")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
  compileOnlyApi(project(":ignite-api"))
  compileOnlyApi(project(":ignite-installer"))

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

applyJarMetadata("space.vectrix.ignite.bootstrap")

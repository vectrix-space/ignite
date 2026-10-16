plugins {
  id("ignite.base-conventions")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.licenser.spotless")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
  checkstyle(libs.stylecheck)
  compileOnlyApi(libs.jetbrains.annotations)
}

spotless {
  java {
    importOrderFile(rootProject.file(".spotless/vectrix.importorder"))
    applyCommon()
  }

  kotlin {
    applyCommon()
  }
}

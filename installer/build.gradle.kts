import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.implementation")
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
  compileOnly(project(":ignite-service"))

  shadow(libs.joptsimple)
  shadow(libs.tinylogApi)
  shadow(libs.tinylogCore)
}

tasks {
  named<ShadowJar>("shadowJar") {
    val relocations = setOf(
      "joptsimple"
    )

    dependencies {
      relocations.forEach { relocate(it, "space.vectrix.ignite.installer.relocated.$it") }

      exclude(dependency("org.codehaus.mojo:animal-sniffer-annotations"))
    }
  }
}

applyJarMetadata("space/vectrix/ignite/installer/","space.vectrix.ignite.installer")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gradle.kotlin.dsl.accessors._e50bf244a9a17e691fa9e8ed86ba2cf9.runtimeClasspath

plugins {
  id("ignite.base")
  id("com.github.johnrengelman.shadow")
}

val runtimeLibrary = configurations.register("runtimeLibrary")

sourceSets.named("main") {
  configurations.named(implementationConfigurationName) {
    extendsFrom(runtimeLibrary.get())
  }
}

tasks {
  val jar = named<Jar>("jar") {
    archiveClassifier.set("base")

    // Package the extra libraries into the jar here.
    val runtimeLibraries = runtimeLibrary.get();
    runtimeLibraries.dependencies.map { dependency ->
      val path = "META-INF/libraries/" + dependency.group!!.replace('.', '/') + "/" + dependency.name + "/" + dependency.version
      val name = dependency.name + "-" + dependency.version + ".jar"

      into(path) {
        from(runtimeLibraries.files(dependency).filter { file -> file.name.equals(name) })
      }
    }

    from(project.rootProject.file("LICENSE"))
  }

  val shadowJar = named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    configurations = listOf(project.configurations.getByName("shadow"))

    from(jar.get().archiveFile)

    exclude("module-info.class")
  }

  named("build") {
    dependsOn(shadowJar)
  }
}

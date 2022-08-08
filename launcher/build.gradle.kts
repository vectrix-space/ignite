import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.base")
  id("com.github.johnrengelman.shadow")
}

val modularProjects = setOf(
  rootProject.projects.igniteApi,
  rootProject.projects.igniteBootstrap
).map { it.dependencyProject }

val shadedProjects = setOf(
  rootProject.projects.igniteInstaller
).map { it.dependencyProject }

tasks {
  val jar = named<Jar>("jar") {
    archiveClassifier.set("base")

    modularProjects.forEach { project ->
      val runtimeLibrary = project.configurations.named("runtimeLibrary")

      // Package the extra libraries into the jar here.
      val runtimeLibraries = runtimeLibrary.get();
      runtimeLibraries.dependencies.map { dependency ->
        val path = "META-INF/libraries/" + dependency.group!!.replace('.', '/') + "/" + dependency.name + "/" + dependency.version
        val name = dependency.name + "-" + dependency.version + ".jar"

        into(path) {
          from(runtimeLibraries.files(dependency).filter { file -> file.name.equals(name) })
        }
      }

      // Package the project into the jar here too.
      val path = "META-INF/libraries/" + rootProject.group.toString().replace('.', '/') + "/" + project.name + "/" + rootProject.version.toString()

      into(path) {
        from(project.tasks.named<Jar>("jar").map { projectJar -> projectJar.archiveFile })
      }
    }

    from(project.rootProject.file("license.txt"))
  }

  val shadowJar = named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    archiveFileName.set("ignite-${rootProject.version}.jar")
    destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(jar.get().archiveFile)

    manifest {
      attributes(
        "Main-Class" to "space.vectrix.ignite.installer.IgniteInstaller",
        "Multi-Release" to true
      )
    }

    shadedProjects.forEach { project ->
      val shadowJarTask = project.tasks.named<ShadowJar>("shadowJar").get()
      dependsOn(project.tasks.withType<Jar>())
      dependsOn(shadowJarTask)
      from(shadowJarTask.archiveFile)
    }
  }

  named("build") {
    dependsOn(shadowJar)
  }
}

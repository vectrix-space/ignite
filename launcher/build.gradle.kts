import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.base")
  id("com.github.johnrengelman.shadow")
}

val apiProjects = setOf(
  rootProject.projects.igniteApi,
  rootProject.projects.igniteService
).map { it.dependencyProject }

val implementationProjects = setOf(
  rootProject.projects.igniteBootstrap,
  rootProject.projects.igniteInstaller
).map { it.dependencyProject }

tasks {
  val shadowJar = named<ShadowJar>("shadowJar") {
    mergeServiceFiles()

    archiveClassifier.set("")
    archiveFileName.set("ignite-${rootProject.version}.jar")
    destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
      attributes(
        "Premain-Class" to "space.vectrix.ignite.installer.agent.Agent",
        "Agent-Class" to "space.vectrix.ignite.installer.agent.Agent",
        "Launcher-Agent-Class" to "space.vectrix.ignite.installer.agent.Agent",
        "Main-Class" to "space.vectrix.ignite.installer.IgniteInstaller",
        "Multi-Release" to true
      )

      apiProjects.forEach { project ->
        val jarTask = project.tasks.named<Jar>("jar").get()
        dependsOn(jarTask)

        from({
          zipTree { jarTask.archiveFile }.matching { include("**/MANIFEST.MF") }.singleFile
        })
      }

      implementationProjects.forEach { project ->
        val shadowJarTask = project.tasks.named<ShadowJar>("shadowJar").get()
        dependsOn(shadowJarTask)
        dependsOn(project.tasks.withType<Jar>())

        from({
          zipTree { shadowJarTask.archiveFile }.matching { include("**/MANIFEST.MF") }.singleFile
        })
      }
    }

    apiProjects.forEach { project ->
      val jarTask = project.tasks.named<Jar>("jar").get()
      dependsOn(jarTask)
      from(jarTask.archiveFile)
    }

    implementationProjects.forEach { project ->
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

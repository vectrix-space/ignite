import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.base")
  id("com.github.johnrengelman.shadow")
}

tasks {
  val jar = named<Jar>("jar") {
    archiveClassifier.set("base")

    from(project.rootProject.file("license.txt"))
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

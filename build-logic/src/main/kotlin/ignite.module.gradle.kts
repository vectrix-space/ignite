plugins {
  id("ignite.base")
  id("de.jjohannes.extra-java-module-info")
}

val runtimeLibrary: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = true
}

sourceSets.named("main") {
  configurations.named(implementationConfigurationName) {
    extendsFrom(runtimeLibrary)
  }
}

extraJavaModuleInfo {
  failOnMissingModuleInfo.set(false)
}

val jar = tasks.named<Jar>("jar") {
  archiveClassifier.set("")

  from(project.rootProject.file("LICENSE"))
}

artifacts {
  add("runtimeLibrary", jar.get())
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("ignite.base-conventions")
  id("com.github.johnrengelman.shadow")
}

// Expose version catalog
val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

val implementationVersion = project.version.toString()
val regexPattern = """(\d+\.\d+)""".toRegex()
val apiVersion = regexPattern.find(implementationVersion)?.value

tasks.getByName<Jar>("jar") {
  manifest {
    attributes(
      "Premain-Class" to "space.vectrix.ignite.agent.IgniteAgent",
      "Agent-Class" to "space.vectrix.ignite.agent.IgniteAgent",
      "Launcher-Agent-Class" to "space.vectrix.ignite.agent.IgniteAgent",
      "Main-Class" to "space.vectrix.ignite.IgniteBootstrap",
      "Multi-Release" to true,

      "Specification-Title" to "ignite",
      "Specification-Version" to apiVersion,
      "Specification-Vendor" to "vectrix.space",

      "Implementation-Title" to project.name,
      "Implementation-Version" to implementationVersion,
      "Implementation-Vendor" to "vectrix.space"
    )

    attributes(
      "org/objectweb/asm/",
      "Implementation-Version" to libs.versions.asm
    )
  }
}

tasks.getByName<ShadowJar>("shadowJar") {
  mergeServiceFiles()

  relocate("com.google.gson", "space.vectrix.ignite.libs.gson")
  relocate("net.fabricmc.accesswidener", "space.vectrix.ignite.libs.accesswidener")
  relocate("org.tinylog", "space.vectrix.ignite.libs.tinylog")
}

tasks.getByName("build") {
  dependsOn("shadowJar")
}

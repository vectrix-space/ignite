plugins {
  id("ignite.common-conventions")
  id("ignite.launcher-conventions")
}

dependencies {
  implementation(project(":ignite-api"))

  implementation(libs.tinylog.impl)

  implementation(libs.mixin) {
    exclude(group = "com.google.guava")
    exclude(group = "com.google.code.gson")
    exclude(group = "org.ow2.asm")
  }

  implementation(libs.mixinExtras) {
    exclude(group = "org.apache.commons")
  }

  implementation(libs.accessWidener)
  implementation(libs.asm)
  implementation(libs.asm.analysis)
  implementation(libs.asm.commons)
  implementation(libs.asm.tree)
  implementation(libs.asm.util)

  implementation(libs.gson)

  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)
}

val agentClasspathTest = sourceSets.create("agentClasspathTest") {
  compileClasspath += sourceSets.main.get().output
  runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

val agentClasspathCheck = tasks.register<JavaExec>("agentClasspathCheck") {
  group = "verification"
  description = "Checks classpath discovery after a real Java agent append."
  dependsOn(tasks.named(agentClasspathTest.classesTaskName), tasks.named("jar"))
  classpath = agentClasspathTest.runtimeClasspath
  mainClass.set("space.vectrix.ignite.util.AgentClasspathCheck")
  jvmArgs("-javaagent:${tasks.named<Jar>("jar").get().archiveFile.get().asFile.absolutePath}")
}

tasks.named("check") {
  dependsOn(agentClasspathCheck)
}

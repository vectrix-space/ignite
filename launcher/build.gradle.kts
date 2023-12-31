plugins {
  id("ignite.launcher-conventions")
}

dependencies {
  implementation(project(":ignite-api"))

  implementation(libs.tinylog.impl)

  implementation(libs.accessWidener)
  implementation(libs.asm)
  implementation(libs.asm.analysis)
  implementation(libs.asm.commons)
  implementation(libs.asm.tree)
  implementation(libs.asm.util)

  implementation(libs.gson)
}
